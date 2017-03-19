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
import com.policestrategies.calm_stop.SignupVerification;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner daySetter;
    private Spinner monthSetter;
    private Spinner yearSetter;
    private Spinner genderSetter;
    private Spinner ethnicitySetter;
    private Spinner languageSetter;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;
    private EditText mPhone;
    private TextView mDateOfBirth;

    private EditText mZip;
    private EditText mLicense;
    private EditText mDepartmentNum;
    private EditText mBadgeNum;
    private ImageView mphoto;

    private static final int chosenImage = 1;

    private int gen = 2;
    private int eth = 0;
    private int lan = 2;
    private int ethTrue = 0;
    private int i_year, i_day, i_month;
    int month;
    int day;
    int year;

    private String FName;
    private String LName;
    private String Email;
    private String PhoneNumber;

    private String License;
    private String ZIP;
    private String DOB;
    private String Day;
    private String Month;
    private String Year;
    private String Gender;
    private String Ethnicity;
    private String Language;
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

        //mFname, mLname, memail, mDOB, mphoneNum, mLicense, mZip, mLanguage, mphoto
        mFirstNameField = (EditText)findViewById(R.id.editFirstName);
        mLastNameField = (EditText)findViewById(R.id.editLastName);
        mEmailField = (EditText)findViewById(R.id.editEmail);
        mPhone = (EditText)findViewById(R.id.editPhonenum);
        mLicense= (EditText)findViewById(R.id.editLicenseNumber);
        mZip = (EditText)findViewById(R.id.editZip);
        mDepartmentNum = (EditText)findViewById(R.id.editdepartnum);
        mBadgeNum = (EditText)findViewById(R.id.editBadgenum);
        mDateOfBirth = (TextView) findViewById(R.id.dateOfBirth);

        mphoto = (ImageView)findViewById(R.id.profilePicture);
        mphoto.setOnClickListener(this);

        findViewById(R.id.backbutton).setOnClickListener(this);
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            officerUid = "";
        }
        else{
            officerUid = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("officer");
        profileRef = mDatabase.child(officerUid).child("profile");

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
                ZIP = snapshot.child("zip_code").getValue().toString();
                Email = snapshot.child("email").getValue().toString();
                FName = snapshot.child("first_name").getValue().toString();
                LName = snapshot.child("last_name").getValue().toString();
                Gender = snapshot.child("gender").getValue().toString();
                Language = snapshot.child("language").getValue().toString();
                License = snapshot.child("license_number").getValue().toString();
                PhoneNumber = snapshot.child("phone_number").getValue().toString();
                Language = snapshot.child("language").getValue().toString();
                Department = snapshot.child("department").getValue().toString();
                Badge = snapshot.child("badge").getValue().toString();
                Ethnicity = snapshot.child("ethnicity").getValue().toString();

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
                PhoneNumber = mPhone.getText().toString();
                License = mLicense.getText().toString();
                ZIP = mZip.getText().toString();
                Department = mDepartmentNum.toString();
                Badge = mBadgeNum.toString();
                Gender = genderSetter.getSelectedItem().toString();
                Ethnicity = ethnicitySetter.getSelectedItem().toString();
                Language = languageSetter.getSelectedItem().toString();


                if (!validateInput(Email, License, FName, LName, PhoneNumber, ZIP, DOB)) return;
                //WRITE TO FIREBASE
                updateFName();
                updateLName();
                updatePhoto();
                updatePhoneNumber();
                updateEmail();
                updateZip();
                updateLicense();
                updateLanguage();
                updateGender();
                updateEthnicity();
                updateDepartment();
                updateBadge();

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
                i_year = getResources().getStringArray(R.array.Year).length + 1909 - position;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
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


    private void setUpGenderSetter() {

        final ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.Gender, android.R.layout.simple_spinner_dropdown_item);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSetter.setAdapter(genderAdapter);

        genderSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0: //Female
                        gen = 0;
                        break;
                    case 1: //Male
                        gen = 1;
                        break;
                    case 2: //Prefer Not to Answer
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

        ethnicitySetter.setSelection(eth);

        ethnicitySetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:  //Prefer Not to Answer
                        eth = 0;
                        break;
                    case 1: //African american
                        eth = 1;
                        break;
                    case 2: //American indian
                        eth = 2;
                        break;
                    case 3: //asian
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

    private void setUpLanguageSetter() {

        final ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.Language, android.R.layout.simple_spinner_dropdown_item);

        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSetter.setAdapter(languageAdapter);

        languageSetter.setSelection(lan); //default to english

        languageSetter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //Arabic
                        lan = 0;
                        Language = "Arabic";
                        break;
                    case 1: //Chinese (Mandarin)
                        lan = 1;
                        Language = "Chinese (Mandarin)";
                        break;
                    case 2: //English
                        lan = 2;
                        Language = "English";
                        break;
                    case 3: //French
                        lan = 3;
                        Language = "French";
                        break;
                    case 4: //German
                        lan = 4;
                        Language = "German";
                        break;
                    case 5: // Italian
                        lan = 5;
                        Language = "Italian";
                        break;
                    case 6: //Portuguese
                        lan = 6;
                        Language = "Portuguese";
                        break;
                    case 7: //Russian
                        lan = 7;
                        Language = "Russian";
                        break;
                    case 8: //Spanish
                        lan = 8;
                        Language = "Spanish";
                        break;
                    case 9: //Swedish
                        lan = 9;
                        Language = "Swedish";
                        break;
                    case 10: //Vietnamese
                        lan = 10;
                        Language = "Vietnamese";
                        break;
                }
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
        mPhone.setText(PhoneNumber);
        mZip.setText(ZIP);
        mLicense.setText(License);
        genderSetter.setSelection(setGen());
        ethnicitySetter.setSelection(setEth());
        languageSetter.setSelection(setLan());
        monthSetter.setSelection(0);
        daySetter.setSelection(0);
        yearSetter.setSelection(0);
    }

    private int setGen(){
        if(Gender.equalsIgnoreCase("male"))
            return 1;
        else if (Gender.equalsIgnoreCase("female"))
            return 0;
        return 2; //else blank
    }

    private int setEth(){
        if (Ethnicity.equalsIgnoreCase("American indian"))
            return 2;
        else if (Ethnicity.equalsIgnoreCase("Asian"))
            return 3;
        else if (Ethnicity.equalsIgnoreCase("African american"))
            return 1;
        else if (Ethnicity.equalsIgnoreCase("Hispanic"))
            return 4;
        else if (Ethnicity.equalsIgnoreCase("pacific islander"))
            return 5;
        else if (Ethnicity.equalsIgnoreCase("white"))
            return 6;
        else
            return 0; //else blank
    }

    private int setLan(){
        if (Language.equalsIgnoreCase("Arabic"))
            return 0;
        else if (Language.equalsIgnoreCase("Chinese (Mandarin)"))
            return 1;
        else if (Language.equalsIgnoreCase("French"))
            return 3;
        else if (Language.equalsIgnoreCase("German"))
            return 4;
        else if (Language.equalsIgnoreCase("Italian"))
            return 5;
        else if (Language.equalsIgnoreCase("Portuguese"))
            return 6;
        else if (Language.equalsIgnoreCase("Russian"))
            return 7;
        else if (Language.equalsIgnoreCase("Spanish"))
            return 8;
        else if (Language.equalsIgnoreCase("Swedish"))
            return 9;
        else if (Language.equalsIgnoreCase("Vietnamese"))
            return 10;
        return 2; //else English
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
    private void updatePhoneNumber() { profileRef.child("phone_number").setValue(PhoneNumber); }
    private void updateZip() {profileRef.child("zip_code").setValue(ZIP);}
    private void updateDOB() { profileRef.child("date_of_birth").setValue(DOB); }
    private void updateLicense() { profileRef.child("license_number").setValue(License); }
    private void updateDepartment() { profileRef.child("department").setValue(Department); }
    private void updateBadge() { profileRef.child("badge").setValue(Badge); }

    private void updateGender() {
        if(gen == 2)
            profileRef.child("gender").setValue("");
        else if(gen == 0)
            profileRef.child("gender").setValue("Female");
        else
            profileRef.child("gender").setValue("Male");
    }

    private void updateEthnicity() {
        if (eth == 2)
            profileRef.child("ethnicity").setValue("American Indian");
        else if (eth == 3)
            profileRef.child("ethnicity").setValue("Asian");
        else if (eth == 1)
            profileRef.child("ethnicity").setValue("African American");
        else if (eth == 4)
            profileRef.child("ethnicity").setValue("Hispanic");
        else if (eth == 5)
            profileRef.child("ethnicity").setValue("pacific islander");
        else if (eth == 6)
            profileRef.child("ethnicity").setValue("white");
        else
            profileRef.child("ethnicity").setValue("");
    }

    private void updateLanguage() {
        if (lan == 0)
            profileRef.child("language").setValue("Arabic");
        else if (lan == 1)
            profileRef.child("language").setValue("Chinese (Mandarin)");
        else if (lan == 3)
            profileRef.child("language").setValue("French");
        else if (lan == 4)
            profileRef.child("language").setValue("German");
        else if (lan == 5)
            profileRef.child("language").setValue("Italian");
        else if (lan == 6)
            profileRef.child("language").setValue("Portuguese");
        else if (lan == 7)
            profileRef.child("language").setValue("Russian");
        else if (lan == 8)
            profileRef.child("language").setValue("Spanish");
        else if (lan == 9)
            profileRef.child("language").setValue("Swedish");
        else if (lan == 10)
            profileRef.child("language").setValue("Vietnamese");
        else
            profileRef.child("language").setValue("English");
    }


    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }


    private boolean validateInput(String email, String licensenum, String firstname,
                                  String lastname, String phone, String zip, String dateofbirth) {

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

        //DATE OF BIRTH REGEX; replace with spinner for month, day, and year.
        if (!SignupVerification.validDateOfBirth(dateofbirth)){
            mDateOfBirth.setError("DD-MM-YYYY");
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
