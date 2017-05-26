package com.policestrategies.calm_stop.citizen.stop;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
import com.policestrategies.calm_stop.SharedUtil;

import java.text.DecimalFormat;

import static com.policestrategies.calm_stop.R.id.question1;

public class SurveyActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private ImageView officerPicture;
    private TextView officerInfo;
    private Button submit;

    private String officerName;
    private String departmentNumber;
    private String badgeNumber;
    private String photoURL;

    private int rating = 0;

    private DatabaseReference mDatabaseReference;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        final Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/avenir-next.ttf");
        final TextView question1 = (TextView) findViewById(R.id.question);
        TextView title = (TextView) findViewById(R.id.feedbacktitle);

        officerPicture = (ImageView) findViewById(R.id.profilePictureOfficer);
        officerInfo = (TextView) findViewById(R.id.officerInfo);
        submit = (Button) findViewById(R.id.submitButton);

        officerInfo.setTypeface(custom_font);
        question1.setTypeface(custom_font);
        title.setTypeface(custom_font);

        /* USE THIS ONCE PROPERLY IMPLEMENTED:
         mDatabaseReference = StopManager.getOfficerReference();
         */

        //hardcoded
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("officer")
                .child("14567").child("Tl4pCcIjlxTXQgCcoLp4IB4Hzti2").child("profile");

        /* Get Officer info from Firebase */
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                officerName = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("last_name").getValue().toString();
                String lastName = dataSnapshot.child("last_name").getValue().toString();
                departmentNumber = dataSnapshot.child("department").getValue().toString();
                badgeNumber = dataSnapshot.child("badge").getValue().toString();
                photoURL = dataSnapshot.child("photo").getValue().toString();

                officerInfo.setText(" Officer " + officerName + "\n#" + badgeNumber + "\nPolice Department: " + departmentNumber);
                question1.setText("Please rate your encounter with Officer " + lastName);
                //FIXME - not showing up
                officerPicture.setImageURI(Uri.parse(photoURL));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.question1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                switch(checkedRadioButtonId){
                    case R.id.verySatButton:
                        rating = 5;
                        //Toast.makeText(SurveyActivity.this, "Button1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.somewhatSatButton:
                        rating = 4;
                        //Toast.makeText(SurveyActivity.this, "Button2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.satButton:
                        rating = 3;
                        //Toast.makeText(SurveyActivity.this, "Button3", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.somewhatDisButton:
                        rating = 2;
                        //Toast.makeText(SurveyActivity.this, "Button4", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.disButton:
                        rating = 1;
                        //Toast.makeText(SurveyActivity.this, "Button5", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.noOpButton:
                        rating = 0;
                        Toast.makeText(SurveyActivity.this, "Button6", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.submitButton:
                        recalculateAverage(rating);
                        Toast.makeText(SurveyActivity.this, "Button2", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private String officerCurrentAverage;
    private String numOfRatings;
    private String officerCurrentOp;

    private void recalculateAverage(int newValue) {

        if (newValue == 0) {
            mDatabaseReference.getParent().child("ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    officerCurrentOp = dataSnapshot.child("no_opinion_count").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            int opinionCount = Integer.parseInt(officerCurrentOp);


            mDatabaseReference.getParent().child("ratings").child("no_opinion_count").setValue(opinionCount + 1);
        } else {
            //mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);
            //Toast.makeText(SurveyActivity.this, "button: " + mDatabaseReference.getParent().child("ratings").toString(), Toast.LENGTH_SHORT).show();

            mDatabaseReference.getParent().child("ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    officerCurrentAverage = dataSnapshot.child("avg_rating").getValue().toString();
                    numOfRatings = dataSnapshot.child("number_of_ratings").getValue().toString();
                    //SharedUtil.dismissProgressDialog(mProgressDialog);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //NOW FOR MATH
            Toast.makeText(SurveyActivity.this, "OfficerAVG: " + officerCurrentAverage + " RATINGS: " + numOfRatings, Toast.LENGTH_SHORT).show();
/*
            double average = Double.parseDouble(officerCurrentAverage);
            double totalRatings = Double.parseDouble(numOfRatings);
            double newTotalRatings = totalRatings + 1;

            double newAverage = (average * totalRatings + newValue) / (totalRatings + 1);

            DecimalFormat df = new DecimalFormat("#.##");
            //Toast.makeText(SurveyActivity.this, "New Average: " + df.format(newAverage), Toast.LENGTH_SHORT).show();

            mDatabaseReference.child("number_of_ratings").setValue(newTotalRatings, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(SurveyActivity.this, "Data could not be saved1 ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SurveyActivity.this, "Data saved successfully1.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mDatabaseReference.child("avg_rating").setValue(df.format(newAverage), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be saved " + databaseError.getMessage());
                    } else {
                        System.out.println("Data saved successfully.");
                    }
                }
            });
*/

        }
    }

}
