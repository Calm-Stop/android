package com.policestrategies.calm_stop.citizen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import com.policestrategies.calm_stop.R;

/**
 * Allows the user to sign up or return to the log in page.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mNameField = (EditText) findViewById(R.id.input_name);
        mEmailField = (EditText) findViewById(R.id.input_email);
        mPasswordField = (EditText) findViewById(R.id.input_password);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);

    }

    /**
     * Any onClicks that we register will be handled in here
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_login:
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
                break;

            case R.id.button_signup: // Signup was pressed, begin the SignupActivity
                signup();
                break;

        }
    }

    /**
     * Begins the signup process. Validates the input and - if input is valid - attempts to create
     * an account.
     */
    private void signup() {
        final String name = mNameField.getText().toString();
        final String email = mEmailField.getText().toString();
        final String password = mPasswordField.getText().toString();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing up");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        if (!validateInput()) {
            return;
        }
        Toast.makeText(SignupActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            successfulSignup();
                            writeNewUser(email, task.getResult().getUser().getUid());
                        } else {
                            unsuccessfulSignup();
                        }
                    }
                });


        // Now we need to attempt to signup - we'll add code for this later (once Firebase is integrated)


    }

    private void successfulSignup() {
        mProgressDialog.dismiss();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("USERNAME", mEmailField.getText().toString());
        startActivity(intent);
        finish();
    }

    private void unsuccessfulSignup() {
        mProgressDialog.dismiss();

        Toast.makeText(SignupActivity.this, "Error creating account", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInput() {
        String nameInput = mNameField.getText().toString();
        String emailInput = mEmailField.getText().toString();
        String passwordInput = mPasswordField.getText().toString();

        if (nameInput.isEmpty()) {
            mNameField.setError("Please enter your name");
            mNameField.requestFocus();
            return false;
        } else {
            mNameField.setError(null);
        }


        if (emailInput.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            mEmailField.setError("Please enter a valid email address");
            mEmailField.requestFocus();
            return false;
        } else {
            mEmailField.setError(null);
        }

        if (passwordInput.isEmpty() || passwordInput.length() < 6) {
            mPasswordField.setError("Please enter a valid password" +
                    " containing at least 6 characters.");
            mPasswordField.requestFocus();
            return false;
        } else {
            mPasswordField.setError(null);
        }

        return true;

    }

    private void writeNewUser(String email, String uuid) {
        System.out.println("Writing user: " + email + " " + uuid);
        mDatabase.child("users").child(uuid).child("name").setValue(mNameField.getText().toString());
    }



} // end class SignupActivity
