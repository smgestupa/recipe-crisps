package com.cite306.project.dashboard.recipeSearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.cite306.project.R;
import com.cite306.project.adapter.recipeSearch.RecipeInfoAdapter;
import com.cite306.project.model.Ingredient;
import com.cite306.project.model.Recipe;
import com.cite306.project.util.Util;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeSearchInfoFragment extends Fragment {

    private ProgressBar recipeInfoProgressBar;
    private RelativeLayout recipeInfoView;
    private RecyclerView recipeInfoRecyclerView;

    private TextView recipeInfoName, recipeInfoServings, recipeInfoReadyIn, recipeInfoPricePerServing;
    private ImageView recipeInfoRecipeImage;

    private Recipe recipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.dashboard_recipesearch_fragment_info, container, false );
        final Bundle bundle = this.getArguments();

        final RelativeLayout closeRecipeInfo;

        recipeInfoProgressBar = view.findViewById( R.id.recipeInfo_progressBar );
        recipeInfoView = view.findViewById( R.id.recipeInfo_view );

        recipeInfoRecyclerView = view.findViewById( R.id.recipeInfo_recyclerView );
        recipeInfoRecyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );

        recipeInfoName = view.findViewById( R.id.recipeInfo_recipeName );
        recipeInfoServings = view.findViewById( R.id.recipeInfo_servings );
        recipeInfoReadyIn = view.findViewById( R.id.recipeInfo_readyIn );
        recipeInfoPricePerServing = view.findViewById( R.id.recipeInfo_pricePerServing );
        recipeInfoRecipeImage = view.findViewById( R.id.recipeInfo_recipeImage );

        closeRecipeInfo = view.findViewById( R.id.recipeInfo_closeRecipeInfo );

        if ( bundle != null ) getRecipeInformation( bundle.getInt( "recipe_id" ) );
        else closeRecipe();

        closeRecipeInfo.setOnClickListener( v -> closeRecipe() );
        return view;
    }

    private void closeRecipe() {
            final Fragment recipeInfoFragment = getActivity().getSupportFragmentManager().findFragmentByTag( "recipeInfo" );

            if ( recipeInfoFragment == null ) return;
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.fragment_slide_up, R.anim.fragment_slide_down ).remove( recipeInfoFragment ).commit();
    }

    private void getRecipeInformation( int id ) {
        final String recipeInformationURL = "https://api.spoonacular.com/recipes/" + id + "/information?includeNutrition=false&apiKey=" + Util.spoonacularAPI_Key;

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url( recipeInformationURL )
                .build();

        client.newCall( request ).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println( "Something went wrong with the OkHttp request, inside getRecipeInformation():" );
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if ( !response.isSuccessful() ) return;

                JSONObject recipeQuery = null;

                try {
                    recipeQuery = new JSONObject( response.body().string() );
                    final JSONArray extendedIngredients = recipeQuery.getJSONArray( "extendedIngredients" );

                    List< Ingredient > ingredients = new ArrayList<>();
                    for ( int i = 0; i < extendedIngredients.length(); i++ ) {
                        ingredients.add( new Ingredient(
                                extendedIngredients.getJSONObject( i ).getInt( "id" ),
                                extendedIngredients.getJSONObject( i ).getString( "name" ),
                                extendedIngredients.getJSONObject( i ).getString( "image" ),
                                extendedIngredients.getJSONObject( i ).getString( "aisle" ),
                                extendedIngredients.getJSONObject( i ).getInt( "amount" ),
                                extendedIngredients.getJSONObject( i ).getString( "unit" )
                        ) );
                    }

                    recipe = new Recipe( recipeQuery.getInt( "id" ), recipeQuery.getString( "title" ), recipeQuery.getString( "image" ), recipeQuery.getInt( "servings" ), recipeQuery.getInt( "readyInMinutes" ), recipeQuery.getDouble( "pricePerServing" ), ingredients );

                } catch (JSONException err ) {
                    err.printStackTrace();
                }

                if ( recipeQuery == null || getActivity() == null ) return;

                getActivity().runOnUiThread( () -> {
                    recipeInfoName.setText( recipe.getTitle() );
                    recipeInfoServings.setText( String.valueOf( recipe.getServings() ) );
                    recipeInfoReadyIn.setText( recipe.getReadyIn() + " minutes" );
                    recipeInfoPricePerServing.setText( recipe.getPricePerServing() + " US cents" );
                    Glide.with( getContext() ).load( recipe.getImageURL() ).placeholder( R.drawable.pelops_two ).transform( new CenterCrop(), new RoundedCorners( 20 ) ).into( recipeInfoRecipeImage );

                    recipeInfoRecyclerView.setAdapter( new RecipeInfoAdapter( getContext(), recipe.getIngredients(), RecipeSearchInfoFragment.this ) );

                    recipeInfoProgressBar.setVisibility( View.INVISIBLE );
                    recipeInfoView.setVisibility( View.VISIBLE );
                } );

                response.body().close();
            }
        } );
    }
}
