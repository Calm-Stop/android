package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SignupVerification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows the user to sign up or return to the log in page.
 */

public class SignupFormActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private static EditText  mEmailField;
    private EditText mPasswordField;
    private EditText mLicenseNum;
    private EditText mDateOfBirth;
    private EditText mPhone;
    private EditText mZip;
    private EditText mGender;
    private EditText mLanguage;
    private EditText mDepartment;

    private static final String TAG = "Signup";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    // [START declare databaseRef & database]
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;
    // [END declare databaseRef & database]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupform);

        mEmailField = (EditText) findViewById(R.id.input_email);
        mPasswordField = (EditText) findViewById(R.id.input_password);
        mLicenseNum = (EditText) findViewById(R.id.input_licenseNum);
        mFirstNameField = (EditText) findViewById(R.id.input_firstname);
        mLastNameField = (EditText) findViewById(R.id.input_lastname);
        mPhone = (EditText) findViewById(R.id.input_phone);
        mZip = (EditText) findViewById(R.id.input_zip);
        mGender = (EditText) findViewById(R.id.input_gender);
        mDateOfBirth = (EditText) findViewById(R.id.input_DateOfBirth);
        mLanguage = (EditText) findViewById(R.id.input_language);
        mDepartment = (EditText) findViewById(R.id.input_department);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
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
        final String email = mEmailField.getText().toString();
        final String password = mPasswordField.getText().toString();
        final String licensenum = mLicenseNum.getText().toString();
        final String firstname = mFirstNameField.getText().toString();
        final String lastname = mLastNameField.getText().toString();
        final String phone = mPhone.getText().toString();
        final String zip = mZip.getText().toString();
        final String gender = mGender.getText().toString();
        final String language = mLanguage.getText().toString();
        final String dateofbirth = mDateOfBirth.getText().toString();
        final String department = mDepartment.getText().toString();

        Log.d(TAG, "createAccount:" + email);


        if (!validateInput(email, password, licensenum, firstname, lastname, phone, zip, dateofbirth)) {
            return;
        }

        // Now we need to attempt to signup - we'll add code for this later (once Firebase is integrated)
        // [START create_user_with_email]

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupFormActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        } else {
                            Toast.makeText(SignupFormActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uuid = user.getUid();

                            // Set up department
                            DatabaseReference officerDatabaseRef = databaseRef.child("officer")
                                    .child(department).child(uuid).child("profile").getRef();

                            officerDatabaseRef.child("email").setValue(email);

                            officerDatabaseRef.child("email").setValue(email);
                            officerDatabaseRef.child("first_name").setValue(firstname);
                            officerDatabaseRef.child("last_name").setValue(lastname);
                            officerDatabaseRef.child("license_number").setValue(licensenum);
                            officerDatabaseRef.child("phone_number").setValue(phone);
                            officerDatabaseRef.child("zip_code").setValue(zip);
                            officerDatabaseRef.child("gender").setValue(gender);
                            officerDatabaseRef.child("language").setValue(language);
                            officerDatabaseRef.child("date_of_birth").setValue(dateofbirth);

                            Intent i = new Intent(getBaseContext(), HomepageActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                });
        // [END create_user_with_email]
        //you signed up, CONGRATS


    }

    private boolean validateInput(String email, String password, String licensenum, String firstname,
                                  String lastname, String phone, String zip, String dateofbirth) {

        //FIRST NAME CHECK
        if (!SignupVerification.validFirstName(firstname)) {
            mFirstNameField.setError("Please enter a valid first name.");
            mFirstNameField.requestFocus();
            return false;
        } else {
            mFirstNameField.setError(null);
        }
        //LAST NAME CHECK
        if (!SignupVerification.validLastName(lastname)) {
            mLastNameField.setError("Please enter a valid last name.");
            mLastNameField.requestFocus();
            return false;
        } else {
            mLastNameField.setError(null);
        }

        //EMAIL CHECK
        if (!SignupVerification.validEmail(email)) {
            mEmailField.setError("Enter a valid email address.");
            mEmailField.requestFocus();
            return false;
        } else {
            mEmailField.setError(null);
        }

        //PASSWORD REGEX
        if (!SignupVerification.validPassword(password)) {
            mPasswordField.setError("Password must be at least 6 characters and contain at least a letter and a number.");
            mPasswordField.requestFocus();
            return false;
        } else {
            mPasswordField.setError(null);
        }

        //DRIVER'S LICENSE REGEX (CALIFORNIA FORMAT)

        if(!SignupVerification.validLicense(licensenum)) {
            mLicenseNum.setError("Enter a letter followed by eight numbers\nExample: A12345678");
            mLicenseNum.requestFocus();
            return false;
        } else {
            mLicenseNum.setError(null);
        }

        //PHONE REGEX
        if(!SignupVerification.validPhone(phone)) {
            mPhone.setError("Invalid Phone Number.");
            mPhone.requestFocus();
            return false;
        } else {
            mPhone.setError(null);
        }

        //DATE OF BIRTH REGEX; replace with spinner for month, day, and year.
        if (!SignupVerification.validDateOfBirth(dateofbirth)){
            mDateOfBirth.setError("DD-MM-YYYY");
            mDateOfBirth.requestFocus();
            return false;
        } else {
            mDateOfBirth.setError(null);
        }

        //zip gender language DOB
        //ZIP CODE REGEX
        if (!SignupVerification.validZip(zip)){
            mZip.setError("This field was left empty.");
            mZip.requestFocus();
            return false;
        } else {
            mZip.setError(null);
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

} // end class SignupformActivity

