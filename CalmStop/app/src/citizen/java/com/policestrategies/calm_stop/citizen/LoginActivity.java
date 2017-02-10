package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;

/**
 * Allows the user to log in or create an account.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // References to the EditText (text fields) in activity_login.xml
    private static EditText  mEmailField;
    private EditText mPasswordField;

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

        // Now we need to attempt to log in - we'll add code for this later (once Firebase is integrated)
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);

        //finish();

        //Toast.makeText(LoginActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();

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
