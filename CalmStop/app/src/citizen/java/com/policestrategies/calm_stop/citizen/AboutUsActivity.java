package com.policestrategies.calm_stop.citizen;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.citizen.beacon_detection.BeaconDetectionActivity;

import java.io.File;
import java.io.IOException;

import static android.R.attr.data;

/**
 * @author mariavizcaino
 */

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private TextView Title;

    private TextView mProfileName;
    private ImageView mProfileImage;
    private View navigView;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mProfileReference;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/avenir-next.ttf");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Title = (TextView) findViewById(R.id.AboutUsTitle);
        Title.setTypeface(custom_font);

        navigView = navigationView.getHeaderView(0);
        mProfileImage = (ImageView) navigView.findViewById(R.id.imageView);
        mProfileName = (TextView) navigView.findViewById(R.id.nameDisplay);
        mProfileName.setTypeface(custom_font);

        TextView content = (TextView) findViewById(R.id.AboutUsContent);
        content.setTypeface(custom_font);
        content.setText("Police Strategies is a data and technology company that is focused" +
                        " on using data science to drive policing reform and build community trust." +
                        " The company offers the Police Force Analysis System℠ to help law enforcement " +
                        "agencies identify risk and improve their policies and training. The Verbal VR™ " +
                        "De-Escalator provides a realistic simulated environment where officers can " +
                        "practice their verbal de-escalation skills. The Stopalog™ mobile application " +
                        "improves communications between officers and the drivers that they stop.\n" +
                         " \n" +
                        "Police Strategies was formed by attorneys, law enforcement professionals and " +
                        "data scientists who all have had decades of experience working in the public " +
                        "safety arena. You can find more information about our company at www.policestrategies.com.");


        findViewById(R.id.menu_main).setOnClickListener(this);

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

        mProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String firstName = snapshot.child("first_name").getValue().toString();
                String lastName = snapshot.child("last_name").getValue().toString();

                String name = firstName + " " + lastName;
                mProfileName.setText(name);
                loadProfileImage();
                //SharedUtil.dismissProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }

        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id) {
                    case R.id.profile:
                        profile();
                        break;
                    case R.id.previous_stops:
                        previousStops();
                        break;
                    case R.id.about_us:
                        aboutUs();
                        break;
                    case R.id.settings:
                        settings();
                        break;
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.detect_beacon_debug:
                        detectBecon();
                        break;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.menu_main:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void profile() {
        Intent i = new Intent(getBaseContext(), ProfileDisplayActivity.class);
        startActivity(i);
        finish();
    }

    private void previousStops() {
        Intent i = new Intent(getBaseContext(), PreviousStopsActivity.class);
        startActivity(i);
        finish();
    }

    private void aboutUs() {
        Intent i = new Intent(getBaseContext(), AboutUsActivity.class);
        startActivity(i);
        finish();
    }

    private void settings() {
        Intent i = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(i);
        finish();
    }

    private void logout() {
        //You want to logout -> login page
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void detectBecon(){
        Intent i = new Intent(this, BeaconDetectionActivity.class);
        startActivity(i);
        finish();
    }

    private void loadProfileImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("ProfilePic", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "profilepic.JPG");
        //mProfileFilePath = f.toString();
        mProfileImage.setImageBitmap(getRoundedShape(convertUriToBitmap(Uri.fromFile(f))));
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

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 500;
        int targetHeight = 500;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

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
}