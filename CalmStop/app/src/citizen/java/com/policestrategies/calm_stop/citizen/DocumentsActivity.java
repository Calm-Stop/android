package com.policestrategies.calm_stop.citizen;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.R.attr.data;
import static android.R.attr.path;
import static com.policestrategies.calm_stop.R.id.imageView;
import static com.policestrategies.calm_stop.R.id.license;
import static com.policestrategies.calm_stop.R.id.registration;
import static java.security.AccessController.getContext;

/**
 * Created by mariavizcaino on 2/26/17.
 */

public class DocumentsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int chosenImage = 1;
    private static final String DOCS = "Documents";
    private static final String LICEN = "license";
    private static final String REGI = "registration";
    private static final String INSUR = "insurance";

    private int PhotoUpdating;  //0 -> License; 1 -> registration; 2 -> insurance

    private ImageView mlicense;
    private ImageView mregistration;
    private ImageView minsurance;
    private Uri licensePic;
    private Uri registrationPic;
    private Uri insurancePic;

    private String path0, path1, path2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_documents);

        mlicense = (ImageView) findViewById(R.id.license);
        mregistration = (ImageView) findViewById(R.id.registration);
        minsurance = (ImageView) findViewById(R.id.insurance);

        findViewById(R.id.viewLicense).setOnClickListener(this);
        findViewById(R.id.viewInsurance).setOnClickListener(this);
        findViewById(R.id.viewRegistration).setOnClickListener(this);

        findViewById(R.id.uploadInsurance).setOnClickListener(this);
        findViewById(R.id.uploadLicense).setOnClickListener(this);
        findViewById(R.id.uploadRegistration).setOnClickListener(this);

        loadLicImage();
        loadRegImage();
        loadInsImage();

    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.uploadLicense:
                PhotoUpdating = 0;
                Intent lisence = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                lisence.setType("image/");
                startActivityForResult(lisence, chosenImage);
                break;
            case R.id.uploadRegistration:
                PhotoUpdating = 1;
                Intent reg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                reg.setType("image/");
                startActivityForResult(reg, chosenImage);
                break;
            case R.id.uploadInsurance:
                PhotoUpdating = 2;
                Intent insur = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                insur.setType("image/");
                startActivityForResult(insur, chosenImage);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(PhotoUpdating) {
            case 0:
                if (requestCode == chosenImage && resultCode == RESULT_OK && data != null) {
                    licensePic = data.getData();
                    mlicense.setImageURI(licensePic);
                    Bitmap licBitmap = null;
                    try {
                        licBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), licensePic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    saveLicImage(licBitmap);

                } else
                    Toast.makeText(DocumentsActivity.this, "Error with License Image", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if (requestCode == chosenImage && resultCode == RESULT_OK && data != null) {
                    registrationPic = data.getData();
                    mregistration.setImageURI(registrationPic);
                    Bitmap regBitmap = null;
                    try {
                        regBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), licensePic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    saveRegImage(regBitmap);

                } else
                    Toast.makeText(DocumentsActivity.this, "Error with Registration Image", Toast.LENGTH_LONG).show();
                break;
            case 2:
                if (requestCode == chosenImage && resultCode == RESULT_OK && data != null) {
                    insurancePic = data.getData();
                    minsurance.setImageURI(insurancePic);
                    Bitmap insBitmap = null;
                    try {
                        insBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), licensePic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    saveInsImage(insBitmap);

                } else
                    Toast.makeText(DocumentsActivity.this, "Error with Insurance Image", Toast.LENGTH_LONG).show();
                break;
        }

    }

    //LOAD LICENSE IMAGE
    private void loadLicImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("License", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "license.JPG");

        try {

            Bitmap bg = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) findViewById(R.id.license);
            img.setImageBitmap(bg);

        } catch(IOException e) {
            e.printStackTrace();
        }


    }

    //LOAD REGISTRATION IMAGE
    private void loadRegImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_Registration/registration.JPG
        File directory = cw.getDir("Registration", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "registration.JPG");

        try {

            Bitmap bg = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) findViewById(R.id.registration);
            img.setImageBitmap(bg);

        } catch(IOException e) {
            e.printStackTrace();
        }


    }

    //LOAD INSURANCE IMAGE
    private void loadInsImage() {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("Insurance", Context.MODE_PRIVATE);

        String path = directory.getAbsolutePath();
        File f = new File(path, "insurance.JPG");

        try {

            Bitmap bg = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) findViewById(R.id.insurance);
            img.setImageBitmap(bg);

        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isFilePresent(String fileName) {
        //ContextWrapper cw = new ContextWrapper(getApplicationContext());
        String path = fileName;
        File file = new File(path);
        Toast.makeText(DocumentsActivity.this, "PATH: " + path, Toast.LENGTH_SHORT).show();
        return file.exists();
    }

    private void saveLicImage(Bitmap image){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File myfile = cw.getDir("License", Context.MODE_PRIVATE);
        File mypath = new File(myfile, "license.JPG");

        FileOutputStream fo;

        //saving image into internal storage
        try {
            //myfile.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(byteArray);
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(DocumentsActivity.this, "Error with License Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(DocumentsActivity.this, "Error with License Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private void saveRegImage(Bitmap image){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File myfile = cw.getDir("Registration", Context.MODE_PRIVATE);
        File mypath = new File(myfile, "registration.JPG");

        FileOutputStream fo;

        //saving image into internal storage
        try {
            //myfile.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(byteArray);
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(DocumentsActivity.this, "Error with Registration Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(DocumentsActivity.this, "Error with Registration Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void saveInsImage(Bitmap image){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File myfile = cw.getDir("Insurance", Context.MODE_PRIVATE);
        File mypath = new File(myfile, "insurance.JPG");

        FileOutputStream fo;

        //saving image into internal storage
        try {
            //myfile.createNewFile();
            fo = new FileOutputStream(mypath);
            fo.write(byteArray);
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(DocumentsActivity.this, "Error with Insurance Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(DocumentsActivity.this, "Error with Insurance Image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

/*

    public boolean isFilePresent(String fileName) {
        //ContextWrapper cw = new ContextWrapper(getApplicationContext());
        String path = fileName;
        File file = new File(path);
        Toast.makeText(DocumentsActivity.this, "PATH: " + path, Toast.LENGTH_SHORT).show();
        return file.exists();
    }
*/

    public void onBackPressed() {
        toHomepage();
    }

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }

}
