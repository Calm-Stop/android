package com.policestrategies.calm_stop.officer;

import android.app.ProgressDialog;
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

    private EditText mBadgeNum;
    private ImageView mphoto;

    private DatabaseReference profileReference;

    private static final int chosenImage = 1;

    private int i_gender;
    private Uri Photo;
    private String officerUid;

    private FirebaseUser mCurrentUser;

    private String valueEmail;
    private String valuePassword;

    private static final String TAG = "ProfileActivity";

    private ProgressDialog mProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);

        mFirstNameField = (EditText)findViewById(R.id.profile_input_firstname);
        mLastNameField = (EditText)findViewById(R.id.profile_input_lastname);
        mEmailField = (EditText)findViewById(R.id.profile_input_email);
        mBadgeNum = (EditText)findViewById(R.id.profile_input_badge_number);

        mphoto = (ImageView)findViewById(R.id.profilePicture);
        mphoto.setOnClickListener(this);

        findViewById(R.id.backbutton).setOnClickListener(this);
        findViewById(R.id.savebutton).setOnClickListener(this);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);

        setUpGenderSetter();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {
            officerUid = "";
        } else {
            officerUid = mCurrentUser.getUid();
        }

        profileReference = FirebaseDatabase.getInstance().getReference("officer")
                .child(Utility.getCurrentDepartmentNumber(this)).child(officerUid).child("profile");

        Photo = mCurrentUser.getPhotoUrl();

        if(Photo == null) {
            //mphoto.setImageURI();
        } else {
            mphoto.setImageURI(Photo);
        }

        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue().toString();
                String firstName = snapshot.child("first_name").getValue().toString();
                String lastName = snapshot.child("last_name").getValue().toString();
                String badgeNumber = snapshot.child("badge").getValue().toString();
                int gender = Integer.parseInt(snapshot.child("gender").getValue().toString());

                mFirstNameField.setText(firstName);
                mLastNameField.setText(lastName);
                mBadgeNum.setText(badgeNumber);
                mEmailField.setText(email);
                genderSetter.setSelection(gender);

                Utility.dismissProgressDialog(mProgressDialog);
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

                String firstName = mFirstNameField.getText().toString();
                String lastName = mLastNameField.getText().toString();
                String email = mEmailField.getText().toString();
                String badgeNumber = mBadgeNum.getText().toString();

                if (!validateInput(firstName, lastName, email, badgeNumber)) {
                    return;
                }

                updateFName(firstName);
                updateLName(lastName);
                updatePhoto();
                updateEmail(email);
                updateGender(i_gender);
                updateBadge(badgeNumber);

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
    private void updateEmail(String email) {
        profileReference.child("email").setValue(email);
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
        profileReference.child("photo").setValue(Photo);
        UserProfileChangeRequest updatePhoto = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Photo)
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
        profileReference.child("first_name").setValue(firstName);
    }

    private void updateLName(String lastName) {
        profileReference.child("last_name").setValue(lastName);
    }

    private void updateBadge(String badgeNumber) {
        profileReference.child("badge").setValue(badgeNumber);
    }

    private void updateGender(int gender) {
        profileReference.child("gender").setValue(gender);
    }


    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

    private boolean validateInput(String firstname, String lastname, String email, String badge) {

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
