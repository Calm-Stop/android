package com.policestrategies.calm_stop.citizen.stop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import static android.widget.Toast.LENGTH_SHORT;
import static com.policestrategies.calm_stop.R.id.question1;

public class SurveyActivity extends AppCompatActivity {

    private Activity mActivityReference;

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

    private static final String TAG = "SurveyActivity";

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

         loadOfficerReference();

        /* Get Officer info from Firebase */
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                officerName = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("last_name").getValue().toString();
                String lastName = dataSnapshot.child("last_name").getValue().toString();
                departmentNumber = dataSnapshot.child("department").getValue().toString();
                badgeNumber = dataSnapshot.child("badge").getValue().toString();
                photoURL = dataSnapshot.child("photo").getValue().toString();

                //Toast.makeText(getApplicationContext(), "officerNAme: " + officerName + " ", LENGTH_SHORT).show();

                officerInfo.setText(" Officer " + officerName + "\n#" + badgeNumber + "\nPolice Department: " + departmentNumber);
                question1.setText("Please rate your encounter with Officer " + lastName);
                //FIXME - not showing up
                officerPicture.setImageURI(Uri.parse(photoURL));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "not getting shit from firebase", databaseError.toException());
            }
        });

        setUpQuestion();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.submitButton:
                        recalculateAverage(rating);
                        startNextQuestion();
                        break;
                }
            }
        });
    }

    private void loadOfficerReference() {
        Intent currentIntent = getIntent();
        String officerURL = currentIntent.getExtras().getString("officer_firebase_reference");
        mDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(officerURL);
    }

    private void setUpQuestion(){
        radioGroup = (RadioGroup) findViewById(R.id.question1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                switch(checkedRadioButtonId){
                    case R.id.verySatButton:
                        rating = 5;
                        break;
                    case R.id.somewhatSatButton:
                        rating = 4;
                        break;
                    case R.id.satButton:
                        rating = 3;
                        break;
                    case R.id.somewhatDisButton:
                        rating = 2;
                        break;
                    case R.id.disButton:
                        rating = 1;
                        break;
                    case R.id.noOpButton:
                        rating = 0;
                        break;
                }
            }
        });
    }

    private void recalculateAverage(final int newValue) {

        if (newValue == 0) {
            mDatabaseReference.getParent().child("ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String officerCurrentOp = dataSnapshot.child("no_opinion_count").getValue().toString();
                    updateOpinionCount(officerCurrentOp);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            mDatabaseReference.getParent().child("ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String officerCurrentAverage = dataSnapshot.child("avg_rating").getValue().toString();
                    String numOfRatings = dataSnapshot.child("number_of_ratings").getValue().toString();
                    math(officerCurrentAverage, numOfRatings, newValue);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void startNextQuestion() {
        Intent i = new Intent(this, AddCommentActivity.class);
        i.putExtra("officer_firebase_reference", mDatabaseReference.toString());
        startActivity(i);
        finish();
    }

    private void math(String average, String num, int newValue) {
        double ave = Double.parseDouble(average);
        double totalRatings = Double.parseDouble(num);
        double newTotalRatings = totalRatings + 1;

        double newAverage = (ave * totalRatings + newValue) / (newTotalRatings);

        DecimalFormat df = new DecimalFormat("#.##");

        updateAverage(df.format(newAverage));
        updateRatingNumber((int) newTotalRatings);  //It will be an integer

    }

    private void updateAverage(String average){
        double ave = Double.parseDouble(average);
        mDatabaseReference.child("avg_rating").setValue(ave);
        mDatabaseReference.getParent().child("ratings").child("avg_rating").setValue(ave);
    }

    private void updateRatingNumber(int rating){
        mDatabaseReference.getParent().child("ratings").child("number_of_ratings").setValue(rating);
        mDatabaseReference.child("number_of_ratings").setValue(rating);
    }

    private void updateOpinionCount(String opinion) {
        double opinCount = Integer.parseInt(opinion);
        mDatabaseReference.getParent().child("ratings").child("no_opinion_count").setValue(opinCount + 1);
    }

}
