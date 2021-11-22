package com.cite306.project.dashboard.makeRecipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cite306.project.R;
import com.cite306.project.adapter.makeRecipe.MakeRecipeInfoAdapter;
import com.cite306.project.model.Ingredient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MakeRecipeInfoFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    private RelativeLayout makeRecipeInfoOpenView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private TextView recipeName, estimatedCost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.dashboard_makerecipe_fragment_info, container, false );
        final Bundle bundle = this.getArguments();

        final RelativeLayout closeRecipeInfo;
        final Button deleteRecipe;

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        makeRecipeInfoOpenView = view.findViewById( R.id.makeRecipeInfo_openView );
        progressBar = view.findViewById( R.id.makeRecipeInfo_progressBar );

        recyclerView = view.findViewById( R.id.makeRecipeInfo_recyclerView );
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );

        recipeName = view.findViewById( R.id.makeRecipeInfo_recipeName );
        estimatedCost = view.findViewById( R.id.makeRecipeInfo_estimatedCost );
        estimatedCost.setText( "0" );

        if ( bundle == null ) System.err.println( "WARNING! Something happened." );
        else getRecipes( bundle.getString( "recipe_name" ) );

        closeRecipeInfo = view.findViewById( R.id.makeRecipe_closeRecipeInfo );

        deleteRecipe = view.findViewById( R.id.makeRecipeInfo_deleteRecipe );

        closeRecipeInfo.setOnClickListener( v -> {
            final Fragment makeRecipeInfo = getActivity().getSupportFragmentManager().findFragmentByTag( "makeRecipeInfo" );

            if ( makeRecipeInfo == null ) return;
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fragment_slide_up, R.anim.fragment_slide_down ).remove( makeRecipeInfo ).commit();
        } );

        deleteRecipe.setOnClickListener( v -> deleteRecipe( bundle.getString( "recipe_name" ) ) );

        return view;
    }

    private void deleteRecipe( String recipe ) {
        database.collection( "users" )
                .document( mAuth.getUid() )
                .collection( "recipes" )
                .document( recipe )
                .delete()
                .addOnSuccessListener( unused -> {
                    final Fragment makeRecipeInfo = getActivity().getSupportFragmentManager().findFragmentByTag( "makeRecipeInfo" );
                    final Fragment makeRecipeFragment = getActivity().getSupportFragmentManager().findFragmentByTag( "makeRecipe" );

                    if ( makeRecipeInfo == null ) return;
                    getActivity().getSupportFragmentManager().beginTransaction().remove( makeRecipeFragment ).commit();
                    getActivity().getSupportFragmentManager().beginTransaction().add( R.id.makeRecipeActivity_fragment, new MakeRecipeFragment(), "makeRecipe" ).commit();

                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fragment_slide_up, R.anim.fragment_slide_down ).remove( makeRecipeInfo ).commit();
                } );
    }

    private void getRecipes( String recipeName ) {
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

                        this.recipeName.setText( recipeName );

                        final MakeRecipeInfoAdapter adapter = new MakeRecipeInfoAdapter( getContext(), recipeIngredients, MakeRecipeInfoFragment.this );

                        recyclerView.setAdapter( adapter );
                        estimatedCost.setText( String.format( Locale.US, "%.2f", adapter.getEstimatedCost() ) );
                        progressBar.setVisibility( View.INVISIBLE );
                        makeRecipeInfoOpenView.setVisibility( View.VISIBLE );
                    } catch ( Exception err ) {
                        err.printStackTrace();
                    }
                } );
    }
}
