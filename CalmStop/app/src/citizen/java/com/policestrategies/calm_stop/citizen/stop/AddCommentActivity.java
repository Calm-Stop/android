package com.policestrategies.calm_stop.citizen.stop;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.citizen.HomepageActivity;

import org.w3c.dom.Text;

import static com.policestrategies.calm_stop.R.id.submitComment;
import static junit.runner.Version.id;

public class AddCommentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText comments;
    private TextView response;
    private Button submit;
    private Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcomment);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/avenir-next.ttf");

        TextView title = (TextView) findViewById(R.id.thankyoutitle);

        title.setTypeface(custom_font);
        title.setText("Thank you for your feedback!");
        title.setTextSize(25);

        response = (TextView) findViewById(R.id.commentResponse);
        response.setTypeface(custom_font);
        response.setVisibility(View.INVISIBLE);

        comments = (EditText) findViewById(R.id.comments);
        comments.setTypeface(custom_font);
        comments.setHint("Optional: Please type any additional comments on " +
                "your experience with the officer here. Your comments will " +
                "be anonymous and will be made available for the officer and " +
                "police department to view.\n");
        comments.setHintTextColor(getResources().getColor(R.color.black));

        home = (Button) findViewById(R.id.home);
        home.setTypeface(custom_font);
        home.setOnClickListener(this);

        submit = (Button) findViewById(R.id.submitComment);
        submit.setTypeface(custom_font);
        submit.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.home:
                toHomepage();
                break;
            case R.id.submitComment:
                submitComment();
                break;
        }
    }

    private void toHomepage(){
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
        finish();
    }


    private void submitComment() {
        //save comment
        Intent currentIntent = getIntent();
        String officerRef = currentIntent.getExtras().getString("officer_firebase_reference");
        DatabaseReference mOfficerReference = FirebaseDatabase.getInstance().getReferenceFromUrl(officerRef);
        mOfficerReference.getParent().child("comments").push().child("text").setValue(comments.getText().toString());
        comments.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.INVISIBLE);

        response.setVisibility(View.VISIBLE);
    }

}
