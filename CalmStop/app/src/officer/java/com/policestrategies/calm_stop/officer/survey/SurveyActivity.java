package com.policestrategies.calm_stop.officer.survey;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.officer.dashboard.DashboardActivity;

import java.util.Calendar;

public class SurveyActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private ImageView driverPicture;
    private TextView driverInfo;
    private TextView thanks;
    private Button submit;
    private Button home;


    private DatabaseReference mDatabaseReference;

    private static final String TAG = "SurveyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/avenir-next.ttf");
        final TextView question1 = (TextView) findViewById(R.id.question);
        TextView title = (TextView) findViewById(R.id.feedbacktitle);

        radioGroup = (RadioGroup) findViewById(R.id.question1);

        driverPicture = (ImageView) findViewById(R.id.profilePictureDriver);
        driverInfo = (TextView) findViewById(R.id.driverInfo);
        thanks = (TextView) findViewById(R.id.thankYouTitle);
        submit = (Button) findViewById(R.id.submitButton);
        home = (Button) findViewById(R.id.homeButton);
        home.setVisibility(View.INVISIBLE);
        thanks.setVisibility(View.INVISIBLE);

        driverInfo.setTypeface(custom_font);
        thanks.setTypeface(custom_font);
        question1.setTypeface(custom_font);
        title.setTypeface(custom_font);

        submit.setText("Submit Report");
        submit.setTypeface(custom_font);
        home.setText("Home");
        home.setTypeface(custom_font);

        loadDriverReference();

       // mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("citizen")
                //.child("gQfLhUrT7ecUrnI4rLbX6EsUn033").child("profile").getRef();

         //Get Driver info from Firebase
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String driverName = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("last_name").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String DOB = dataSnapshot.child("dob").getValue().toString();
                String zipcode = dataSnapshot.child("zip_code").getValue().toString();
                int age = getAge(DOB);
                driverInfo.setText(driverName + "\n" + gender + ", " + age + "\n " + zipcode);

                /*photoURL = dataSnapshot.child("photo").getValue().toString();
                //FIXME - not showing up
                //driverPicture.setImageURI(Uri.parse(photoURL));
                */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "not getting shit from firebase", databaseError.toException());
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.submitButton:
                        submitAnswer();
                        radioGroup.setVisibility(View.INVISIBLE);
                        submit.setVisibility(View.INVISIBLE);
                        home.setVisibility(View.VISIBLE);
                        thanks.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.homeButton:
                        toDashboard();
                        break;
                }
            }
        });
    }

    private void loadDriverReference() {
        Intent currentIntent = getIntent();
        String driverURL = currentIntent.getExtras().getString("driver_firebase_reference");
        mDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(driverURL);
    }

    private void submitAnswer() {
        mDatabaseReference.getParent().child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int alerts =  Integer.parseInt(dataSnapshot.child("alerts").getValue().toString());
                int arrests =  Integer.parseInt(dataSnapshot.child("arrests").getValue().toString());
                int citations =  Integer.parseInt(dataSnapshot.child("citations").getValue().toString());
                int intoxicate =  Integer.parseInt(dataSnapshot.child("intoxicated").getValue().toString());
                int stops =  Integer.parseInt(dataSnapshot.child("stops").getValue().toString());
                int threats =  Integer.parseInt(dataSnapshot.child("threats").getValue().toString());
                int warnings =  Integer.parseInt(dataSnapshot.child("warnings").getValue().toString());
                int weapons =  Integer.parseInt(dataSnapshot.child("weapons").getValue().toString());

                updateStops(stops);
                CheckBox warn = (CheckBox) findViewById(R.id.warningButton);
                if(warn.isChecked()){
                    updateWarnings(warnings);
                }
                CheckBox citat = (CheckBox) findViewById(R.id.citationButton);
                if(citat.isChecked()){
                    updateCitations(citations);
                }
                CheckBox arr = (CheckBox) findViewById(R.id.arrestedButton);
                if(arr.isChecked()){
                    updateArrests(arrests);
                }
                CheckBox alert = (CheckBox) findViewById(R.id.markAlertButton);
                if(alert.isChecked()){
                    updateAlerts(alerts);
                }
                CheckBox threat = (CheckBox) findViewById(R.id.threatenButton);
                if(threat.isChecked()){
                    updateThreats(threats);
                }
                CheckBox intox = (CheckBox) findViewById(R.id.intoxicatedButton);
                if(intox.isChecked()){
                    updateIntoxicate(intoxicate);
                }
                CheckBox weap = (CheckBox) findViewById(R.id.weaponButton);
                if(weap.isChecked()){
                    updateWeapons(weapons);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "not getting shit from firebase", databaseError.toException());
            }
        });
    }

    private int getAge(String dob){

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String delims ="/";
        String[] tokens = dob.split(delims);

        int birthYear = Integer.parseInt(tokens[2]);
        int birthMonth = Integer.parseInt(tokens[0]);
        int birthDay = Integer.parseInt(tokens[1]);

        int age = -1;

        if(month + 1 == birthMonth) {
            if (day >= birthDay)
                age++;           //its my birthday or later in the month
        }
        else if(month + 1 > birthMonth)
            age++;

        return year - (1900 + birthYear) + age ;
    }

    private void toDashboard() {
        Intent i = new Intent(getBaseContext(), DashboardActivity.class);
        startActivity(i);
        finish();
    }

    private void updateAlerts(int num) {
        mDatabaseReference.getParent().child("info").child("alerts").setValue(num + 1);
    }
    private void updateArrests(int num) {
        mDatabaseReference.getParent().child("info").child("arrests").setValue(num + 1);
    }
    private void updateThreats(int num) {
        mDatabaseReference.getParent().child("info").child("threats").setValue(num + 1);
    }
    private void updateWeapons(int num) {
        mDatabaseReference.getParent().child("info").child("weapons").setValue(num + 1);
    }
    private void updateWarnings(int num) {
        mDatabaseReference.getParent().child("info").child("warnings").setValue(num + 1);
    }
    private void updateStops(int num) {
        mDatabaseReference.getParent().child("info").child("stops").setValue(num + 1);
    }
    private void updateIntoxicate(int num) {
        mDatabaseReference.getParent().child("info").child("intoxicated").setValue(num + 1);
    }
    private void updateCitations(int num) {
        mDatabaseReference.getParent().child("info").child("citations").setValue(num + 1);
    }

}
