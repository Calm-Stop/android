package com.policestrategies.calm_stop.citizen;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

        SharedPreferences docs = getSharedPreferences(DOCS, 0);


        String licen = docs.getString(LICEN, null);
        String regi = docs.getString(REGI, null);
        String insur = docs.getString(INSUR, null);
        /*
        loadImageFromStorage(path0, 0); //For License
        loadImageFromStorage(path1, 1); //For Registation
        loadImageFromStorage(path2, 2); //For Insurance
*/

        if(licen != null) {
            licensePic = Uri.parse(licen);
            mlicense.setImageURI(licensePic);
        }

        if(regi != null) {
            registrationPic = Uri.parse(regi);
            mregistration.setImageURI(registrationPic);
        }

        if(insur != null) {
            insurancePic = Uri.parse(insur);
            minsurance.setImageURI(insurancePic);
        }

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

                    String license = licensePic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(LICEN, license);
                    editor.commit();

                    path0 = saveToInternalStorage(licBitmap);
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

                    String regi = registrationPic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(REGI, regi);
                    editor.commit();

                    path1 = saveToInternalStorage(regBitmap);
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

                    String insur = insurancePic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(INSUR, insur);
                    editor.commit();

                    path2 = saveToInternalStorage(insBitmap);
                } else
                    Toast.makeText(DocumentsActivity.this, "Error with Insurance Image", Toast.LENGTH_LONG).show();
                break;
        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = null;
        switch(PhotoUpdating) {
            case 0:
                mypath = new File(directory, "license.jpg");
                break;
            case 1:
                mypath = new File(directory, "registration.jpg");
                break;
            case 2:
                mypath = new File(directory, "insurance.jpg");
                break;
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Toast.makeText(DocumentsActivity.this, "Error with License bitImage", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path, int image) {
        FileInputStream inputStream = null;
        switch(image) {
            case 0:
                try {
                    //File f = new File(path, "license.jpg");
                    inputStream = new FileInputStream(path);
                    Bitmap b = BitmapFactory.decodeStream(inputStream);
                    mlicense.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    Toast.makeText(DocumentsActivity.this, "Error with License Image", Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    File f = new File(path, "registration.jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    mregistration.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    Toast.makeText(DocumentsActivity.this, "Error with Registration Image", Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    File f = new File(path, "insurance.jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    minsurance.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    Toast.makeText(DocumentsActivity.this, "Error with Insurance Image", Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }
                break;
        }

    }

}
