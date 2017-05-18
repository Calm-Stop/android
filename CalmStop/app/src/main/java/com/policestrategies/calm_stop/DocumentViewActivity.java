package com.policestrategies.calm_stop;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

/**
 * Allows the user to view an image in a custom imageview.
 * @author Talal Abou Haiba
 */
public class DocumentViewActivity extends AppCompatActivity {

    private PhotoView mPhotoView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_document_view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mPhotoView = (PhotoView) findViewById(R.id.document_view);
        loadDocument();
    }

    private void loadDocument() {
        String filePath = getIntent().getStringExtra("document_path");
        if (filePath != null && !filePath.isEmpty()) {
            displayDocument(filePath);
        }
    }

    private void displayDocument(String path) {
        File f = new File(path);
        mPhotoView.setImageURI(Uri.fromFile(f));
    }

} // end class DocumentViewActivity
