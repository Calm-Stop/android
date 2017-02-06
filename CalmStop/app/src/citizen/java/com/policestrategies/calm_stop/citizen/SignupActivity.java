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
 * Allows the user to sign up or return to the log in page.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mNameField = (EditText) findViewById(R.id.input_name);
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

        if (!validateInput()) {
            return;
        }

        // Now we need to attempt to signup - we'll add code for this later (once Firebase is integrated)

        Toast.makeText(SignupActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
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

} // end class SignupActivity
