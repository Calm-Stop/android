package com.policestrategies.calm_stop.citizen;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.RegexChecks;
import com.policestrategies.calm_stop.SharedUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Allows the user to view and edit their profile.
 * Created by mariavizcaino on 2/19/17.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner genderSetter;
    private Spinner ethnicitySetter;
    private Spinner languageSetter;
    private int i_gender, i_ethnicity, i_language;


    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mDateOfBirthField;
    private EditText mPhoneNumberField;
    private EditText mZipField;
    private EditText mLicenseNumberField;

    private ImageView mImageView;
    private Uri mUserPhoto;

    private static final int chosenImage = 1;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mProfileReference;

    private String valueEmail;
    private String valuePassword;

    private static final String TAG = "ProfileActivity";

    private ProgressDialog mProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);

        mFirstNameField = (EditText)findViewById(R.id.profile_input_firstname);
        mLastNameField = (EditText)findViewById(R.id.profile_input_lastname);
        mEmailField = (EditText)findViewById(R.id.profile_input_email);
        mDateOfBirthField = (EditText) findViewById(R.id.profile_input_dob);
        mPhoneNumberField = (EditText)findViewById(R.id.profile_input_phone_number);
        mLicenseNumberField = (EditText)findViewById(R.id.profile_input_license_number);
        mZipField = (EditText)findViewById(R.id.profile_input_zipcode);

        mPhoneNumberField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mImageView = (ImageView)findViewById(R.id.profilePicture);
        mImageView.setOnClickListener(this);

        findViewById(R.id.savebutton).setOnClickListener(this);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);
        ethnicitySetter = (Spinner) findViewById(R.id.ethnicitySetter);
        languageSetter = (Spinner) findViewById(R.id.languageSetter);

        setUpCalendar();
        setUpGenderSetter();
        setUpEthnicitySetter();
        setUpLanguageSetter();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            mProfileReference = FirebaseDatabase.getInstance().getReference("citizen")
                    .child(mCurrentUser.getUid()).child("profile");

        }

        mUserPhoto = mCurrentUser.getPhotoUrl();

        if (mUserPhoto != null) {
            mImageView.setImageURI(mUserPhoto);
        }

        mProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String zip = snapshot.child("zip_code").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        String firstName = snapshot.child("first_name").getValue().toString();
                        String lastName = snapshot.child("last_name").getValue().toString();
                        String license = snapshot.child("license_number").getValue().toString();
                        String phoneNumber = snapshot.child("phone_number").getValue().toString();
                        String dateOfBirth = snapshot.child("dob").getValue().toString();
                        i_gender = Integer.parseInt(snapshot.child("gender").getValue().toString());
                        i_language = Integer.parseInt(snapshot.child("language").getValue().toString());
                        i_ethnicity = Integer.parseInt(snapshot.child("ethnicity").getValue().toString());

                        mZipField.setText(zip);
                        mEmailField.setText(email);
                        mFirstNameField.setText(firstName);
                        mLastNameField.setText(lastName);
                        mLicenseNumberField.setText(license);
                        mPhoneNumberField.setText(phoneNumber);
                        mDateOfBirthField.setText(dateOfBirth);

                        SharedUtil.dismissProgressDialog(mProgressDialog);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Failed to read app title value.", error.toException());
                    }

                });
    }

    @Override
    public void onBackPressed() {
        toProfileDisplay();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.savebutton:

                String firstName = mFirstNameField.getText().toString();
                String lastName = mLastNameField.getText().toString();
                String email = mEmailField.getText().toString();
                String phoneNumber = mPhoneNumberField.getText().toString();
                String license = mLicenseNumberField.getText().toString();
                String zip = mZipField.getText().toString();
                String dob = mDateOfBirthField.getText().toString();

                if (!validateInput(email, license, firstName, lastName, phoneNumber, zip, dob)) {
                    return;
                }

                //WRITE TO FIREBASE
                updateFName(firstName);
                updateLName(lastName);
                updatePhoto();
                updatePhoneNumber(phoneNumber);
                updateEmail(email);
                updateLicense(license);
                updateZip(zip);
                updateDOB(dob);
                updateLanguage();
                updateGender();
                updateEthnicity();

                recreate();
                break;

            case R.id.profilePicture:
                Intent pics = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pics.setType("image/");
                startActivityForResult(pics, chosenImage);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == chosenImage && resultCode == RESULT_OK && data != null){
            mUserPhoto = data.getData();
            mImageView.setImageURI(mUserPhoto);
        }
        else
            Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_LONG).show();

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
            }
        });
    }

    private void setUpLanguageSetter() {

        final ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.Language, android.R.layout.simple_spinner_dropdown_item);

        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSetter.setAdapter(languageAdapter);

        languageSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                i_language = position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void authentication() {
        AlertDialog.Builder authen = new AlertDialog.Builder(this);
        authen.setTitle("Reauthentication");
        authen.setMessage("Please Confirm Old Email and Password");

        final EditText authEmail = new EditText(this);
        final EditText authPassword = new EditText(this);

        authen.setView(authEmail);
        authen.setView(authPassword);

        authen.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                valueEmail = authEmail.getText().toString();
                valuePassword = authPassword.getText().toString();
                dialog.dismiss();
            }
        });

        authen.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        authen.show();
    }

    private void updateEmail(String email) {
        mProfileReference.child("email").setValue(email);
        mCurrentUser.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                        else {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthRecentLoginRequiredException e){

                                authentication();

                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(valueEmail, valuePassword );
                                mCurrentUser.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(TAG, "User re-authenticated.");
                                            }
                                        });

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }


    private void updatePhoto() {
        mProfileReference.child("photo").setValue(mUserPhoto);
        UserProfileChangeRequest updatePhoto = new UserProfileChangeRequest.Builder()
                .setPhotoUri(mUserPhoto)
                .build();

        mCurrentUser.updateProfile(updatePhoto)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                        else
                            Toast.makeText(ProfileActivity.this, "Error Uploading Image",
                                    Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void updateFName(String firstName) {
        mProfileReference.child("first_name").setValue(firstName);
    }

    private void updateLName(String lastName) {
        mProfileReference.child("last_name").setValue(lastName);
    }

    private void updateLicense(String license) {
        mProfileReference.child("license_number").setValue(license);
    }

    private void updatePhoneNumber(String phone) {
        mProfileReference.child("phone_number").setValue(phone);
    }

    private void updateZip(String zip) {
        mProfileReference.child("zip_code").setValue(zip);
    }

    private void updateDOB(String dob) {
        mProfileReference.child("dob").setValue(dob);
    }

    private void updateLanguage() {
        mProfileReference.child("language").setValue(i_language);
    }

    private void updateEthnicity() {
        mProfileReference.child("ethnicity").setValue(i_ethnicity);
    }

    private void updateGender() {
        mProfileReference.child("gender").setValue(i_gender);
    }

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

    private void toDocuments() {
        Intent i = new Intent(getBaseContext(), DocumentsActivity.class);
        startActivity(i);
    }

    private void toProfileDisplay() {
        Intent i = new Intent(getBaseContext(), ProfileDisplayActivity.class);
        startActivity(i);
    }

    private boolean validateInput(String email, String licensenum, String firstname,
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

        //DRIVER'S LICENSE REGEX (CALIFORNIA FORMAT)
        if(!RegexChecks.validLicense(licensenum)) {
            mLicenseNumberField.setError("Enter a letter followed by eight numbers\n" +
                    "Example: A12345678");
            mLicenseNumberField.requestFocus();
            return false;
        } else {
            mLicenseNumberField.setError(null);
        }

        //PHONE REGEX
        if(!RegexChecks.validPhone(phone)) {
            mPhoneNumberField.setError("Invalid Phone Number.");
            mPhoneNumberField.requestFocus();
            return false;
        } else {
            mPhoneNumberField.setError(null);
        }

        if (!RegexChecks.validDateOfBirth(dateOfBirth)) {
            mDateOfBirthField.performClick();
            Toast.makeText(ProfileActivity.this, "Please enter your date of birth",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        //ZIP CODE REGEX
        if (!RegexChecks.validZip(zip)){
            mZipField.setError("This field was left empty.");
            mZipField.requestFocus();
            return false;
        } else {
            mZipField.setError(null);
        }

        return true;
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
                new DatePickerDialog(ProfileActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

} // end class ProfileActivity
