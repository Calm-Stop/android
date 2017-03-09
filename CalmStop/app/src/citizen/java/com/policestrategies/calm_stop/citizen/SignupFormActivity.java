package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
    private Spinner genderSetter;
    private Spinner langSetter;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private static EditText  mEmailField;
    private EditText mPasswordField;
    private EditText mLicenseNum;
    private EditText mDateOfBirth;
    private EditText mPhone;
    private EditText mAddress;

    private int gen = 0;
    private int lang = 0;

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

        genderSetter = (Spinner) findViewById(R.id.genderSetter);
        langSetter = (Spinner) findViewById(R.id.ethnicitySetter);

        setUpGenderSetter();
        setUpLangSetter();

        mEmailField = (EditText) findViewById(R.id.input_email);
        mPasswordField = (EditText) findViewById(R.id.input_password);
        mLicenseNum = (EditText) findViewById(R.id.input_licenseNum);
        mFirstNameField = (EditText) findViewById(R.id.input_firstname);
        mLastNameField = (EditText) findViewById(R.id.input_lastname);
        mPhone = (EditText) findViewById(R.id.input_phone);
        mAddress = (EditText) findViewById(R.id.input_address);
        mDateOfBirth = (EditText) findViewById(R.id.input_DateOfBirth);

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
        final String address = mAddress.getText().toString();
        final String dateofbirth = mDateOfBirth.getText().toString();
        final String gender = genderSetter.getSelectedItem().toString();
        final String language = langSetter.getSelectedItem().toString();

        Log.d(TAG, "createAccount:" + email);

        if (!validateInput(email, password, licensenum, firstname, lastname, phone, address, gender, language, dateofbirth)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful())  {
                            Toast.makeText(SignupFormActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        } else {
                            Toast.makeText(SignupFormActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uuid = user.getUid();
                            DatabaseReference citizenDatabaseRef = databaseRef.child("citizen")
                                    .child(uuid).child("profile").getRef();
                            //email, password, licensenum, firstname, lastname, phone, address, dateofbirth, gender, language
                            citizenDatabaseRef.child("email").setValue(email);
                            citizenDatabaseRef.child("first_name").setValue(firstname);
                            citizenDatabaseRef.child("last_name").setValue(lastname);
                            citizenDatabaseRef.child("license_number").setValue(licensenum);
                            citizenDatabaseRef.child("phone_number").setValue(phone);
                            citizenDatabaseRef.child("address").setValue(address);
                            citizenDatabaseRef.child("gender").setValue(gender);
                            citizenDatabaseRef.child("language").setValue(language);
                            citizenDatabaseRef.child("date_of_birth").setValue(dateofbirth);

//                            databaseRef.child("citizen").child(uuid).child("profile").setValue(mUser);
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
                                  String lastname, String phone, String address, String gender, String language, String dateofbirth) {

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

        //address gender language DOB; Use google map API for address
        //ADDRESS REGEX
        if (!SignupVerification.validAddress(address)){
            mAddress.setError("This field was left empty.");
            mAddress.requestFocus();
            return false;
        } else {
            mAddress.setError(null);
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

    private void setUpGenderSetter() {

        final ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.Gender, android.R.layout.simple_spinner_dropdown_item);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSetter.setAdapter(genderAdapter);

        genderSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        //blank
                        gen = 0;
                        break;
                    case 1:
                        //female
                        gen = 1;
                        break;
                    case 2:
                        //male
                        gen = 2;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setUpLangSetter() {

        final ArrayAdapter<CharSequence> langAdapter = ArrayAdapter.createFromResource(this,
                R.array.Language, android.R.layout.simple_spinner_dropdown_item);

        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSetter.setAdapter(langAdapter);

        langSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        //English
                        lang = 0;
                        break;
                    case 1:
                        //Spanish
                        lang = 1;
                        break;
                    case 2:
                        //Mandarin Chinese
                        lang = 2;
                        break;
                    case 3:
                        //Vietnamese
                        lang = 3;
                        break;
                    case 4:
                        //French
                        lang = 4;
                        break;
                    case 5:
                        //Arabic
                        lang = 5;
                        break;
                    case 6:
                        //German
                        lang = 6;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    /*
     *  local private class
     *  Citizen is used to store data that is dumped into firebase on account creation here
     */
/*    private class Citizen {
        String email, password, licensenum,
                firstname, lastname, phone, address, gender, language, dateofbirth;

        public Citizen(String email, String password, String licenseNum, String firstname,
                       String lastname, String phone, String address, String gender, String language,
                       String dateofbirth) {
            this.email = email;
            this.password = password;
            this.licensenum = licenseNum;
            this.firstname = firstname;
            this.lastname = lastname;
            this.phone = phone;
            this.address = address;
            this.gender = gender;
            this.language = language;
            this.dateofbirth = dateofbirth;
        }

        public Citizen(final String email, final String password) {
            this.email = email;
            this.password = password;
        }
    }*/
} // end class SignupActivity

