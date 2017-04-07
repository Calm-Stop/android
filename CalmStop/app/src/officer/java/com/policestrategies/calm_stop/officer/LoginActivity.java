package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.policestrategies.calm_stop.R;

/**
 * Allows the officer to log in or create an account.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mDepartmentField;
    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAG = "Login";

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
        // hide the titlebar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        mDepartmentField = (EditText) findViewById(R.id.login_input_department);
        mEmailField = (EditText) findViewById(R.id.login_input_email);
        mPasswordField = (EditText) findViewById(R.id.login_input_password);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);

        // START initialize_auth so you can track when user signs in and signs out
        mAuth = FirebaseAuth.getInstance();
        // END initialize_auth

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
                login(mDepartmentField.getText().toString(), mEmailField.getText().toString(),
                        mPasswordField.getText().toString());
                break;

            case R.id.button_signup: // Signup was pressed, begin the SignupActivity
                Intent i = new Intent(getBaseContext(), SignupFormActivity.class);
                startActivity(i);
                break;

        }
    }

    /**
     * Begins the login process. Validates the input and - if input is valid - attempts to log in.
     */
    private void login(final String departmentNumber, String email, String password) {
        Log.d(TAG, "Signing in with email: " + email);
        if (!validateInput()) {
            Toast.makeText(LoginActivity.this, "Validation Failed", Toast.LENGTH_SHORT).show();
            return;
        }
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
                        }
                        if (task.isSuccessful()) {

                            final String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference acctypeRef = FirebaseDatabase.getInstance().getReference("officer");
                            acctypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    // Validate department number
                                    if (dataSnapshot.hasChild(departmentNumber)) {
                                        if (dataSnapshot.child(departmentNumber).hasChild(Uid)) {
                                            getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
                                                    .edit().putString(getString(R.string.shared_preferences_department_number),
                                                    departmentNumber).commit();
                                            Toast.makeText(LoginActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getBaseContext(), HomepageActivity.class);
                                            startActivity(i);
                                        } else {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(LoginActivity.this, "This is not an officer account.", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        mDepartmentField.setError("Invalid department number");
                                        mDepartmentField.requestFocus();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                });
    }

    /**
     * Validates the given email and password. Does not connect to server, simply ensures that
     * the user typed in an email address and a password.
     * @return true if the email and password are correctly formed
     */
    private boolean validateInput() {
        String departmentInput = mDepartmentField.getText().toString();
        String emailInput = mEmailField.getText().toString();
        String passwordInput = mPasswordField.getText().toString();

        if (departmentInput.isEmpty()) {
            mDepartmentField.setError("Please enter your department number");
            mDepartmentField.requestFocus();
            return false;
        } else {
            mPasswordField.setError(null);
        }


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

} // end class LoginActivity
