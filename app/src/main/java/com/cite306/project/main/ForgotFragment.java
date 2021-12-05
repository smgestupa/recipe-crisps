package com.cite306.project.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.cite306.project.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private EditText emailField;
    private Pattern isEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.main_fragment_forgot_new, container, false );

        final Button resetButton;

        mAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById( R.id.forgotFragment_progressBar );

        emailField = view.findViewById( R.id.forgotFragment_emailField );
        isEmail = Pattern.compile( "(@)([\\d\\w]+)([.]([\\d\\w]+))+", Pattern.CASE_INSENSITIVE );

        resetButton = view.findViewById( R.id.forgotFragment_resetButton );

        resetButton.setOnClickListener( v -> forgotPassword() );

        return view;
    }

    private void forgotPassword() {
        final InputMethodManager keypad = ( InputMethodManager )getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
        keypad.hideSoftInputFromWindow( getActivity().getCurrentFocus().getWindowToken(), 0 );

        final String email = emailField.getText().toString().trim();

        boolean stopReset = false;

        if ( email.isEmpty() ) {
            emailField.setError( "This field must not be empty." );
            stopReset = true;
        } else if ( !isEmail.matcher( email ).find() ) {
            emailField.setError( "This field must be an email address." );
            stopReset = true;
        }

        if ( stopReset ) return;

        progressBar.setVisibility( View.VISIBLE );

        mAuth.sendPasswordResetEmail( email )
                .addOnCompleteListener( task -> {
                    showSnackbarMessage( task.isSuccessful() );
                    progressBar.setVisibility( View.INVISIBLE );
                } );
    }

    private void showSnackbarMessage( boolean success ) {
        final Snackbar notif;

        if ( success ) notif = Snackbar.make( getView(), "Please check your email for password reset instructions.", Snackbar.LENGTH_LONG );
        else notif = Snackbar.make( getView(), "Reset failed, please try again. Something went wrong!", Snackbar.LENGTH_LONG );

        final View snackbarDesign = notif.getView();
        snackbarDesign.setBackground( ContextCompat.getDrawable( getContext(), R.drawable.toast_design ) );

        final TextView notifText = snackbarDesign.findViewById( com.google.android.material.R.id.snackbar_text );
        notifText.setTextColor( Color.parseColor( "#ffffff" ) );
        notifText.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
        notifText.setTextSize( 14 );

        notif.show();
    }
}
