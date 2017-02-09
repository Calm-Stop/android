package com.policestrategies.calm_stop.citizen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.policestrategies.calm_stop.R;

/**
 * Allows the user to log in or create an account.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // References to the EditText (text fields) in activity_login.xml
    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;
    private Button mLoginButton;
    private TextView mSignupButton;

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

        mLoginButton = (Button)findViewById(R.id.button_login);
        mSignupButton = (EditText)findViewById(R.id.button_signup);

        mLoginButton.setOnClickListener(this);
        mSignupButton.setOnClickListener(this);
    }

    /**
     * Any onClicks that we register will be handled in here
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.button_login: // The login button was pressed - let's run the login function
                login();
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
    private void login() {

        if (!validateInput()) {
            return;
        }
        Toast.makeText(LoginActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Logging in");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        authenticateLogin();

    }
    private void successfulLogin() {
        mProgressDialog.dismiss();

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void unsuccessfulLogin() {
        mProgressDialog.dismiss();
        Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_SHORT).show();
    }

    private void authenticateLogin() {
        String emailInput = mEmailField.getText().toString();
        String passwordInput = mPasswordField.getText().toString();


        mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            successfulLogin();
                        } else {
                            unsuccessfulLogin();
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
