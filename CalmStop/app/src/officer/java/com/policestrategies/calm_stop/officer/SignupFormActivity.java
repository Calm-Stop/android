package com.policestrategies.calm_stop.officer;

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

/**
 * Allows the user to sign up or return to the log in page.
 */

public class SignupFormActivity extends AppCompatActivity implements View.OnClickListener {

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

    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference databaseRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupform);

        mEmailField = (EditText) findViewById(R.id.input_email);
        mPasswordField = (EditText) findViewById(R.id.input_password);
        mFirstNameField = (EditText) findViewById(R.id.input_firstname);
        mLastNameField = (EditText) findViewById(R.id.input_lastname);
        mDepartmentField = (EditText) findViewById(R.id.input_department_number);
        mBadgeField = (EditText) findViewById(R.id.input_badge);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);

        setUpGenderSetter();

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
        final String firstname = mFirstNameField.getText().toString();
        final String lastname = mLastNameField.getText().toString();
        final String department = mDepartmentField.getText().toString();
        final String badge = mBadgeField.getText().toString();

        Log.d(TAG, "createAccount:" + email);


        if (!validateInput(email, password, firstname, lastname, department, badge)) {
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
                        } else {

                            Toast.makeText(SignupFormActivity.this, "Validation success!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
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

                            //Set up empty ratings section
                            DatabaseReference officerRatingDatabaseRef = databaseRef.child("officer")
                                    .child(department).child(uuid).child("ratings").getRef();
                            officerRatingDatabaseRef.child("avg_rating").setValue(0);
                            officerRatingDatabaseRef.child("number_of_ratings").setValue(0);

                            //Set up empty comments section
                            DatabaseReference officerCommentsDatabaseRef = databaseRef.child("officer")
                                    .child(department).child(uuid).child("comments").getRef();

                            getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
                                    .edit().putString(getString(R.string.shared_preferences_department_number),
                                    department).commit();


                            Intent i = new Intent(getBaseContext(), HomepageActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                });
        // [END create_user_with_email]
        //you signed up, CONGRATS


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

