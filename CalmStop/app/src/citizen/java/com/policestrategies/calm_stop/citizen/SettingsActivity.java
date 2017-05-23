package com.policestrategies.calm_stop.citizen;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
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

import static android.R.attr.targetActivity;
import static android.R.attr.value;
import static com.policestrategies.calm_stop.R.id.howToUse;
import static com.policestrategies.calm_stop.R.layout.activity_alertdialog;

/**
 * Created by mariavizcaino on 2/19/17.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private TextView Title;

    private String valueEmail;
    private String valuePassword;

    /*When Changing Passwords*/
    private String valueNewPassword;
    private String valueNewPassword2;

    private static final String TAG = "SettingsActivity";

    private TextView mProfileName;
    private ImageView mProfileImage;
    private View navigView;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mProfileReference;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_settings);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/avenir-next.ttf");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Title = (TextView) findViewById(R.id.settingsTitle);
        Title.setTypeface(custom_font);

        TextView accountT = (TextView) findViewById(R.id.accountTitle);
        accountT.setTypeface(custom_font);

        TextView helpT = (TextView) findViewById(R.id.helpTitle);
        helpT.setTypeface(custom_font);


        findViewById(R.id.menu_main).setOnClickListener(this);

        Button changePass = (Button) findViewById(R.id.changePassword);
        changePass.setOnClickListener(this);
        changePass.setText("Change Password");
        changePass.setTypeface(custom_font);

        Button complaint = (Button) findViewById(R.id.complaint);
        complaint.setOnClickListener(this);
        complaint.setText("I want to file a complaint.");
        complaint.setTypeface(custom_font);

        Button howToUse = (Button) findViewById(R.id.howToUse);
        howToUse.setOnClickListener(this);
        howToUse.setText("How to Use");
        howToUse.setTypeface(custom_font);

        navigView = navigationView.getHeaderView(0);
        mProfileImage = (ImageView) navigView.findViewById(R.id.imageView);
        mProfileName = (TextView) navigView.findViewById(R.id.nameDisplay);
        mProfileName.setTypeface(custom_font);

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
        });



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.menu_main:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.complaint:
                goToComplaint();
                break;
            case R.id.changePassword:
                getNewPassword();
                break;
            case howToUse:
                howToUse();
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

    private void howToUse() {
        Intent i = new Intent(getBaseContext(), HowToUseActivity.class);
        startActivity(i);
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

    private void goToComplaint(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://www.cityofsantacruz.com/departments/police/report-a-crime/online-police-reporting-system"));
        startActivity(intent);
    }

    private void authentication() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View alertBox = inflater.inflate(R.layout.activity_alertdialog, null);

        AlertDialog.Builder authen = new AlertDialog.Builder(this);

        authen.setTitle("Reauthentication");
        authen.setMessage("Please Confirm Email and Password");

        authen.setView(alertBox);

        final EditText authEmail = (EditText) alertBox.findViewById(R.id.email);
        final EditText authPassword = (EditText) alertBox.findViewById(R.id.password);

        authen.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                valueEmail = authEmail.getText().toString();
                valuePassword = authPassword.getText().toString();

                dialog.dismiss();
            }
        });

        authen.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        authen.show();
    }

    private void getNewPassword() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View alertBox = inflater.inflate(R.layout.activity_alertdialogpassword, null);

        final AlertDialog.Builder authen = new AlertDialog.Builder(this);

        authen.setTitle("New Password");
        authen.setMessage("Please Input New Password and Confirm");

        authen.setView(alertBox);

        final EditText authNewPassword = (EditText) alertBox.findViewById(R.id.newpassword);
        final EditText authNewPassword2 = (EditText) alertBox.findViewById(R.id.newpassword2);

        authen.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        authen.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = authen.create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    valueNewPassword = authNewPassword.getText().toString();
                    valueNewPassword2 = authNewPassword2.getText().toString();

                    if (valueNewPassword.compareTo(valueNewPassword2) == 0) {
                        updatePassword();
                        dialog.dismiss();
                    } else {
                        authNewPassword2.setError("Passwords Don't Match");
                    }
                }
            });

    }

    private void updatePassword() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(valueNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User password updated.");
                    Toast.makeText(SettingsActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthRecentLoginRequiredException e) {
                        authentication();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(valueEmail, valuePassword);
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(TAG, "User re-authenticated.");
                                    }
                                });

                    } catch(FirebaseAuthException e){
                        Toast.makeText(SettingsActivity.this, "Updated UnSuccessfully1", Toast.LENGTH_SHORT).show();

                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Toast.makeText(SettingsActivity.this, "Updated UnSuccessfully", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void loadProfileImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("ProfilePic", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "profilepic.JPG");
        if(getRoundedShape(convertUriToBitmap(Uri.fromFile(f))) != null)
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
