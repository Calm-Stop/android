package com.policestrategies.calm_stop.officer;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.RegexChecks;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner genderSetter;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;

    private String mDepartmentNumber;

    private EditText mDepartmentNum;
    private EditText mBadgeNum;
    private ImageView mphoto;

    private static final int chosenImage = 1;

    private int i_gender;

    private String FName;
    private String LName;
    private String Email;

    private String Department;
    private String Badge;

    private Uri Photo;
    private String officerUid;

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference profileRef;

    private String valueEmail;
    private String valuePassword;

    private static final String TAG = "Profile Edit";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDepartmentNumber = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE).getString(getString(R.string.shared_preferences_department_number),
                "");

        //mFname, mLname, memail, mDOB, mphoneNum, mLicense, mZip, mLanguage, mphoto
        mFirstNameField = (EditText)findViewById(R.id.editFirstName);
        mLastNameField = (EditText)findViewById(R.id.editLastName);
        mEmailField = (EditText)findViewById(R.id.editEmail);
        mDepartmentNum = (EditText)findViewById(R.id.editdepartnum);
        mBadgeNum = (EditText)findViewById(R.id.editBadgenum);

        mphoto = (ImageView)findViewById(R.id.profilePicture);
        mphoto.setOnClickListener(this);

        findViewById(R.id.backbutton).setOnClickListener(this);
        findViewById(R.id.savebutton).setOnClickListener(this);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);

        setUpGenderSetter();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            officerUid = "";
        }
        else{
            officerUid = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("officer");
        profileRef = mDatabase.child(mDepartmentNumber).child(officerUid).child("profile");

        Photo = user.getPhotoUrl();

        if(Photo == null) {
            //mphoto.setImageURI();
        }else {
            mphoto.setImageURI(Photo);
        }

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //NEED TO ADD BADGE NUMBER
                Email = snapshot.child("email").getValue().toString();
                FName = snapshot.child("first_name").getValue().toString();
                LName = snapshot.child("last_name").getValue().toString();
                Department = snapshot.child("department").getValue().toString();
                Badge = snapshot.child("badge").getValue().toString();
                i_gender = Integer.parseInt(snapshot.child("gender").getValue().toString());
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

            case R.id.savebutton:

                FName = mFirstNameField.getText().toString();
                LName = mLastNameField.getText().toString();
                Email = mEmailField.getText().toString();
                Department = mDepartmentNum.getText().toString();
                Badge = mBadgeNum.getText().toString();

                if (!validateInput(FName, LName, Department, Badge, Email)) return;
                //WRITE TO FIREBASE
                updateFName();
                updateLName();
                updatePhoto();
                updateEmail();
                updateGender();
                updateDepartment();
                updateBadge();
                updateDOB();

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
                i_gender = position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }


    private void setEverything() {

        mFirstNameField.setText(FName);
        mLastNameField.setText(LName);
        mDepartmentNum.setText(Department);
        mBadgeNum.setText(Badge);
        mEmailField.setText(Email);
        genderSetter.setSelection(i_gender);
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
    //mFname, mLname, memail, mDOB, mphoneNum, mLicense, mZip, mLanguage, mphoto
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
    private void updateDepartment() { profileRef.child("department").setValue(Department); }
    private void updateBadge() { profileRef.child("badge").setValue(Badge); }
    private void updateDOB() {
    }
    private void updateGender() { profileRef.child("gender").setValue(i_gender); }




    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

    private boolean validateInput(String firstname, String lastname, String department, String badge, String email
                                  ) {

//FName, LName, Department, Badge, Email, PhoneNumber, ZIP, License, DOB
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
        if (!RegexChecks.validDepartment(department)) {
            mDepartmentNum.setError("Enter your Department Number.");
            mDepartmentNum.requestFocus();
            return false;
        } else {
            mDepartmentNum.setError(null);
        }

        //BADGE NUMBER CHECK
        if (!RegexChecks.validBadge(badge)) {
            mBadgeNum.setError("Enter your Badge Number.");
            mBadgeNum.requestFocus();
            return false;
        } else {
            mBadgeNum.setError(null);
        }

        //EMAIL CHECK
        if (!RegexChecks.validEmail(email)) {
            mEmailField.setError("Enter a valid email address.");
            mEmailField.requestFocus();
            return false;
        } else {
            mEmailField.setError(null);
        }

        return true;
    }
} // end ProfileActivity
