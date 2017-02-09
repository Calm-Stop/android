package com.policestrategies.calm_stop.citizen;

import android.os.Bundle;


import com.policestrategies.calm_stop.R;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.util.Patterns;
import android.os.Bundle;

import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
    }
}
