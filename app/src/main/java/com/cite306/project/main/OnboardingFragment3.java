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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OnboardingFragment3 extends Fragment {

    FloatingActionButton floatActBut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding3, container, false);

        floatActBut = root.findViewById( R.id.floatActBut );

        floatActBut.setOnClickListener( v -> {
            Intent intent = new Intent( getActivity(), LoginActivity.class );
            startActivity( intent );
        } );

        return root;
    }
}
