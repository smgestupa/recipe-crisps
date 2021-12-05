package com.cite306.project.dashboard.recipeSearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.cite306.project.R;
import com.cite306.project.adapter.recipeSearch.RecipeSearchAdapter;
import com.cite306.project.dashboard.DashboardActivity;
import com.cite306.project.model.Recipe;
import com.cite306.project.util.Util;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeSearchFragment extends Fragment {

    private RecyclerView recipeQueryList;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.dashboard_recipesearch_fragment_search, container, false );

        final EditText recipeSearchField;
        final LinearLayout backButton;

        recipeSearchField = view.findViewById( R.id.recipeSearch_searchField);

        backButton = view.findViewById( R.id.recipeSearch_backButton );

        recipeQueryList = view.findViewById( R.id.recipeSearch_recipeQueryList );
        recipeQueryList.setLayoutManager( new LinearLayoutManager( getContext() , LinearLayoutManager.HORIZONTAL, false ) );

        progressBar = view.findViewById( R.id.recipeSearch_progressBar );

        new PagerSnapHelper().attachToRecyclerView( recipeQueryList );

        recipeSearchField.setOnEditorActionListener( ( v, actionId, event ) -> {
            if ( actionId == EditorInfo.IME_ACTION_DONE ) {
                final String recipeQuery = recipeSearchField.getText().toString().trim();

                progressBar.setVisibility( View.VISIBLE );

                try {
                    recipeQueryList.setVisibility( View.INVISIBLE );
                    searchForRecipes( recipeQuery );
                } catch ( Exception err ) {
                    System.err.println( "Something went wrong in onCreate() function inside RecipeSearchActivity.java!" );
                    err.printStackTrace();
                }

                return false;
            }

            return false;
        } );

        backButton.setOnClickListener( v -> {
            startActivity( new Intent( getActivity(), DashboardActivity.class ) );
            getActivity().finish();
        } );

        return view;
    }

    private void searchForRecipes( String query ) throws Exception {
        if ( query.isEmpty() ) return;

        final String cleanQuery = query.replaceAll( " +", " " );
        final URL url = new URL( Util.spoonacularRecipeSearchURL.replace( "{query}", cleanQuery ) );

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url( url )
                .build();

        client.newCall( request ).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.err.println( "Something went wrong with the OkHttp request, inside searchForRecipes():" );
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if ( !response.isSuccessful() ) return;

                final String recipeQuery = response.body().string();

                getActivity().runOnUiThread( () -> {
                    List< Recipe > recipeQueried = new ArrayList<>();

                    try {
                        final JSONArray json = new JSONObject( recipeQuery ).getJSONArray( "results" );

                        for ( int i = 0; i < json.length(); i++ ) {
                            final JSONObject currentEntry = json.getJSONObject( i );
                            recipeQueried.add( new Recipe( currentEntry.getInt( "id" ), currentEntry.getString( "title" ), currentEntry.getString( "image" ) ) );
                        }
                    } catch ( Exception err ) {
                        System.err.println( "Something went wrong inside searchForRecipes() function inside RecipeSearchActivity.java!" );
                        err.printStackTrace();
                    }

                    progressBar.setVisibility( View.INVISIBLE );

                    recipeQueryList.setAdapter( new RecipeSearchAdapter( getContext(), recipeQueried, RecipeSearchFragment.this ) );
                    recipeQueryList.setVisibility( View.VISIBLE );
                } );

                response.body().close();
            }
        } );
    }
}
