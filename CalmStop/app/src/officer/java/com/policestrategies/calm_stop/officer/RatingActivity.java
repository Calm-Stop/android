package com.policestrategies.calm_stop.officer;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.rating;
import static android.R.attr.value;
//import static com.policestrategies.calm_stop.R.id.Name;
// import static com.policestrategies.calm_stop.R.id.ratingBar;



/**
 * Created by mariavizcaino on 2/27/17.
 */

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {


    List<String> comments_array = new ArrayList<String>();
    RatingBar officerStarRating;
    float officerStarRatingValue;

    private DatabaseReference mDatabase;

    private BottomNavigationView bottomNavigationView;

    private FirebaseUser user;

    private static final String TAG = "RatingActivity";
    private TextView ratingDigits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rating);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ratingDigits = (TextView) findViewById(R.id.star_rating_digits);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Log.d(TAG, "comments array:" + comments_array);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String userId = user.getUid();

            // Get officer rating
            mDatabase.child("officer").child(userId).child("ratings").child("avg_rating").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Float officerStarRatingValue = dataSnapshot.getValue(Float.class);
                            Log.d(TAG, "rating = " + officerStarRatingValue);
                            if (officerStarRatingValue == null) {
                                officerStarRatingValue = (float) 0;
                            }
                            officerStarRating = (RatingBar) findViewById(R.id.officerStarRatingBar);
                            officerStarRating.setRating(officerStarRatingValue);
                            String ratingDigitsString = String.valueOf(officerStarRatingValue);
                            ratingDigits.setText(ratingDigitsString);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );


            // Get officer comments
            mDatabase.child("officer").child(userId).child("comments").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // empty comments_array
                            List<String> comments_array = new ArrayList<String>();
                            // add comments from Firebase to comments_array
                            for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                                String comment = commentSnapshot.getValue(String.class);
                                comments_array.add(comment);
                            }
                            // if there are no comments, let officer know
                            if (comments_array.isEmpty()) {
                                comments_array.add("You have not received any comments yet.");
                            }
                            // comments_array used in listView
                            ArrayAdapter adapter = new ArrayAdapter<String>(getBaseContext(),
                                    R.layout.activity_drivercommentslistview, comments_array);
                            ListView listView = (ListView) findViewById(R.id.driver_comments);
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                home();
                                break;

                            case R.id.action_ratings:
                                ratings();
                                break;

                            case R.id.action_history:
                                history();
                                break;

                            case R.id.action_account:
                                account();
                                break;

                        }
                        return true;
                    }
                });


        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_drivercommentslistview, comments_array);

        ListView listView = (ListView) findViewById(R.id.driver_comments);
        listView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

        }
    }

    private void home() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
        finish();

    }

    private void ratings() {
        Intent i = new Intent(getBaseContext(), RatingActivity.class);
        startActivity(i);

    }
    private void account() {
        Intent i = new Intent(getBaseContext(), AccountActivity.class);
        startActivity(i);

    }
    private void history() {
        Intent i = new Intent(getBaseContext(), HistoryActivity.class);
        startActivity(i);

    }

}
