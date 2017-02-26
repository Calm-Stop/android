package com.policestrategies.calm_stop.citizen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.policestrategies.calm_stop.R;

/**
 * Allows the user to log in or create an account.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // References to the EditText (text fields) in activity_login.xml
    private static EditText  mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private static final String TAG = "Login";

    private UserLocalStore localStore;

    /**
     * onCreate is called immediately following the creation of an Activity.
     * Activity is opened -> onCreate is called.
     * In onCreate, we will attach onClickListeners to our buttons as well as set up the view
     * (setContentView, takes in an xml file).
     *
     * https://developer.android.com/reference/android/app/Activity.html - check out the diagram here
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = (EditText) findViewById(R.id.input_email);
        mPasswordField = (EditText) findViewById(R.id.input_password);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);

        // START initialize_auth so you can track when user signs in and signs out
        mAuth = FirebaseAuth.getInstance();
        // END initialize_auth
        //localStore initializes on current context
        localStore = new UserLocalStore(this);

        // START auth_state_listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // END auth_state_listener

    }

    // START on_start_add_listener
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // Remove FirebaseAuth instance on onStop()
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * Any onClicks that we register will be handled in here
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_login: // The login button was pressed - let's run the login function
                login(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;

            case R.id.button_signup: // Signup was pressed, begin the SignupActivity
                Intent i = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(i);
                break;

        }
    }

    /**
     * Begins the login process. Validates the input and - if input is valid - attempts to log in.
     */
    private void login(String email, String password) {
        final String m_email = email;
        final String m_password = password;
        Log.d(TAG, "signIn" + email);
        if (!validateInput()) {
            Toast.makeText(LoginActivity.this, "Validation Failed", Toast.LENGTH_SHORT).show();
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Validation failed.", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getBaseContext(), SignupActivity.class);
                            startActivity(i);
                            finish();
                        }
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(LoginActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
                            User user = new User(m_email, m_password);
                            localStore.StoreUserData(user);
                            Intent i = new Intent(getBaseContext(), HomepageActivity.class);
                            startActivity(i);
                        }

                    }
                });
        // [END sign_in_with_email]
        //you signed up, CONGRATS


    }



    /**
     * Validates the given email and password. Does not connect to server, simply ensures that
     * the user typed in an email address and a password.
     * @return true if the email and password are correctly formed
     */
    private boolean validateInput() {
        String emailInput = mEmailField.getText().toString();
        String passwordInput = mPasswordField.getText().toString();

        if (emailInput.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            mEmailField.setError("Please enter a valid email address");
            mEmailField.requestFocus();
            return false;
        } else {
            mEmailField.setError(null);
        }

        if (passwordInput.isEmpty()) {
            mPasswordField.setError("Please enter your password");
            mPasswordField.requestFocus();
            return false;
        } else {
            mPasswordField.setError(null);
        }

        return true;
    }

    public static String getEmail(){
        String emailInput = mEmailField.getText().toString();

        //parse. Don't care about email after @....
        String delimiter = "@";
        //split into 2 after @
        String[] token = emailInput.split(delimiter);
        //first part
        emailInput = token[0];
        return emailInput;
    }

} // end class LoginActivity