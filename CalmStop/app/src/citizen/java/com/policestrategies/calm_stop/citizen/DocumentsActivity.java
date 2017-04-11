package com.policestrategies.calm_stop.citizen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.policestrategies.calm_stop.R;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_36dp);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Documents");
        setContentView(R.layout.activity_documents);

        mlicense = (ImageView) findViewById(R.id.license);
        mregistration = (ImageView) findViewById(R.id.registration);
        minsurance = (ImageView) findViewById(R.id.insurance);

       // findViewById(R.id.backbutton).setOnClickListener(this);

        mlicense.setOnClickListener(this);
        mregistration.setOnClickListener(this);
        minsurance.setOnClickListener(this);

        SharedPreferences docs = getSharedPreferences(DOCS, 0);

        //READ FROM
        String licen = docs.getString(LICEN, null);
        String regi = docs.getString(REGI, null);
        String insur = docs.getString(INSUR, null);

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

        Toast.makeText(DocumentsActivity.this, "Click on Image To Upload", Toast.LENGTH_SHORT).show();
    }

    public void onClick(View v) {

        switch(v.getId()) {

//            case R.id.backbutton: // The login button was pressed - let's run the login function
//                toProfile();
//                break;
            case R.id.license:
                PhotoUpdating = 0;
                Intent lisence = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                lisence.setType("image/");
                startActivityForResult(lisence, chosenImage);
                break;
            case R.id.registration:
                PhotoUpdating = 1;
                Intent reg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                reg.setType("image/");
                startActivityForResult(reg, chosenImage);
                break;
            case R.id.insurance:
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
                    String license = licensePic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(LICEN, license);
                    editor.commit();
                } else
                    Toast.makeText(DocumentsActivity.this, "Error with License", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if (requestCode == chosenImage && resultCode == RESULT_OK && data != null) {
                    registrationPic = data.getData();
                    mregistration.setImageURI(registrationPic);
                    String regi = registrationPic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(REGI, regi);
                    editor.commit();
                } else
                    Toast.makeText(DocumentsActivity.this, "Error with Registration", Toast.LENGTH_LONG).show();
                break;
            case 2:
                if (requestCode == chosenImage && resultCode == RESULT_OK && data != null) {
                    insurancePic = data.getData();
                    minsurance.setImageURI(insurancePic);
                    String insur = insurancePic.toString();
                    SharedPreferences docs = getSharedPreferences(DOCS, 0);
                    SharedPreferences.Editor editor = docs.edit();
                    editor.putString(INSUR, insur);
                    editor.commit();
                } else
                    Toast.makeText(DocumentsActivity.this, "Error with Insurance", Toast.LENGTH_LONG).show();
                break;
        }

    }

    private void toProfile() {
        Intent i = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(i);
    }
}
