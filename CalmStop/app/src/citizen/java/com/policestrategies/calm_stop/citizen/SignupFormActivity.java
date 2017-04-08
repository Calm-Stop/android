package com.policestrategies.calm_stop.citizen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import com.policestrategies.calm_stop.RegexChecks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Allows the user to sign up or return to the log in page.
 */
public class SignupFormActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner genderSetter;
    private Spinner langSetter;
    private Spinner ethnicitySetter;
    private int i_gender, i_language, i_ethnicity;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mDateOfBirthField;
    private EditText mLicenseField;
    private EditText mPhoneNumberField;
    private EditText mZipField;

    private static final String TAG = "Signup";

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference databaseRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupform);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);
        langSetter = (Spinner) findViewById(R.id.langSetter);
        ethnicitySetter = (Spinner) findViewById(R.id.ethnicitySetter);

        setUpGenderSetter();
        setUpLangSetter();
        setUpEthnicitySetter();

        mEmailField = (EditText) findViewById(R.id.signup_input_email);
        mPasswordField = (EditText) findViewById(R.id.signup_input_password);
        mLicenseField = (EditText) findViewById(R.id.signup_input_license_number);
        mFirstNameField = (EditText) findViewById(R.id.signup_input_firstname);
        mLastNameField = (EditText) findViewById(R.id.signup_input_lastname);
        mPhoneNumberField = (EditText) findViewById(R.id.input_phone);
        mZipField = (EditText) findViewById(R.id.signup_input_zipcode);
        mDateOfBirthField = (EditText) findViewById(R.id.signup_input_dob);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);

        setUpCalendar();
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
        final String licensenum = mLicenseField.getText().toString();
        final String firstname = mFirstNameField.getText().toString();
        final String lastname = mLastNameField.getText().toString();
        final String phone = mPhoneNumberField.getText().toString();
        final String zip = mZipField.getText().toString();
        final String dateOfBirth = mDateOfBirthField.getText().toString();

        Log.d(TAG, "createAccount:" + email);

        if (!validateInput(email, password, licensenum, firstname, lastname, phone, zip, dateOfBirth)) {
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
                            //email, password, licensenum, firstname, lastname, phone, zip, dateofbirth, gender, language
                            citizenDatabaseRef.child("email").setValue(email);
                            citizenDatabaseRef.child("first_name").setValue(firstname);
                            citizenDatabaseRef.child("last_name").setValue(lastname);
                            citizenDatabaseRef.child("license_number").setValue(licensenum);
                            citizenDatabaseRef.child("phone_number").setValue(phone);
                            citizenDatabaseRef.child("zip_code").setValue(zip);
                            citizenDatabaseRef.child("gender").setValue(i_gender);
                            citizenDatabaseRef.child("language").setValue(i_language);
                            citizenDatabaseRef.child("ethnicity").setValue(i_ethnicity);
                            citizenDatabaseRef.child("dob").setValue(dateOfBirth);

                            Intent i = new Intent(getBaseContext(), HomepageActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                });
    }

    private boolean validateInput(String email, String password, String licensenum, String firstname,
                                  String lastname, String phone, String zip, String dateOfBirth) {

        //FIRST NAME CHECK
        if (!RegexChecks.validFirstName(firstname)) {
            mFirstNameField.setError("Please enter a valid first name.");
            mFirstNameField.requestFocus();
            return false;
        } else {
            mFirstNameField.setError(null);
        }
        //LAST NAME CHECK
        if (!RegexChecks.validLastName(lastname)) {
            mLastNameField.setError("Please enter a valid last name.");
            mLastNameField.requestFocus();
            return false;
        } else {
            mLastNameField.setError(null);
        }

        //EMAIL CHECK
        if (!RegexChecks.validEmail(email)) {
            mEmailField.setError("Enter a valid email address.");
            mEmailField.requestFocus();
            return false;
        } else {
            mEmailField.setError(null);
        }

        //PASSWORD REGEX
        if (!RegexChecks.validPassword(password)) {
            mPasswordField.setError("Password must be at least 6 characters and contain at least a letter and a number.");
            mPasswordField.requestFocus();
            return false;
        } else {
            mPasswordField.setError(null);
        }

        //DRIVER'S LICENSE REGEX (CALIFORNIA FORMAT)

        if(!RegexChecks.validLicense(licensenum)) {
            mLicenseField.setError("Enter a letter followed by eight numbers\nExample: A12345678");
            mLicenseField.requestFocus();
            return false;
        } else {
            mLicenseField.setError(null);
        }

        //PHONE REGEX
        if(!RegexChecks.validPhone(phone)) {
            mPhoneNumberField.setError("Invalid Phone Number.");
            mPhoneNumberField.requestFocus();
            return false;
        } else {
            mPhoneNumberField.setError(null);
        }

        //ZIP CODE REGEX
        if (!RegexChecks.validZip(zip)){
            mZipField.setError("Example: 95064");
            mZipField.requestFocus();
            return false;
        } else {
            mZipField.setError(null);
        }

        if (!RegexChecks.validDateOfBirth(dateOfBirth)) {
            mDateOfBirthField.performClick();
            Toast.makeText(SignupFormActivity.this, "Please enter your date of birth",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setUpGenderSetter() {

        final ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.Gender, android.R.layout.simple_spinner_dropdown_item);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSetter.setAdapter(genderAdapter);

        genderSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i_gender = position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }


    private void setUpEthnicitySetter() {

        final ArrayAdapter<CharSequence> ethnicityAdapter = ArrayAdapter.createFromResource(this,
                R.array.Ethnicity, android.R.layout.simple_spinner_dropdown_item);

        ethnicityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ethnicitySetter.setAdapter(ethnicityAdapter);

        ethnicitySetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i_ethnicity = position;
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
                i_language = position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setUpCalendar() {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                mDateOfBirthField.setText(sdf.format(calendar.getTime()));

            }

        };

        mDateOfBirthField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SignupFormActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

} // end class SignupActivity

