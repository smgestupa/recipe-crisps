package com.cite306.project.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.cite306.project.dashboard.DashboardActivity;
import com.cite306.project.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private EditText emailField, passwordField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.main_fragment_login_new, container, false );

        final Button loginButton;

        mAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById( R.id.loginFragment_progressBar );

        emailField = view.findViewById( R.id.loginFragment_emailField );
        passwordField = view.findViewById( R.id.loginFragment_passwordField );

        emailField.requestFocus();

        loginButton = view.findViewById( R.id.loginFragment_loginButton );

        loginButton.setOnClickListener( v -> userLogin() );

        return view;
    }

    private void userLogin() {
        InputMethodManager keypad = ( InputMethodManager )getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
        keypad.hideSoftInputFromWindow( getActivity().getCurrentFocus().getWindowToken(), 0 );

        final String email = emailField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();

        boolean stopLogin = false;

        if ( email.isEmpty() ) {
            emailField.setError( "This field must not be empty." );
            stopLogin = true;
        }

        if ( password.isEmpty() ) {
            passwordField.setError( "This field must not be empty." );
            stopLogin = true;
        } else if ( password.length() < 6 ) {
            passwordField.setError( "Must not be less than 6 characters." );
            stopLogin = true;
        }

        if ( stopLogin ) return;

        progressBar.setVisibility( View.VISIBLE );

        emailField.setFocusable( false );
        passwordField.setFocusable( false );

        mAuth.signInWithEmailAndPassword( email, password )
                .addOnCompleteListener( task -> {
                    if ( !task.isSuccessful() ) {
                        showSnackbarMessage( "Log in failed, please try again." );
                    } else if ( !checkIfEmailVerified() ) {
                        showSnackbarMessage( "You must verify your email first." );
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener( t -> System.out.println( "Email verification has been sent to the user." ) );
                        mAuth.signOut();
                    } else if ( task.isSuccessful() ) {
                        System.out.println( "Logged in successfully!" );

                        startActivity( new Intent( getActivity(), DashboardActivity.class ) );
                        getActivity().finish();
                    }

                    progressBar.setVisibility( View.INVISIBLE );

                    emailField.setFocusableInTouchMode( true );
                    passwordField.setFocusableInTouchMode( true );
                } );
    }

    private boolean checkIfEmailVerified() {
        FirebaseUser user = mAuth.getCurrentUser();
        if ( user == null ) return false;

        return user.isEmailVerified();
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