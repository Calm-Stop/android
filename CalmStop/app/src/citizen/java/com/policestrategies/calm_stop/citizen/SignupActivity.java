package com.policestrategies.calm_stop.citizen;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import com.policestrategies.calm_stop.RegexChecks;
import com.policestrategies.calm_stop.SharedUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Allows the user to sign up or return to the log in page.
 */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

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

    private static final String TAG = "SignupActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        mPhoneNumberField = (EditText) findViewById(R.id.signup_input_phone_number);
        mZipField = (EditText) findViewById(R.id.signup_input_zipcode);
        mDateOfBirthField = (EditText) findViewById(R.id.signup_input_dob);

        mPhoneNumberField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        findViewById(R.id.signup_button_login).setOnClickListener(this);
        findViewById(R.id.signup_button_signup).setOnClickListener(this);

        setUpCalendar();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    } // end onCreate

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
            case R.id.signup_button_login:
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.signup_button_signup: // Signup was pressed, begin the SignupActivity
                mProgressDialog = ProgressDialog.show(this, "", "Logging in", true, false);
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

        Log.d(TAG, "Creating account: " + email);

        if (!validateInput(email, password, licensenum, firstname, lastname, phone, zip, dateOfBirth)) {
            SharedUtil.dismissProgressDialog(mProgressDialog);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful() || mAuth.getCurrentUser() == null)  {
                            SharedUtil.dismissProgressDialog(mProgressDialog);
                            Toast.makeText(SignupActivity.this, "Error signing up",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uuid = user.getUid();
                            DatabaseReference citizenDatabaseRef = databaseRef.child("citizen")
                                    .child(uuid).child("profile").getRef();

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

                            SharedUtil.dismissProgressDialog(mProgressDialog);

                            Intent i = new Intent(getBaseContext(), HomepageActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                });
    } // end signup

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
            mPasswordField.setError("Password must be at least 6 characters and contain at least" +
                    " a letter and a number.");
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
            Toast.makeText(SignupActivity.this, "Please enter your date of birth",
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
            public void onNothingSelected(AdapterView<?> arg0) {}
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
            public void onNothingSelected(AdapterView<?> arg0) {}
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
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }

    private void setUpCalendar() {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
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
                new DatePickerDialog(SignupActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

} // end class SignupActivity

