package com.policestrategies.calm_stop.officer;

import android.media.Rating;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;

import static android.R.attr.data;
import static android.R.attr.rating;
import static android.R.attr.value;
import static com.policestrategies.calm_stop.R.id.Name;
// import static com.policestrategies.calm_stop.R.id.ratingBar;



/**
 * Created by mariavizcaino on 2/27/17.
 */

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {


    String[] comments_array = {"\"Officer was very friendly.\"","\"I felt unfairly treated by the officer.\"","\"Officer gave me unnecessarily long lecture on why tail lights are important.\"","\"The traffic stop was professionally conducted.\"","\"I don't think I should have been pulled over for going 7 over the speed limit.\""};

    RatingBar officerStarRating;

    float officerStarRatingValue;

    private DatabaseReference mDatabase;


    private FirebaseUser user;

    private static final String TAG = "RatingActivity";
    private TextView ratingDigits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rating);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ratingDigits = (TextView)findViewById(R.id.star_rating_digits);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("officer").child("14566").child(userId).child("ratings").child("avg_rating").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Float officerStarRatingValue = dataSnapshot.getValue(Float.class);
                            Log.d(TAG, "rating = "+officerStarRatingValue);
                            officerStarRating=(RatingBar)findViewById(R.id.officerStarRatingBar);
                            officerStarRating.setRating(officerStarRatingValue);
                            String ratingDigitsString = String.valueOf(officerStarRatingValue);
                            ratingDigits.setText(ratingDigitsString);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
            // Read from the database
            /* FirebaseDatabase database = FirebaseDatabase.getInstance();
            String findOfficerRating = "officer/14566/"+userId+"/ratings/avg_rating";
            Log.d(TAG, "path: "+findOfficerRating);
            DatabaseReference myRef = database.getReference("officer/14566/KeqDG6SFGFNlKpoYJuZb4xRqo682/ratings/avg_rating");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    officerStarRatingValue = dataSnapshot.getValue(Float.class);
                    Log.d(TAG, "Rating is: " + officerStarRatingValue);
                    officerStarRating=(RatingBar)findViewById(R.id.officerStarRatingBar);
                    officerStarRating.setRating(officerStarRatingValue);
                    String ratingDigitsString = String.valueOf(officerStarRatingValue);
                    ratingDigits.setText(ratingDigitsString);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            }); */


        }



        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_drivercommentslistview, comments_array);

        ListView listView = (ListView) findViewById(R.id.driver_comments);
        listView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
/*
            case R.id.button_logout: // The login button was pressed - let's run the login function
                logout();
                break;
*/
        }
    }


}
