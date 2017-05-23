package com.policestrategies.calm_stop.citizen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.SharedUtil;
import com.policestrategies.calm_stop.chat.ChatActivity;
import com.policestrategies.calm_stop.citizen.beacon_detection.BeaconDetectionActivity;
import com.policestrategies.calm_stop.citizen.stop.StopActivity;

import java.io.File;
import java.io.IOException;

public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private TextView mProfileName;
    private ImageView mDrawerImage;
    private TextView Title;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mProfileReference;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        ActionBar actionBar = getSupportActionBar();;
        actionBar.hide();

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/avenir-next.ttf");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Title = (TextView) findViewById(R.id.WelcomeTitle);

        LayoutInflater inflater = LayoutInflater.from(this);
        View drawermenu = inflater.inflate(R.layout.nav_header_main, null);

        mProfileName = (TextView) drawermenu.findViewById(R.id.nameDisplay);
        mDrawerImage = (ImageView) drawermenu.findViewById(R.id.imageView);

        Title.setTypeface(custom_font);
        mProfileName.setTypeface(custom_font);

        mProfileName.setText("WHYYYY");

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

        //mProgressDialog = ProgressDialog.show(this, "", "Loading", true, false);
        mProfileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String firstName = snapshot.child("first_name").getValue().toString();
                String lastName = snapshot.child("last_name").getValue().toString();

                String name = firstName + " " + lastName;
                //FIXME
                mProfileName.setText(name);
                loadProfileImage();
                //SharedUtil.dismissProgressDialog(mProgressDialog);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }

        });

        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.menu_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.menu_main:
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                        break;

                }

            }

        });

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()) {

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
                detectBeacon();
                break;

            case R.id.chat_activity_debug:
                debugChat();
                break;

            case R.id.stop_activity_debug:
                debugStop();
                break;

        }

        return true;
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

    private void detectBeacon(){
        Intent i = new Intent(this, BeaconDetectionActivity.class);
        startActivity(i);
        finish();
    }

    private void debugChat() {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("thread_id", "01");
        startActivity(i);
        finish();
    }

    private void debugStop() {
        Intent i = new Intent(this, StopActivity.class);
        i.putExtra("officer_department", "14567");
        i.putExtra("officer_id", "Tl4pCcIjlxTXQgCcoLp4IB4Hzti2");
        i.putExtra("stop_id", "-Kj9XharhDa88IxXePwx");
        startActivity(i);
    }

    private void loadProfileImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("ProfilePic", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "profilepic.JPG");
        mDrawerImage.setImageBitmap(getRoundedShape(convertUriToBitmap(Uri.fromFile(f))));
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

}
