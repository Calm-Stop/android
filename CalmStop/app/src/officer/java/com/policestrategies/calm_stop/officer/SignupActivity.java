package com.policestrategies.calm_stop.officer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.policestrategies.calm_stop.officer.dashboard.DashboardActivity;

/**
 * Allows the user to sign up or return to the log in page.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner genderSetter;
    private int i_gender;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mDepartmentField;
    private EditText mBadgeField;

    private static final String TAG = "Signup";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailField = (EditText) findViewById(R.id.signup_input_email);
        mPasswordField = (EditText) findViewById(R.id.signup_input_password);
        mFirstNameField = (EditText) findViewById(R.id.signup_input_firstname);
        mLastNameField = (EditText) findViewById(R.id.signup_input_lastname);
        mDepartmentField = (EditText) findViewById(R.id.signup_input_department_number);
        mBadgeField = (EditText) findViewById(R.id.signup_input_badge_number);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);

        setUpGenderSetter();

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_signup).setOnClickListener(this);

        databaseRef = FirebaseDatabase.getInstance().getReference();
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

    @Override
    public void onBackPressed() {
        navigateToLoginActivity();
    }

    /**
     * Any onClicks that we register will be handled in here
     */
    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.button_login:
                navigateToLoginActivity();
                break;

            case R.id.button_signup:
                mProgressDialog = ProgressDialog.show(this, "", "Signing up", true, false);
                signup();
                break;

        }
    }

    private void navigateToLoginActivity() {
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Begins the signup process. Validates the input and - if input is valid - attempts to create
     * an account.
     */
    private void signup() {
        final String email = mEmailField.getText().toString();
        final String password = mPasswordField.getText().toString();
        final String firstname = mFirstNameField.getText().toString();
        final String lastname = mLastNameField.getText().toString();
        final String department = mDepartmentField.getText().toString();
        final String badge = mBadgeField.getText().toString();

        Log.d(TAG, "createAccount:" + email);


        if (!validateInput(email, password, firstname, lastname, department, badge)) {
            SharedUtil.dismissProgressDialog(mProgressDialog);
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
                        if (!task.isSuccessful()) {
                            SharedUtil.dismissProgressDialog(mProgressDialog);
                            Toast.makeText(SignupActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user == null) {
                                SharedUtil.dismissProgressDialog(mProgressDialog);
                                Toast.makeText(SignupActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String uuid = user.getUid();

                            //Set up officer profile
                            DatabaseReference officerDatabaseRef = databaseRef.child("officer")
                                    .child(department).child(uuid).child("profile").getRef();

                            officerDatabaseRef.child("email").setValue(email);
                            officerDatabaseRef.child("first_name").setValue(firstname);
                            officerDatabaseRef.child("last_name").setValue(lastname);
                            officerDatabaseRef.child("gender").setValue(i_gender);
                            officerDatabaseRef.child("department").setValue(department);
                            officerDatabaseRef.child("badge").setValue(badge);

                            String photoPath;
                            if (i_gender == 0) {
                                photoPath = "images/profile/default_female";
                            } else {
                                photoPath = "images/profile/default_male";
                            }

                            officerDatabaseRef.child("photo").setValue(photoPath);


                            //Set up empty ratings section
                            DatabaseReference officerRatingDatabaseRef = databaseRef.child("officer")
                                    .child(department).child(uuid).child("ratings").getRef();
                            officerRatingDatabaseRef.child("avg_rating").setValue(0);
                            officerRatingDatabaseRef.child("number_of_ratings").setValue(0);

                            //Set up empty comments section
//                            DatabaseReference officerCommentsDatabaseRef = databaseRef.child("officer")
//                                    .child(department).child(uuid).child("comments").getRef();

                            getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
                                    .edit().putString(getString(R.string.shared_preferences_department_number),
                                    department).commit();

                            Toast.makeText(SignupActivity.this, "Validation success!",
                                    Toast.LENGTH_SHORT).show();
                            SharedUtil.dismissProgressDialog(mProgressDialog);
                            Intent i = new Intent(getBaseContext(), DashboardActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                });

    } // end signup()

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

    private boolean validateInput(String email, String password, String firstname,
                                  String lastname, String department, String badge) {

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

        //DEPARTMENT CHECK
        if (department.isEmpty()) {
            mDepartmentField.setError("Enter your department number");
            mDepartmentField.requestFocus();
            return false;
        } else {
            mDepartmentField.setError(null);
        }

        //BADGE CHECK
        if (badge.isEmpty()) {
            mBadgeField.setError("Enter your badge number");
            mBadgeField.requestFocus();
            return false;
        } else {
            mBadgeField.setError(null);
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

        return true;
    }

} // end class SignupformActivity

