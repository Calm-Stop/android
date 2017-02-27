package com.policestrategies.calm_stop.citizen;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import com.policestrategies.calm_stop.R;


/**
 * Created by mariavizcaino on 2/19/17.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner genderSetter;
    private Spinner ethnicitySetter;
    private EditText mname;
    private EditText memail;
    private EditText mphoneNum;
    private ImageView mphoto;

    private static final int chosenImage = 1;

    private int gen;
    private int eth;

    private String Name;
    private String Email;
    private String PhoneNumber;
    private String valueEmail;
    private String valuePassword;

    private Uri Photo;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAG = "Profile Edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mname = (EditText)findViewById(R.id.editName);
        memail = (EditText)findViewById(R.id.editEmail);
        mphoneNum = (EditText)findViewById(R.id.editPhonenum);
        mphoto = (ImageView)findViewById(R.id.profilePicture);

        findViewById(R.id.backbutton).setOnClickListener(this);
        findViewById(R.id.viewDocs).setOnClickListener(this);
        findViewById(R.id.savebutton).setOnClickListener(this);
        mphoto.setOnClickListener(this);

        genderSetter = (Spinner) findViewById(R.id.genderSetter);
        ethnicitySetter = (Spinner) findViewById(R.id.ethnicitySetter);

        setUpGenderSetter();
        setUpEthnicitySetter();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            Name = user.getDisplayName();
            Email = user.getEmail();
            Photo = user.getPhotoUrl();
            String photStr = Photo.toString();
        }

        mname.setText(Name);
        memail.setText(Email);

        if(Photo == null) {
            //mphoto.setImageURI();
        }else {
            mphoto.setImageURI(Photo);
        }

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

                Name = mname.getText().toString();
                Email = memail.getText().toString();
                PhoneNumber = mphoneNum.getText().toString();

                //WRITE TO FIREBASE
                updateName();
                updatePhoto();
                //updatePhoneNumber();
                updateEmail();

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
                    case 0:
                        //blank
                        gen = 0;
                        break;
                    case 1:
                        //female
                        gen = 1;
                        break;
                    case 2:
                        //male
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
                    case 0:
                        //blank
                        eth = 0;
                        break;
                    case 1:
                        //American indian
                        eth = 1;
                        break;
                    case 2:
                        //asian
                        eth = 2;
                        break;
                    case 3:
                        //African american
                        eth = 3;
                        break;
                    case 4:
                        //hispanic
                        eth = 4;
                        break;
                    case 5:
                        //pacific islander
                        eth = 5;
                        break;
                    case 6:
                        //white
                        eth = 6;
                        break;
                }
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

    private void updateEmail() {
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
                                        .getCredential(valueEmail,valuePassword );
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

    private void updateName() {
        UserProfileChangeRequest updateName = new UserProfileChangeRequest.Builder()
                .setDisplayName(Name)
                .build();

        user.updateProfile(updateName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                        else {
                            Toast.makeText(ProfileActivity.this, "Error Updating Name", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void updatePhoto() {
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
                            Toast.makeText(ProfileActivity.this, "Error Uploading Image", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void updatePhoneNumber() {

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
