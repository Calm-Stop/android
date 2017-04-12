package com.policestrategies.calm_stop.citizen;

import android.app.ProgressDialog;
import android.content.Intent;
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


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SharedUtil;

/**
 * Allows the user to log in or create an account.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

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
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mEmailField = (EditText) findViewById(R.id.input_email);
        mPasswordField = (EditText) findViewById(R.id.input_password);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();


    } // end onCreate

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedUtil.dismissProgressDialog(mProgressDialog);
    }


    /**
     * Any onClicks that we register will be handled in here
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_login:
                mProgressDialog = ProgressDialog.show(this, "", "Logging in", true, false);
                login();
                break;

            case R.id.button_signup:
                Intent i = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(i);
                break;

        }
    }

    /**
     * Begins the login process. Validates the input and - if input is valid - attempts to log in.
     */
    private void login() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        Log.d(TAG, "signIn" + email);
        if (!validateInput()) {
            SharedUtil.dismissProgressDialog(mProgressDialog);
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
                            SharedUtil.dismissProgressDialog(mProgressDialog);
                        } else  {

                            final String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference acctypeRef = FirebaseDatabase.getInstance().getReference("citizen");
                            acctypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    SharedUtil.dismissProgressDialog(mProgressDialog);
                                    if (dataSnapshot.hasChild(Uid)) {
                                        Toast.makeText(LoginActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(LoginActivity.this, "This is not a citizen account.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    SharedUtil.dismissProgressDialog(mProgressDialog);
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

} // end class LoginActivity
