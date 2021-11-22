package com.cite306.project.dashboard.makeRecipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.cite306.project.R;
import com.cite306.project.adapter.makeRecipe.AddedIngredientsListAdapter;
import com.cite306.project.adapter.makeRecipe.MakeRecipeAdapter;
import com.cite306.project.adapter.makeRecipe.SearchedIngredientsAdapter;
import com.cite306.project.adapter.recipeSearch.RecipeInfoAdapter;
import com.cite306.project.adapter.recipeSearch.RecipeSearchAdapter;
import com.cite306.project.dashboard.DashboardActivity;
import com.cite306.project.dashboard.recipeSearch.RecipeSearchFragment;
import com.cite306.project.dashboard.recipeSearch.RecipeSearchInfoFragment;
import com.cite306.project.main.LoginFragment;
import com.cite306.project.model.Ingredient;
import com.cite306.project.model.Recipe;
import com.cite306.project.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MakeRecipeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.dashboard_makerecipe_fragment_list, container, false );

        final Button addRecipe;
        final LinearLayout backButton;

        addRecipe = view.findViewById( R.id.makeRecipeList_addRecipe );
        backButton = view.findViewById( R.id.makeRecipeList_backButton );

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById( R.id.makeRecipeList_recyclerView );
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );

        progressBar = view.findViewById( R.id.makeRecipeList_progressBar );

        loadSavedRecipes( mAuth.getUid() );

        addRecipe.setOnClickListener( v -> getActivity().getSupportFragmentManager().beginTransaction().add( R.id.makeRecipeActivity_fragment, new MakeRecipeUpdateFragment(), "makeRecipeUpdate" ).commit() );

        backButton.setOnClickListener( v -> {
            startActivity( new Intent( getActivity(), DashboardActivity.class ) );
            getActivity().finish();
        } );

        return view;
    }

    public void loadSavedRecipes( String userUID ) {
        if ( userUID == null ) return;

        database.collection( "users" )
                .document( userUID )
                .collection( "recipes" )
                .get().addOnCompleteListener( task -> {
                    if ( !task.isSuccessful() ) return;

                    final QuerySnapshot returnedRecipes = task.getResult();

                    List< String > recipeNames = new ArrayList<>();

                    for (int i = 0; i < returnedRecipes.size(); i++ ) {
                        recipeNames.add( returnedRecipes.getDocuments().get( i ).getId() );
                    }

                    if ( recipeNames.isEmpty() ) return;

                    recyclerView.setAdapter( new MakeRecipeAdapter( getContext(), recipeNames, MakeRecipeFragment.this ) );
                } );

        progressBar.setVisibility( View.INVISIBLE );
    }
}
