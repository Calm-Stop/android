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


/**
 * Created by mariavizcaino on 2/19/17.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner genderSetter;
    private Spinner ethnicitySetter;
    private EditText mFname;
    private EditText mLname;
    private EditText memail;
    private EditText mphoneNum;
    private EditText mDOB;
    private EditText maddress;
    private EditText mLicense;
    private EditText mLanguage;
    private ImageView mphoto;

    private static final int chosenImage = 1;

    private int gen = 0;
    private int eth = 0;

    private String FName;
    private String LName;
    private String Email;
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

    private String valueEmail;
    private String valuePassword;

    private static final String TAG = "Profile Edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFname = (EditText)findViewById(R.id.editFirstName);
        mLname = (EditText)findViewById(R.id.editLastName);
        memail = (EditText)findViewById(R.id.editEmail);
        mDOB = (EditText)findViewById(R.id.editDateOfBirth);
        mphoneNum = (EditText)findViewById(R.id.editPhonenum);
        mLicense= (EditText)findViewById(R.id.editLicenseNumber);
        maddress = (EditText)findViewById(R.id.editAddress);
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

        mDatabase = FirebaseDatabase.getInstance().getReference("citizen");

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
           citizenUid = "";
        }
        else{
            citizenUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        Photo = user.getPhotoUrl();

        if(Photo == null) {
            //mphoto.setImageURI();
        }else {
            mphoto.setImageURI(Photo);
        }

        mDatabase.child(citizenUid).child("profile") //.child("profile") <-not part of database
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //NEED TO ADD BADGE NUMBER
                        Address = snapshot.child("address").getValue().toString();
                        DOB = snapshot.child("date_of_birth").getValue().toString();
                        Email = snapshot.child("email").getValue().toString();
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

                FName = mFname.getText().toString();
                LName = mLname.getText().toString();
                Email = memail.getText().toString();
                PhoneNumber = mphoneNum.getText().toString();
                DOB = mDOB.getText().toString();
                License = mLicense.getText().toString();
                Address = maddress.getText().toString();
                Language = mLanguage.getText().toString();

                //WRITE TO FIREBASE
                updateFName();
                updateLName();
                updatePhoto();
                updatePhoneNumber();
                updateEmail();
                updateAddress();
                updateDOB();
                updateLicense();
                updateLanguage();
                updateGender();

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

        mFname.setText(FName);
        mLname.setText(LName);
        memail.setText(Email);
        mphoneNum.setText(PhoneNumber);
        maddress.setText(Address);
        mDOB.setText(DOB);
        mLicense.setText(License);
        mLanguage.setText(Language);
        genderSetter.setSelection(setGen());
        ethnicitySetter.setSelection(setEth());

    }

    private int setGen(){
        if(Gender.equalsIgnoreCase("male"))
            return 2;
        else if (Gender.equalsIgnoreCase("female"))
            return 1;
        return 0;
    }


    private int setEth(){
        if (Ethnicity.equalsIgnoreCase("American indian"))
            return 1;
        else if (Ethnicity.equalsIgnoreCase("Asian"))
            return 2;
        else if (Ethnicity.equalsIgnoreCase("African american"))
            return 3;
        else if (Ethnicity.equalsIgnoreCase("Hispanic"))
            return 4;
        else if (Ethnicity.equalsIgnoreCase("pacific islander"))
            return 5;
        else if (Ethnicity.equalsIgnoreCase("white"))
            return 6;
        return 0;
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
        mDatabase.child(citizenUid).child("profile").child("email").setValue(Email);
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

    private void updateFName() {
        mDatabase.child(citizenUid).child("profile").child("first_name").setValue(FName);
    }

    private void updateLName() {
        mDatabase.child(citizenUid).child("profile").child("last_name").setValue(LName);
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

    private void updatePhoneNumber() {
        mDatabase.child(citizenUid).child("profile").child("phone_number").setValue(PhoneNumber);
    }

    private void updateAddress() {
        mDatabase.child(citizenUid).child("profile").child("address").setValue(Address);
    }

    private void updateDOB() {
        mDatabase.child(citizenUid).child("profile").child("date_of_birth").setValue(DOB);

    }

    private void updateLicense() {
        mDatabase.child(citizenUid).child("profile").child("license_number").setValue(License);
    }

    private void updateLanguage(){
        mDatabase.child(citizenUid).child("profile").child("language").setValue(Language);
    }

    private void updateGender() {
        if(gen == 0)
            mDatabase.child(citizenUid).child("profile").child("gender").setValue(" ");
        else if(gen == 1)
            mDatabase.child(citizenUid).child("profile").child("gender").setValue("Female");
        else
            mDatabase.child(citizenUid).child("profile").child("gender").setValue("Male");

    }

    private void updateEthnicity() {
        if (eth == 1 )
            mDatabase.child(citizenUid).child("profile").child("ethnicity").setValue("American indian");
        else if (eth == 2 )
            mDatabase.child(citizenUid).child("profile").child("ethnicity").setValue("Asian");
        else if (eth == 3)
            mDatabase.child(citizenUid).child("profile").child("ethnicity").setValue("African American");
        else if (eth == 4)
            mDatabase.child(citizenUid).child("profile").child("ethnicty").setValue("Hispanic");
        else if (eth == 5)
            mDatabase.child(citizenUid).child("profile").child("ethnicity").setValue("pacific islander");
        else if (eth == 6)
            mDatabase.child(citizenUid).child("profile").child("ethnicity").setValue("white");
        else
            mDatabase.child(citizenUid).child("profile").child("ethnicity").setValue(" ");
    }

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

    private void toDocuments() {
        Intent i = new Intent(getBaseContext(), DocumentsActivity.class);
        startActivity(i);
    }

}
