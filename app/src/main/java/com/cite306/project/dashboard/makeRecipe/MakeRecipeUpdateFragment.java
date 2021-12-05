package com.cite306.project.dashboard.makeRecipe;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cite306.project.R;
import com.cite306.project.adapter.makeRecipe.AddedIngredientsListAdapter;
import com.cite306.project.adapter.makeRecipe.SearchedIngredientsAdapter;
import com.cite306.project.model.Ingredient;
import com.cite306.project.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MakeRecipeUpdateFragment extends Fragment {

    private Bundle bundle;

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    private AddedIngredientsListAdapter adapter;

    private RecyclerView searchedIngredientsList, addedIngredientsList;
    private ProgressBar searchedIngredientsListProgressBar;
    private EditText recipeNameField;

    private TextView estimatedCost;
    private Button saveRecipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.dashboard_makerecipe_fragment_update, container, false );
        bundle = this.getArguments();

        final RelativeLayout removeIngredient;
        final LinearLayout closeButton;

        final EditText searchField;

        removeIngredient = view.findViewById( R.id.makeRecipe_removeIngredient );
        closeButton = view.findViewById( R.id.makeRecipe_closeButton );

        searchField = view.findViewById( R.id.makeRecipe_searchField );

        saveRecipe = view.findViewById( R.id.makeRecipeUpdate_saveRecipe );

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        searchedIngredientsList = view.findViewById( R.id.makeRecipe_searchedIngredientsList );
        addedIngredientsList = view.findViewById( R.id.makeRecipe_addedIngredientsList );
        recipeNameField = view.findViewById( R.id.makeRecipe_recipeName );

        searchedIngredientsListProgressBar = view.findViewById( R.id.searchedIngredientsList_progressBar );

        searchedIngredientsList.setLayoutManager(  new LinearLayoutManager( getActivity(), LinearLayoutManager.HORIZONTAL, false ) );

        if ( bundle == null ) adapter = new AddedIngredientsListAdapter( getContext(), MakeRecipeUpdateFragment.this );
        else {
            adapter = new AddedIngredientsListAdapter( getContext(), MakeRecipeUpdateFragment.this );
            getSavedRecipe( bundle.getString( "recipe_name" ) );
            saveRecipe.setText( R.string.recipeMakeUpdate_updateButton );
        }

        addedIngredientsList.setLayoutManager( new LinearLayoutManager( getActivity() ) );
        addedIngredientsList.setAdapter( adapter );

        estimatedCost = view.findViewById( R.id.makeRecipe_estimatedCost );
        estimatedCost.setText( "0" );

        addedIngredientsList.setOnDragListener( ( v, event ) -> {
            final int action = event.getAction();
            if ( action != DragEvent.ACTION_DROP || event.getClipDescription().getLabel().toString().equals( "addedIngredient" ) ) return true;

            addNewIngredient( event.getClipDescription().getLabel().toString() );

            return false;
        } );

        removeIngredient.setOnDragListener( ( v, event ) -> {
            final int action = event.getAction();
            if ( action != DragEvent.ACTION_DROP || !event.getClipDescription().getLabel().toString().equals( "addedIngredient" ) ) return true;

            final String[] ingredientData = event.getClipData().getItemAt( 0 ).getText().toString().split( "\\|" );
            adapter.removeIngredient( Integer.parseInt( ingredientData[0] ) );
            addedIngredientsList.setAdapter( adapter );
            addedIngredientsList.smoothScrollToPosition( addedIngredientsList.getBottom() );

            return false;
        } );

        closeButton.setOnClickListener( v -> {
            final Fragment makeRecipeUpdateFragment = getActivity().getSupportFragmentManager().findFragmentByTag( "makeRecipeUpdate" );

            if ( makeRecipeUpdateFragment == null ) return;
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fragment_slide_up, R.anim.fragment_slide_down ).remove( makeRecipeUpdateFragment ).commit();
        } );

        searchField.setOnEditorActionListener( ( v, actionId, event ) -> {
            if ( actionId != EditorInfo.IME_ACTION_DONE ) return false;

            final String ingredientQuery = searchField.getText().toString().trim();

            searchedIngredientsListProgressBar.setVisibility( View.VISIBLE );

            try {
                searchedIngredientsList.setVisibility( View.INVISIBLE );
                searchForIngredients( ingredientQuery );
            } catch ( Exception err ) {
                System.err.println( "Something went wrong in onCreate() function inside MakeRecipeFragment.java!" );
                err.printStackTrace();
            }

            return false;
        } );

        saveRecipe.setOnClickListener( v -> saveRecipe() );

        return view;
    }

    private void closeRecipeAfterSave() {
        final Fragment makeRecipeUpdateFragment = getActivity().getSupportFragmentManager().findFragmentByTag( "makeRecipeUpdate" );
        final Fragment makeRecipeFragment = getActivity().getSupportFragmentManager().findFragmentByTag( "makeRecipe" );

        if ( makeRecipeUpdateFragment == null ) return;
        getActivity().getSupportFragmentManager().beginTransaction().remove( makeRecipeFragment ).commit();
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fragment_slide_up, R.anim.fragment_slide_down ).remove( makeRecipeUpdateFragment ).commit();

        getActivity().getSupportFragmentManager().beginTransaction().add( R.id.makeRecipeActivity_fragment, new MakeRecipeFragment(), "makeRecipe" ).commit();
    }

    private void addNewIngredient( String ingredientId ) {

        final String url = Util.spoonacularSpecificIngredientSearch.replace( "{id}", ingredientId );

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url( url )
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println( "Something went wrong with the OkHttp request, inside getIngredientData():" );
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if ( !response.isSuccessful() ) return;

                final String ingredientData = response.body().string();

                getActivity().runOnUiThread( () -> {
                    JSONObject res = null;

                    int id = 0;
                    String name = "";
                    String imageURL = "";
                    String aisle = "";
                    float cost = 0;
                    try {
                        res = new JSONObject( ingredientData );

                        id = res.getInt( "id" );
                        name = res.getString( "name" );
                        imageURL = res.getString( "image" );
                        aisle = res.getString( "aisle" );
                        cost = Float.parseFloat( res.getJSONObject( "estimatedCost" ).getString( "value" ) );
                    } catch ( Exception err ) {
                        err.printStackTrace();
                    }

                    if ( res == null ) return;

                    final Ingredient newIngredient = new Ingredient( id, name, imageURL, aisle, cost );

                    adapter.addNewIngredient( newIngredient );
                    addedIngredientsList.setAdapter( adapter );
                    addedIngredientsList.smoothScrollToPosition( addedIngredientsList.getBottom() );

                    estimatedCost.setText( String.format( Locale.US, "%.2f", adapter.getEstimatedCost() ) );
                } );

                response.body().close();
            }
        } );
    }

    private void getSavedRecipe( String recipeName ) {
        database.collection( "users" )
                .document( mAuth.getUid() )
                .collection( "recipes" )
                .document( recipeName )
                .get().addOnCompleteListener( task -> {
                    if ( !task.isSuccessful() ) return;

                    final Object response = task.getResult().getData();

                    JSONArray ingredientsJSON;
                    try {
                        ingredientsJSON = new JSONObject( new Gson().toJson( response ) ).getJSONArray( recipeName );
                        List< Ingredient > recipeIngredients = new ArrayList<>();

                        for ( int i = 0; i < ingredientsJSON.length(); i++ ) {
                            recipeIngredients.add( new Ingredient(
                                    ingredientsJSON.getJSONObject( i ).getInt( "id" ),
                                    ingredientsJSON.getJSONObject( i ).getString( "name" ),
                                    ingredientsJSON.getJSONObject( i ).getString( "imageURL" ).replace( "https://spoonacular.com/cdn/ingredients_500x500/", "" ),
                                    ingredientsJSON.getJSONObject( i ).getString( "aisle" ),
                                    Float.parseFloat( ingredientsJSON.getJSONObject( i ).getString( "cost" ) )
                            ) );
                        }

                        adapter.setAddedIngredients( recipeIngredients );
                        addedIngredientsList.setAdapter( adapter );
                        recipeNameField.setText( recipeName );
                        estimatedCost.setText( String.format( Locale.US, "%.2f", adapter.getEstimatedCost() ) );
                    } catch ( Exception err ) {
                        err.printStackTrace();
                    }
                } );
    }

    private void saveRecipe() {
        if ( recipeNameField.getText().toString().trim().isEmpty() ) return;

        final List< Ingredient > returnedIngredients = adapter.getAddedIngredients();

        Map< String, Object > savedIngredients = new HashMap<>();
        savedIngredients.put( recipeNameField.getText().toString(), returnedIngredients );

        if ( bundle == null ) {
            database.collection("users")
                    .document( mAuth.getUid() )
                    .collection("recipes")
                    .document( recipeNameField.getText().toString().trim() )
                    .set(savedIngredients)
                    .addOnCompleteListener( t -> System.out.println("Recipe has been added to database!"));

            showSnackbarMessage("Successfully added a new recipe.");
            closeRecipeAfterSave();
        } else if ( recipeNameField.getText().toString().trim().equals( bundle.getString( "recipe_name" ) ) ) {
            database.collection( "users" )
                    .document( mAuth.getUid() )
                    .collection( "recipes" )
                    .document( recipeNameField.getText().toString().trim() )
                    .set( savedIngredients )
                    .addOnCompleteListener( t -> System.out.println( "Recipe has been renamed and updated to database!" ) );

            showSnackbarMessage( "Successfully updated a recipe." );
            closeRecipeAfterSave();
        } else {
            final String savedRecipeName = bundle.getString( "recipe_name" );

            database.collection( "users" )
                    .document( mAuth.getUid() )
                    .collection( "recipes" )
                    .document( recipeNameField.getText().toString().trim() )
                    .get().addOnCompleteListener( task -> {
                        if ( task.getResult().exists() ) {
                            showSnackbarMessage( "Please use a different name." );
                        } else {
                            database.collection( "users" )
                                    .document( mAuth.getUid() )
                                    .collection( "recipes" )
                                    .document( savedRecipeName )
                                    .delete();

                            database.collection( "users" )
                                    .document( mAuth.getUid() )
                                    .collection( "recipes" )
                                    .document( recipeNameField.getText().toString().trim() )
                                    .set( savedIngredients )
                                    .addOnCompleteListener( t -> System.out.println( "Recipe has been renamed and updated to database!" ) );

                            showSnackbarMessage( "Successfully renamed and updated a recipe." );
                            closeRecipeAfterSave();
                        }
                    } );
        }
    }

    private void searchForIngredients( String query ) throws Exception {
        if ( query.isEmpty() ) return;

        final String cleanQuery = query.replaceAll( " +", " " );
        final URL url = new URL( Util.spoonacularIngredientSearchURL.replace( "{query}", cleanQuery ) );

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url( url )
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println( "Something went wrong with the OkHttp request, inside searchForIngredients():" );
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if ( !response.isSuccessful() ) return;

                final String ingredientQuery = response.body().string();

                getActivity().runOnUiThread( () -> {
                    List< Ingredient > ingredientsQueried = new ArrayList<>();

                    try {
                        final JSONArray json = new JSONObject( ingredientQuery ).getJSONArray( "results" );

                        for ( int i = 0; i < json.length(); i++ ) {
                            final JSONObject currentEntry = json.getJSONObject( i );
                            ingredientsQueried.add( new Ingredient( currentEntry.getInt( "id" ), currentEntry.getString( "name" ), currentEntry.getString( "image" ) ) );
                        }
                    } catch ( Exception err ) {
                        System.err.println( "Something went wrong inside searchForIngredients() function inside MakeRecipeFragment.java!" );
                        err.printStackTrace();
                    }

                    searchedIngredientsListProgressBar.setVisibility( View.INVISIBLE );

                    searchedIngredientsList.setAdapter( new SearchedIngredientsAdapter( getContext(), ingredientsQueried, MakeRecipeUpdateFragment.this ) );
                    searchedIngredientsList.setVisibility( View.VISIBLE );
                } );

                response.body().close();
            }
        } );
    }

    private void showSnackbarMessage( String message ) {
        final Snackbar notif = Snackbar.make( getView(), message, Snackbar.LENGTH_LONG );

        final View snackbarDesign = notif.getView();
        snackbarDesign.setBackground( ContextCompat.getDrawable( getContext(), R.drawable.toast_design ) );

        final TextView notifText = snackbarDesign.findViewById( com.google.android.material.R.id.snackbar_text );
        notifText.setTextColor( Color.parseColor( "#ffffff" ) );
        notifText.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
        notifText.setTextSize( 14 );

        notif.show();
    }
}
