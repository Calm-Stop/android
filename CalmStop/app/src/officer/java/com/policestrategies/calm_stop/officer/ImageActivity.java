package com.policestrategies.calm_stop.officer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.policestrategies.calm_stop.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//PLAN: This is a single image module that should be passed an image reference when called.
//This class is a module dedicated to viewing and manipulating the image (e.g. zoom, pan, etc.)
//This class does not know where images come from, only that it receives images (URL, Uri, etc.)
//TODO: set up three specific local file path to which documents are downloaded and pulled
public class ImageActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private PhotoView mPhotoView;

    private String mStopID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image);
        mPhotoView = (PhotoView) findViewById(R.id.photo_view);
//The image from firebase is the test image to be manipulated in the view:
        mStopID = "temp_stop_ID";
        mStorageRef = FirebaseStorage.getInstance().getReference();
        SetImage(mPhotoView, "license");

    }

//SetImage(PhotoView photoView, String docType)
//retrieves image from Firebase Storage under stopID->${docType}
    public void SetImage(PhotoView photoView, String docType) {
        StorageReference imageRef = mStorageRef.child(mStopID).child(docType);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(imageRef)
                .into(photoView);
    }

}
