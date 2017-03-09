package com.policestrategies.calm_stop.citizen;

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
import com.policestrategies.calm_stop.SignupVerification;


/**
 * Created by mariavizcaino on 2/19/17.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner genderSetter;
    private Spinner ethnicitySetter;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mPhone;
    private EditText mDateOfBirth;
    private EditText mAddress;
    private EditText mLicense;
    private EditText mLanguage;
    private ImageView mphoto;

    private static final int chosenImage = 1;

    private int gen = 0;
    private int eth = 0;

    private String FName;
    private String LName;
    private String email;
    private String PhoneNumber;

    private String License;
    private String Address;
    private String DOB;
    private String Gender;
    private String Ethnicity;
    private String Language;

    private Uri Photo;
    private String citizenUid;

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference profileRef;

    private String valueEmail;
    private String valuePassword;

    private static final String TAG = "Profile Edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //mFname, mLname, memail, mDOB, mphoneNum, mLicense, maddress, mLanguage, mphoto
        mFirstNameField = (EditText)findViewById(R.id.editFirstName);
        mLastNameField = (EditText)findViewById(R.id.editLastName);
        mEmailField = (EditText)findViewById(R.id.editEmail);
        mDateOfBirth = (EditText)findViewById(R.id.editDateOfBirth);
        mPhone = (EditText)findViewById(R.id.editPhonenum);
        mLicense= (EditText)findViewById(R.id.editLicenseNumber);
        mAddress = (EditText)findViewById(R.id.editAddress);
        mLanguage = (EditText)findViewById(R.id.editprefferedLang);

        mphoto = (ImageView)findViewById(R.id.profilePicture);
        mphoto.setOnClickListener(this);

        findViewById(R.id.backbutton).setOnClickListener(this);
        findViewById(R.id.viewDocs).setOnClickListener(this);
        findViewById(R.id.savebutton).setOnClickListener(this);


        genderSetter = (Spinner) findViewById(R.id.genderSetter);
        ethnicitySetter = (Spinner) findViewById(R.id.ethnicitySetter);

        setUpGenderSetter();
        setUpEthnicitySetter();


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
        }

        profileRef.child("profile"); //<-not part of database

        mDatabase.child(citizenUid).child("profile") //.child("profile") <-not part of database

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //NEED TO ADD BADGE NUMBER
                        Address = snapshot.child("address").getValue().toString();
                        DOB = snapshot.child("date_of_birth").getValue().toString();
                        email = snapshot.child("email").getValue().toString();
                        FName = snapshot.child("first_name").getValue().toString();
                        LName = snapshot.child("last_name").getValue().toString();
                        Gender = snapshot.child("gender").getValue().toString();
                        Language = snapshot.child("language").getValue().toString();
                        License = snapshot.child("license_number").getValue().toString();
                        PhoneNumber = snapshot.child("phone_number").getValue().toString();
                        Language = snapshot.child("language").getValue().toString();
                        Ethnicity = "";

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
                //FIXME
                toDocuments();
                break;

            case R.id.savebutton:
                //FIXME
                //TODO

                FName = mFirstNameField.getText().toString();
                LName = mLastNameField.getText().toString();
                email = mEmailField.getText().toString();
                PhoneNumber = mPhone.getText().toString();
                DOB = mDateOfBirth.getText().toString();
                License = mLicense.getText().toString();
                Address = mAddress.getText().toString();
                Language = mLanguage.getText().toString();
                Gender = genderSetter.getSelectedItem().toString();
                Ethnicity = ethnicitySetter.getSelectedItem().toString();

                if (!validateInput(email, License, FName, LName, PhoneNumber, Address, Gender, Language, DOB)) return;
                //WRITE TO FIREBASE
                updateFName(FName);
                updateLName(LName);
                updatePhoto();
                updatePhoneNumber(PhoneNumber);
                updateEmail(email);
                updateAddress(Address);
                updateDOB(DOB);
                updateLicense(License);
                updateLanguage(Language);
                updateGender(Gender);
                updateEthnicity(Ethnicity);

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


    private void setUpGenderSetter() {

        final ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.Gender, android.R.layout.simple_spinner_dropdown_item);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSetter.setAdapter(genderAdapter);

        genderSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0: //blank
                        gen = 0;
                        break;
                    case 1: //female
                        gen = 1;
                        break;
                    case 2: //male
                        gen = 2;
                        break;
                }
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
                switch(position) {
                    case 0: //blank
                        eth = 0;
                        break;
                    case 1: //American indian
                        eth = 1;
                        break;
                    case 2: //asian
                        eth = 2;
                        break;
                    case 3: //African american
                        eth = 3;
                        break;
                    case 4: //hispanic
                        eth = 4;
                        break;
                    case 5: //pacific islander
                        eth = 5;
                        break;
                    case 6: //white
                        eth = 6;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setEverything(){

        mFirstNameField.setText(FName);
        mLastNameField.setText(LName);
        mEmailField.setText(email);
        mPhone.setText(PhoneNumber);
        mAddress.setText(Address);
        mDateOfBirth.setText(DOB);
        mLicense.setText(License);
        mLanguage.setText(Language);
        genderSetter.setSelection(setGen());
        ethnicitySetter.setSelection(setEth());

    }

    private int setGen(){
        if(Gender.equalsIgnoreCase("male"))
            return 1;
        else if (Gender.equalsIgnoreCase("female"))
            return 0;
        return 2;
    }


    private int setEth(){
        if (Ethnicity.equalsIgnoreCase("American indian"))
            return 0;
        else if (Ethnicity.equalsIgnoreCase("Asian"))
            return 1;
        else if (Ethnicity.equalsIgnoreCase("African american"))
            return 2;
        else if (Ethnicity.equalsIgnoreCase("Hispanic"))
            return 3;
        else if (Ethnicity.equalsIgnoreCase("pacific islander"))
            return 4;
        else if (Ethnicity.equalsIgnoreCase("white"))
            return 5;
        return 6;
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
    //mFname, mLname, memail, mDOB, mphoneNum, mLicense, maddress, mLanguage, mphoto
    private void updateEmail(String email) {
        profileRef.child("email").setValue(email);
        user.updateEmail(email)
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
        //mDatabase.child(citizenUid).child("profile").child("photo").setValue(Photo);
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


    private void updateFName(String firstname) { profileRef.child("first_name").setValue(firstname); }

    private void updateLName(String lastname) { profileRef.child("last_name").setValue(lastname); }

    private void updatePhoneNumber(String phone) { profileRef.child("phone_number").setValue(phone); }

    private void updateAddress(String address) {
        profileRef.child("address").setValue(address);
    }

    private void updateDOB(String dateofbirth) { profileRef.child("date_of_birth").setValue(dateofbirth); }

    private void updateLicense(String license) { profileRef.child("license_number").setValue(license); }

    private void updateLanguage(String language){ profileRef.child("language").setValue(language); }

    private void updateGender(String gender) { profileRef.child("gender").setValue(gender); }

    private void updateEthnicity(String ethnicity) { profileRef.child("ethnicity").setValue(ethnicity); }

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

    private void toDocuments() {
        Intent i = new Intent(getBaseContext(), DocumentsActivity.class);
        startActivity(i);
    }

    private boolean validateInput(String email, String licensenum, String firstname,
                                  String lastname, String phone, String address, String gender, String language, String dateofbirth) {

//(8 fns)validEmail, validPassword, validLicense, validAddress, validPhone, validFirstname, validLastname, validDateOfBirth
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
}
