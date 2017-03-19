package com.policestrategies.calm_stop.citizen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.server.converter.StringToIntConverter;
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
import com.policestrategies.calm_stop.SignupVerification;

import java.util.Calendar;


/**
 * Created by mariavizcaino on 2/19/17.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner genderSetter;
    private Spinner ethnicitySetter;
    private Spinner languageSetter;
    private Spinner daySetter;
    private Spinner monthSetter;
    private Spinner yearSetter;
    private int i_month, i_day, i_year, i_gender, i_ethnicity, i_language;


    private TextView mDateOfBirth;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mPhone;
    private EditText mZip;
    private EditText mLicense;
    private ImageView mphoto;

    private static final int chosenImage = 1;

    private String FName;
    private String LName;
    private String Email;
    private String PhoneNumber;

    private String License;
    private String ZIP;

    private Uri Photo;
    private String citizenUid;

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference profileRef;

    private String valueEmail;
    private String valuePassword;

    private static final String TAG = "Profile Edit";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //mFname, mLname, memail, mDOB, mphoneNum, mLicense, mZip, mLanguage, mphoto
        mFirstNameField = (EditText)findViewById(R.id.editFirstName);
        mLastNameField = (EditText)findViewById(R.id.editLastName);
        mEmailField = (EditText)findViewById(R.id.editEmail);
        mDateOfBirth = (TextView) findViewById(R.id.text_dateOfBirth);
        mPhone = (EditText)findViewById(R.id.editPhonenum);
        mLicense= (EditText)findViewById(R.id.editLicenseNumber);
        mZip = (EditText)findViewById(R.id.editZip);

        mphoto = (ImageView)findViewById(R.id.profilePicture);
        mphoto.setOnClickListener(this);

        findViewById(R.id.backbutton).setOnClickListener(this);
        findViewById(R.id.viewDocs).setOnClickListener(this);
        findViewById(R.id.savebutton).setOnClickListener(this);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);
        ethnicitySetter = (Spinner) findViewById(R.id.ethnicitySetter);
        languageSetter = (Spinner) findViewById(R.id.languageSetter);
        monthSetter = (Spinner) findViewById(R.id.monthSetter);
        daySetter = (Spinner) findViewById(R.id.daySetter);
        yearSetter = (Spinner) findViewById(R.id.yearSetter);

        setUpGenderSetter();
        setUpEthnicitySetter();
        setUpLanguageSetter();
        setUpMonthSetter();
        setUpDaySetter();
        setUpYearSetter();

        mDatabase = FirebaseDatabase.getInstance().getReference("citizen");

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
           citizenUid = "";
        }
        else{
            citizenUid = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("citizen");
        profileRef = mDatabase.child(citizenUid).child("profile");

        Photo = user.getPhotoUrl();

        if(Photo == null) {
            //mphoto.setImageURI();
        }else {
            mphoto.setImageURI(Photo);
        }profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //NEED TO ADD BADGE NUMBER
                        ZIP = snapshot.child("zip_code").getValue().toString();
                        Email = snapshot.child("email").getValue().toString();
                        FName = snapshot.child("first_name").getValue().toString();
                        LName = snapshot.child("last_name").getValue().toString();
                        License = snapshot.child("license_number").getValue().toString();
                        PhoneNumber = snapshot.child("phone_number").getValue().toString();
                        i_month = Integer.parseInt(snapshot.child("month").getValue().toString());
                        i_day = Integer.parseInt(snapshot.child("day").getValue().toString());
                        i_year = Integer.parseInt(snapshot.child("year").getValue().toString());
                        i_gender = Integer.parseInt(snapshot.child("gender").getValue().toString());
                        i_language = Integer.parseInt(snapshot.child("language").getValue().toString());
                        i_ethnicity = Integer.parseInt(snapshot.child("ethnicity").getValue().toString());
                        setEverything();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Failed to read app title value.", error.toException());
                    }

                });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.backbutton:
                toHomepage();
                break;

            case R.id.viewDocs:
                toDocuments();
                break;

            case R.id.savebutton:

                FName = mFirstNameField.getText().toString();
                LName = mLastNameField.getText().toString();
                Email = mEmailField.getText().toString();
                PhoneNumber = mPhone.getText().toString();
                License = mLicense.getText().toString();
                ZIP = mZip.getText().toString();

                if (!validateInput(Email, License, FName, LName, PhoneNumber, ZIP)) return;
                //WRITE TO FIREBASE
                updateFName();
                updateLName();
                updatePhoto();
                updatePhoneNumber();
                updateEmail();
                updateLicense();
                updateZip();
                updateDOB();
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
            Photo = data.getData();
            mphoto.setImageURI(Photo);
        }
        else
            Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_LONG).show();

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
                // TODO Auto-generated method stub
            }
        });
    }

    private void setEverything() {

        mFirstNameField.setText(FName);
        mLastNameField.setText(LName);
        mEmailField.setText(Email);
        mPhone.setText(PhoneNumber);
        mZip.setText(ZIP);
        mLicense.setText(License);
        monthSetter.setSelection(i_month);
        daySetter.setSelection(i_day);
        yearSetter.setSelection(i_year);
        genderSetter.setSelection(i_gender);
        ethnicitySetter.setSelection(i_ethnicity);
        languageSetter.setSelection(i_language);
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
                return;
            }
        });

        authen.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        });

        authen.show();
    }

    private void updateEmail() {
        profileRef.child("email").setValue(Email);
        user.updateEmail(Email)
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
                                user.reauthenticate(credential)
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
        profileRef.child("photo").setValue(Photo);
        UserProfileChangeRequest updatePhoto = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Photo)
                .build();

        user.updateProfile(updatePhoto)
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


    private void updateFName() { profileRef.child("first_name").setValue(FName); }

    private void updateLName() { profileRef.child("last_name").setValue(LName); }

    private void updateLicense() { profileRef.child("license_number").setValue(License); }

    private void updatePhoneNumber() { profileRef.child("phone_number").setValue(PhoneNumber); }

    private void updateZip() {profileRef.child("zip_code").setValue(ZIP);}

    private void updateDOB() {
        profileRef.child("month").setValue(i_month);
        profileRef.child("day").setValue(i_day);
        profileRef.child("year").setValue(i_year);
    }

    private void updateLanguage() { profileRef.child("language").setValue(i_language); }

    private void updateEthnicity() { profileRef.child("ethnicity").setValue(i_ethnicity); }

    private void updateGender() { profileRef.child("gender").setValue(i_gender); }

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

    private void toDocuments() {
        Intent i = new Intent(getBaseContext(), DocumentsActivity.class);
        startActivity(i);
    }

    private boolean validateInput(String email, String licensenum, String firstname,
                                  String lastname, String phone, String zip) {

//(8 fns)validEmail, validPassword, validLicense, validZip, validPhone, validFirstname, validLastname, validDateOfBirth
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

        //DATE OF BIRTH REGEX
        mDateOfBirth.clearFocus();
        if (!SignupVerification.validDateOfBirth(i_month, i_day, getResources().getStringArray(R.array.Year).length - i_year + 1909 )){
            mDateOfBirth.setError("Invalid Date of Birth");
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
}
