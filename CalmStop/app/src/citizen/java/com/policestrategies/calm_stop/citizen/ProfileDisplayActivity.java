package com.policestrategies.calm_stop.citizen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SharedUtil;

import org.w3c.dom.Text;

/**
 * Created by mariavizcaino on 4/14/17.
 */

public class ProfileDisplayActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView Name;
    private TextView Email;
    private TextView Phone;
    private TextView DOB;
    private TextView DriverLis;
    private TextView Gender;
    private TextView Lang;
    private TextView Ethn;
    private TextView Zipcode;

    private int i_gender, i_ethnicity, i_language;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mProfileReference;

    private static final String TAG = "ProfileActivity";

    private ProgressDialog mProgressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profiledisplay);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Name = (TextView) findViewById(R.id.Nametxt);
        Email = (TextView) findViewById(R.id.Emailtxt);
        Phone = (TextView) findViewById(R.id.Phonenumtxt);
        DOB = (TextView) findViewById(R.id.DOBtxt);
        DriverLis = (TextView) findViewById(R.id.DriversListxt);
        Zipcode = (TextView) findViewById(R.id.Zipcodetxt);
        Lang = (TextView) findViewById(R.id.Langtxt);
        Gender = (TextView) findViewById(R.id.Gendertxt);
        Ethn = (TextView) findViewById(R.id.Ethnicitytxt);

        findViewById(R.id.viewDocs).setOnClickListener(this);
        findViewById(R.id.EditButton).setOnClickListener(this);

        mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);

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

        Name.setTextColor(getColorStateList(R.color.black));
        Name.setTextSize(14);

        Email.setTextColor(getColorStateList(R.color.black));
        Email.setTextSize(14);

        Phone.setTextColor(getColorStateList(R.color.black));
        Phone.setTextSize(14);

        DOB.setTextColor(getColorStateList(R.color.black));
        DOB.setTextSize(14);

        DriverLis.setTextColor(getColorStateList(R.color.black));
        DriverLis.setTextSize(14);

        Zipcode.setTextColor(getColorStateList(R.color.black));
        Zipcode.setTextSize(14);

        Lang.setTextColor(getColorStateList(R.color.black));
        Lang.setTextSize(14);

        Gender.setTextColor(getColorStateList(R.color.black));
        Gender.setTextSize(14);

        Ethn.setTextColor(getColorStateList(R.color.black));
        Ethn.setTextSize(14);

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


                String name = firstName + " " + lastName;
                Zipcode.setText(zip);
                Email.setText(email);
                Name.setText(name);
                DriverLis.setText(license);
                Phone.setText(phoneNumber);
                DOB.setText(dateOfBirth);
                Ethn.setText(getEth(i_ethnicity));
                Gender.setText(getGen(i_gender));
                Lang.setText(getLang(i_language));

                SharedUtil.dismissProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }

        });

    }

    public void onBackPressed() {
        toHomepage();
    }

    private String getEth(int id){
        switch (id){
            case 0:
                return "Prefer Not to Answer";
            case 1:
                return "African American";
            case 2:
                return "American Indian";
            case 3:
                return "Asian";
            case 4:
                return "Hispanic";
            case 5:
                return "Pacific Islander";
            case 6:
                return "White";
        }
        return "Prefer Not to Answer";
    }

    private String getGen(int id){
        switch (id){
            case 0:
                return "Female";
            case 1:
                return "Male";
            case 2:
                return "Prefer Not to Answer";
        }
        return "Prefer Not to Answer";
    }

    private String getLang(int id){
        switch (id){
            case 0:
                return "Arabic";
            case 1:
                return "Chinese (Mandarin)";
            case 2:
                return "English";
            case 3:
                return "French";
            case 4:
                return "German";
            case 5:
                return "Italian";
            case 6:
                return "Portuguese";
            case 7:
                return "Russian";
            case 8:
                return "Spanish";
            case 9:
                return "Swedish";
            case 10:
                return "Vietnamese";
        }
        return "English";
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewDocs:
                toDocuments();
                break;
            case R.id.EditButton:
                toProfileEdit();
                break;
        }
    }

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

    private void toProfileEdit() {
        Intent i = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(i);
    }

    private void toDocuments() {
        Intent i = new Intent(getBaseContext(), DocumentsActivity.class);
        startActivity(i);
    }

}
