package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows the user to sign up or return to the log in page.
 */

public class SignupFormActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner genderSetter;
    private Spinner langSetter;
    private Spinner ethnicitySetter;
    private Spinner monthSetter;
    private Spinner daySetter;
    private Spinner yearSetter;
    private int i_month, i_day, i_year, i_ethnicity, i_language, i_gender;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private static EditText  mEmailField;
    private EditText mPasswordField;
    private EditText mLicense;
    private TextView mDateOfBirth;
    private EditText mPhone;
    private EditText mZip;
    private EditText mDepartment;
    private EditText mBadge;

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

        mDateOfBirth = (TextView) findViewById(R.id.dateOfBirth);
        mEmailField = (EditText) findViewById(R.id.input_email);
        mPasswordField = (EditText) findViewById(R.id.input_password);
        mLicense = (EditText) findViewById(R.id.input_licenseNum);
        mFirstNameField = (EditText) findViewById(R.id.input_firstname);
        mLastNameField = (EditText) findViewById(R.id.input_lastname);
        mPhone = (EditText) findViewById(R.id.input_phone);
        mZip = (EditText) findViewById(R.id.input_zip);
        mDepartment = (EditText) findViewById(R.id.input_department);
        mBadge = (EditText) findViewById(R.id.input_badge);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);
        langSetter = (Spinner) findViewById(R.id.langSetter);
        ethnicitySetter = (Spinner) findViewById(R.id.ethnicitySetter);
        monthSetter = (Spinner) findViewById(R.id.monthSetter);
        daySetter = (Spinner) findViewById(R.id.daySetter);
        yearSetter = (Spinner) findViewById(R.id.yearSetter);

        setUpGenderSetter();
        setUpLangSetter();
        setUpEthnicitySetter();
        setUpDaySetter();
        setUpMonthSetter();
        setUpYearSetter();

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
        final String licensenum = mLicense.getText().toString();
        final String firstname = mFirstNameField.getText().toString();
        final String lastname = mLastNameField.getText().toString();
        final String phone = mPhone.getText().toString();
        final String zip = mZip.getText().toString();
        final String department = mDepartment.getText().toString();
        final String badge = mBadge.getText().toString();

        Log.d(TAG, "createAccount:" + email);


        if (!validateInput(email, password, licensenum, firstname, lastname, department,
                badge, phone, zip)) {
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
                            // Store the officer's ID in the inputted department and set to be active
                            // An active officer in a department means that the officer currently works
                            // in that department
                            databaseRef.child("department").child(department).child(uuid).setValue("Active");

                            //Set up officer profile
                            DatabaseReference officerDatabaseRef = databaseRef.child("officer")
                                    .child(uuid).child("profile").getRef();

                            officerDatabaseRef.child("email").setValue(email);
                            officerDatabaseRef.child("first_name").setValue(firstname);
                            officerDatabaseRef.child("last_name").setValue(lastname);
                            officerDatabaseRef.child("license_number").setValue(licensenum);
                            officerDatabaseRef.child("phone_number").setValue(phone);
                            officerDatabaseRef.child("zip_code").setValue(zip);
                            //DATE OF BIRTH
                            officerDatabaseRef.child("day").setValue(i_day);
                            officerDatabaseRef.child("month").setValue(i_month);
                            officerDatabaseRef.child("year").setValue(i_year);

                            officerDatabaseRef.child("gender").setValue(i_gender);
                            officerDatabaseRef.child("language").setValue(i_language);
                            officerDatabaseRef.child("ethnicity").setValue(i_ethnicity);

                            officerDatabaseRef.child("department").setValue(department);
                            officerDatabaseRef.child("badge").setValue(badge);

                            Intent i = new Intent(getBaseContext(), HomepageActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                });
        // [END create_user_with_email]
        //you signed up, CONGRATS


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

    private void setUpDaySetter() {
        final ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Day_31, android.R.layout.simple_spinner_dropdown_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySetter.setAdapter(dayAdapter);
        daySetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i_day = position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    private void setUpYearSetter() {
        final ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.Year, android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSetter.setAdapter(yearAdapter);
        yearSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i_year = position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }


    private void setUpMonthSetter() {
        final ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.Month, android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSetter.setAdapter(monthAdapter);
        monthSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i_month = position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
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

    private boolean validateInput(String email, String password, String licensenum, String firstname,
                                  String lastname, String department, String badge, String phone,
                                  String zip) {

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

        //DEPARTMENT CHECK
        if (department.isEmpty()) {
            mDepartment.setError("Enter your department number");
            mDepartment.requestFocus();
            return false;
        } else {
            mDepartment.setError(null);
        }

        //BADGE CHECK
        if (badge.isEmpty()) {
            mBadge.setError("Enter your badge number");
            mBadge.requestFocus();
            return false;
        } else {
            mBadge.setError(null);
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
            mLicense.setError("Enter a letter followed by eight numbers\nExample: A12345678");
            mLicense.requestFocus();
            return false;
        } else {
            mLicense.setError(null);
        }

        //PHONE REGEX
        if(!SignupVerification.validPhone(phone)) {
            mPhone.setError("Invalid Phone Number.");
            mPhone.requestFocus();
            return false;
        } else {
            mPhone.setError(null);
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

        //DATE OF BIRTH
        mDateOfBirth.clearFocus();
        if (!SignupVerification.validDateOfBirth(i_month, i_day, 1909 + getResources().getStringArray(R.array.Year).length - i_year )) {
            mDateOfBirth.setError("Invalid Date of Birth.");
            mDateOfBirth.requestFocus();
            return false;
        } else {
            mDateOfBirth.setError(null);
        }


        return true;
    }

} // end class SignupformActivity

