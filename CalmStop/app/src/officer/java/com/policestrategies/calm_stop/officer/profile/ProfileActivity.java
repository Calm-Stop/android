package com.policestrategies.calm_stop.officer.profile;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.RegexChecks;
import com.policestrategies.calm_stop.SharedUtil;
import com.policestrategies.calm_stop.officer.Utility;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner mGenderSpinner;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mBadgeNumberField;
    private ImageView mProfileImageView;

    private DatabaseReference profileReference;
    private StorageReference mStorageReference;

    private static final int RESULT_PICK_GALLERY_IMAGE = 1;

    private Uri Photo;
    private String officerUid;

    private FirebaseUser mCurrentUser;

    private String valueEmail;
    private String valuePassword;

    private static final String TAG = "ProfileActivity";

    private ProgressDialog mProgressDialog;

    private ProfileUpdater mProfileUpdater;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);

        mFirstNameField = (EditText)findViewById(R.id.profile_input_firstname);
        mLastNameField = (EditText)findViewById(R.id.profile_input_lastname);
        mEmailField = (EditText)findViewById(R.id.profile_input_email);
        mBadgeNumberField = (EditText)findViewById(R.id.profile_input_badge_number);

        mProfileImageView = (ImageView)findViewById(R.id.profilePicture);
        mProfileImageView.setOnClickListener(this);

        findViewById(R.id.savebutton).setOnClickListener(this);

        mGenderSpinner = (Spinner) findViewById(R.id.genderSetter);

        setUpGenderSetter();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {
            officerUid = "";
        } else {
            officerUid = mCurrentUser.getUid();
        }

        profileReference = FirebaseDatabase.getInstance().getReference("officer")
                .child(Utility.getCurrentDepartmentNumber(this)).child(officerUid).child("profile");
        mStorageReference = FirebaseStorage.getInstance().getReference();

        Photo = mCurrentUser.getPhotoUrl();

        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue().toString();
                String firstName = snapshot.child("first_name").getValue().toString();
                String lastName = snapshot.child("last_name").getValue().toString();
                String badgeNumber = snapshot.child("badge").getValue().toString();
                String photoPath = snapshot.child("photo").getValue().toString();
                int gender = Integer.parseInt(snapshot.child("gender").getValue().toString());

                mFirstNameField.setText(firstName);
                mLastNameField.setText(lastName);
                mBadgeNumberField.setText(badgeNumber);
                mEmailField.setText(email);
                mGenderSpinner.setSelection(gender);

                profileReference.child("photo").child(photoPath);

                Glide.with(ProfileActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(mStorageReference.child(photoPath))
                        .into(mProfileImageView);

                mProfileUpdater = new ProfileUpdater(email, firstName, lastName, badgeNumber,
                        photoPath, gender, profileReference, mStorageReference, officerUid);

                SharedUtil.dismissProgressDialog(mProgressDialog);
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

            case R.id.savebutton:

                String firstName = mFirstNameField.getText().toString();
                String lastName = mLastNameField.getText().toString();
                String email = mEmailField.getText().toString();
                String badgeNumber = mBadgeNumberField.getText().toString();
                int gender = mGenderSpinner.getSelectedItemPosition();

                if (!validateInput(firstName, lastName, email, badgeNumber)) {
                    return;
                }

                mProfileUpdater.updateProfile(email, firstName, lastName, badgeNumber, Photo, gender);

                break;

            case R.id.profilePicture:
                Intent pics = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pics.setType("image/");
                startActivityForResult(pics, RESULT_PICK_GALLERY_IMAGE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_PICK_GALLERY_IMAGE && resultCode == RESULT_OK && data != null){
            Photo = data.getData();
            mProfileImageView.setImageURI(Photo);
        }
        else
            Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_LONG).show();

    }

    private void setUpGenderSetter() {
        final ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.Gender, android.R.layout.simple_spinner_dropdown_item);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(genderAdapter);
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
            mBadgeNumberField.setError("Enter your Badge Number.");
            mBadgeNumberField.requestFocus();
            return false;
        } else {
            mBadgeNumberField.setError(null);
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
