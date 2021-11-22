package com.cite306.project.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cite306.project.LoginActivity;
import com.cite306.project.R;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeActivity;
import com.cite306.project.dashboard.makeRecipe.MakeRecipeFragment;
import com.cite306.project.dashboard.recipeSearch.RecipeSearchActivity;
import com.cite306.project.dashboard.recipeSearch.RecipeSearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;

    private TextView usernameLabel;

    private LinearLayout switchToSearch, switchToMakeRecipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.dashboard_fragment_dashboard, container, false );

        final Button logoutButton;

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        switchToSearch = view.findViewById( R.id.dashboardFragment_switchToSearch );
        switchToMakeRecipe = view.findViewById( R.id.dashboardFragment_switchToMakeRecipe );

        usernameLabel = view.findViewById( R.id.dashboardFragment_usernameLabel );

        getUsername( mAuth.getCurrentUser() );

        logoutButton = view.findViewById( R.id.dashboardFragment_logoutButton );

        switchToSearch.setOnClickListener( v -> startActivity( new Intent( getActivity(), RecipeSearchActivity.class ) ) );

        switchToMakeRecipe.setOnClickListener( v -> startActivity( new Intent( getActivity(), MakeRecipeActivity.class ) ) );

        logoutButton.setOnClickListener( v -> {
          mAuth.signOut();
          startActivity( new Intent( getActivity() , LoginActivity.class ) );
          getActivity().finish();
        } );

        return view;
    }

    private void getUsername( FirebaseUser user ) {
        if ( user == null ) return;

        database.collection( "users" )
                .document( user.getUid() )
                .get().addOnCompleteListener( task -> {
                    usernameLabel.setText( task.getResult().get( "username" ).toString() );
                } );

        final Animation fadeIn = new AlphaAnimation( 0, 1 );
        fadeIn.setInterpolator( new DecelerateInterpolator() );
        fadeIn.setDuration( 1000 );

        usernameLabel.setAnimation( fadeIn );
    }
}
