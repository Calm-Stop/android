package com.policestrategies.calm_stop.citizen;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.R.attr.id;


/**
 * Allows the user to view and edit their profile.
 * Created by mariavizcaino on 2/19/17.
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner genderSetter;
    private Spinner ethnicitySetter;
    private Spinner languageSetter;

    private String Gender;
    private String Language;
    private String Ethnicity;


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


        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/avenir-next.ttf");

        TextView Title = (TextView) findViewById(R.id.title);

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

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        Title.setTypeface(custom_font);

        //Toast.makeText(ProfileActivity.this, "Error1", Toast.LENGTH_SHORT).show();
        if (mCurrentUser == null) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            mProfileReference = FirebaseDatabase.getInstance().getReference("citizen")
                    .child(mCurrentUser.getUid()).child("profile");
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
                Language = snapshot.child("language").getValue().toString();
                Ethnicity = snapshot.child("ethnicity").getValue().toString();
                Gender = snapshot.child("gender").getValue().toString();

                mZipField.setText(zip);
                mEmailField.setText(email);
                mFirstNameField.setText(firstName);
                mLastNameField.setText(lastName);
                mLicenseNumberField.setText(license);
                mPhoneNumberField.setText(phoneNumber);
                mDateOfBirthField.setText(dateOfBirth);
                languageSetter.setSelection(getLang(Language));
                ethnicitySetter.setSelection(getEth(Ethnicity));
                genderSetter.setSelection(getGen(Gender));

                loadProfileImage();

                SharedUtil.dismissProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }

        });
        //Toast.makeText(ProfileActivity.this, "LANGUAGE: " + Language + Ethnicity + Gender, Toast.LENGTH_SHORT).show();

        setUpCalendar();
        setUpGenderSetter();
        setUpEthnicitySetter();
        setUpLanguageSetter();

        //Toast.makeText(ProfileActivity.this, "Error3", Toast.LENGTH_SHORT).show();

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
                //updatePhoto();
                updatePhoneNumber(phoneNumber);
                updateEmail(email);
                updateLicense(license);
                updateZip(zip);
                updateDOB(dob);
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
            mUserPhoto = data.getData();

            Bitmap myImage = convertUriToBitmap(mUserPhoto);
            myImage = getRoundedShape(myImage);

            mImageView.setImageBitmap(myImage);
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
                //i_gender = position;
                Gender = genderAdapter.getItem(position).toString();
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
                //i_ethnicity = position;
                Ethnicity = ethnicityAdapter.getItem(position).toString();
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
                //i_language = position;
                Language = languageAdapter.getItem(position).toString();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void authentication() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View alertBox = inflater.inflate(R.layout.activity_alertdialog, null);

        AlertDialog.Builder authen = new AlertDialog.Builder(this);

        authen.setTitle("Reauthentication");
        authen.setMessage("Please Confirm Email and Password");

        authen.setView(alertBox);

        final EditText authEmail = (EditText) alertBox.findViewById(R.id.email);
        final EditText authPassword = (EditText) alertBox.findViewById(R.id.password);

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
        mProfileReference.child("email").setValue(email);
    }


    private void updatePhoto() {
        mProfileReference.child("photo").setValue(mUserPhoto);
        saveProfileImage(mUserPhoto);
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

    private void updateLanguage(String lang) {
        mProfileReference.child("language").setValue(lang);
    }

    private void updateEthnicity(String ethn) {
        mProfileReference.child("ethnicity").setValue(ethn);
    }

    private void updateGender(String gen) {
        mProfileReference.child("gender").setValue(gen);
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

    private int getEth(String id){
        switch (id){
            case "Prefer Not to Answer":
                return 0;
            case "African American":
                return 1;
            case "American Indian":
                return 2;
            case "Asian":
                return 3;
            case "Hispanic":
                return 4;
            case "Pacific Islander":
                return 5;
            case "White":
                return 6;
        }
        return 0;
    }

    private int getGen(String id){
        switch (id){
            case "Female":
                return 0;
            case "Male":
                return 1;
            case "Prefer Not to Answer":
                return 2;
        }
        return 2;
    }

    private int getLang(String id){
        switch (id){
            case "Arabic":
                return 0 ;
            case "Chinese (Mandarin)":
                return 1;
            case "English":
                return 2;
            case "French":
                return 3;
            case "German":
                return 4;
            case "Italian":
                return 5;
            case "Portuguese":
                return 6;
            case "Russian":
                return 7;
            case "Spanish":
                return 8;
            case "Swedish":
                return 9;
            case "Vietnamese":
                return 10;
        }
        return 2;
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 500;
        int targetHeight = 500;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    private void saveProfileImage(Uri data){
        Bitmap image = convertUriToBitmap(data);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File myfile = cw.getDir("ProfilePic", Context.MODE_PRIVATE);
        File mypath = new File(myfile, "profilepic.JPG");

        FileOutputStream fo;

        //saving image into internal storage
        try {
            //myfile.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(byteArray);
            fo.flush();
            fo.close();
        } catch (Exception e) {
            Toast.makeText(ProfileActivity.this, "Error with Profile Picture", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private Bitmap convertUriToBitmap(Uri data) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void loadProfileImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("ProfilePic", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "profilepic.JPG");
        //mProfileFilePath = f.toString();
        mImageView.setImageBitmap(getRoundedShape(convertUriToBitmap(Uri.fromFile(f))));
    }


} // end class ProfileActivity
