package com.policestrategies.calm_stop.officer;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SharedUtil;

/**
 * Allows the officer to log in or create an account.
 * @author Talal Abou Haiba
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mDepartmentField;
    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";

    private ProgressDialog mProgressDialog;

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

        mDepartmentField = (EditText) findViewById(R.id.login_input_department);
        mEmailField = (EditText) findViewById(R.id.login_input_email);
        mPasswordField = (EditText) findViewById(R.id.login_input_password);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

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
                navigateToSignupActivity();
                break;

        }
    }

    private void navigateToSignupActivity() {
        Intent i = new Intent(getBaseContext(), SignupActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Begins the login process. Validates the input and - if input is valid - attempts to log in.
     */
    private void login() {
        final String departmentNumber = mDepartmentField.getText().toString();
        final String email = mEmailField.getText().toString();
        final String password = mPasswordField.getText().toString();

        Log.d(TAG, "Signing in with email: " + email);
        if (!validateInput()) {
            SharedUtil.dismissProgressDialog(mProgressDialog);
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
                            SharedUtil.dismissProgressDialog(mProgressDialog);
                        } else {

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
                                            SharedUtil.dismissProgressDialog(mProgressDialog);
                                            Toast.makeText(LoginActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getBaseContext(), DashboardActivity.class);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            FirebaseAuth.getInstance().signOut();
                                            SharedUtil.dismissProgressDialog(mProgressDialog);
                                            Toast.makeText(LoginActivity.this,
                                                    "This is not an officer account.",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        SharedUtil.dismissProgressDialog(mProgressDialog);
                                        mDepartmentField.setError("Invalid department number");
                                        mDepartmentField.requestFocus();
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
