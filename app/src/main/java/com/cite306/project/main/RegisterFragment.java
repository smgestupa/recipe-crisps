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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cite306.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private ProgressBar progressBar;

    private EditText usernameField, emailField, passwordField;

    private Pattern isEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.main_fragment_register_new, container, false );

        final Button registerButton;

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        progressBar = view.findViewById( R.id.registerFragment_progressBar );

        usernameField = view.findViewById( R.id.registerFragment_usernameField );
        emailField = view.findViewById( R.id.registerFragment_emailField );
        passwordField = view.findViewById( R.id.registerFragment_passwordField );

        isEmail = Pattern.compile( "(@)([\\d\\w]+)([.]([\\d\\w]+))+", Pattern.CASE_INSENSITIVE );

        registerButton = view.findViewById( R.id.registerFragment_registerButton );

        registerButton.setOnClickListener( v -> registerUser() );

        usernameField.requestFocus();

        return view;
    }

    private void registerUser() {
        final InputMethodManager keypad = ( InputMethodManager )getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
        keypad.hideSoftInputFromWindow( getActivity().getCurrentFocus().getWindowToken(), 0 );

        final String username = usernameField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();

        boolean stopRegister = false;

        if ( username.isEmpty() ) {
            usernameField.setError( "This field must not be empty." );
            stopRegister = true;
        }

        if ( email.isEmpty() ) {
            emailField.setError( "This field must not be empty." );
            stopRegister = true;
        } else if ( !isEmail.matcher( email ).find() ) {
            emailField.setError( "This field must be an email address." );
            stopRegister = true;
        }

        if ( password.isEmpty() ) {
            passwordField.setError( "This field must not be empty." );
            stopRegister = true;
        } else if ( password.length() < 6 ) {
            passwordField.setError( "Must not be less than 6 characters." );
            stopRegister = true;
        }

        if ( stopRegister ) return;

        progressBar.setVisibility( View.VISIBLE );

        usernameField.setFocusable( false );
        emailField.setFocusable( false );
        passwordField.setFocusable( false );

        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( task -> {

                    try {
                        if ( task.getResult().getAdditionalUserInfo().isNewUser() ) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if ( user == null ) return;

                            sendEmailVerification( user );

                            Map< String, Object > newUser = new HashMap<>();
                            newUser.put( "username", username );
                            newUser.put( "email", email );

                            database.collection( "users" )
                                    .document( user.getUid() )
                                    .set( newUser )
                                    .addOnCompleteListener( t -> System.out.println( "Successfully added new user to database." ) );

                            showToastMessage( "Email verification has been sent." );
                        }
                    } catch ( Exception err ) {
                        showToastMessage( "The email provided already exists." );
                    }

                    progressBar.setVisibility( View.INVISIBLE );

                    usernameField.setFocusableInTouchMode( true );
                    emailField.setFocusableInTouchMode( true );
                    passwordField.setFocusableInTouchMode( true );
                } );
    }

    private void sendEmailVerification( FirebaseUser user ) {
        user.sendEmailVerification()
                .addOnCompleteListener( task -> System.out.println( "Email verification has been sent to user's email address." ) );

        System.out.println( "Signing out user." );
        FirebaseAuth.getInstance().signOut();
    }

    private void showToastMessage( String message ) {
        final Toast notif;
        notif = Toast.makeText( getContext(), message, Toast.LENGTH_LONG );

        View toastView = notif.getView();
        toastView.setBackgroundResource( R.drawable.toast_design );

        TextView notifText = toastView.findViewById( android.R.id.message );
        notifText.setTextColor( Color.parseColor( "#ffffff" ) );
        notifText.setGravity( Gravity.CENTER );
        notifText.setTextSize( 14 );

        notif.show();
    }
}
