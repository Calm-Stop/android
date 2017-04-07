package com.policestrategies.calm_stop.citizen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.policestrategies.calm_stop.R;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Button send;
    private EditText content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.backbutton:
                toHomepage();
                break;

        }

    }

    private void toHomepage() {
        Intent i = new Intent(getBaseContext(), HomepageActivity.class);
        startActivity(i);
    }
}