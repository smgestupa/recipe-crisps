package com.cite306.project.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cite306.project.LoginActivity;
import com.cite306.project.R;
import com.cite306.project.dashboard.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class OnboardingFragment1 extends Fragment {

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        if ( mAuth.getCurrentUser() != null ) {
            Intent intent = new Intent( getActivity(), DashboardActivity.class );
            startActivity( intent );
        }

        return inflater.inflate( R.layout.fragment_on_boarding1, container,false );
    }
}
