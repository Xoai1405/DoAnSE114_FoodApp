package com.example.myfoodapp.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myfoodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    DatabaseReference reference;
    public String TAG = "HTBB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        initConfigCloudinary();
    }

    private void initConfigCloudinary() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dqehmxzq6");

            MediaManager.init(this, config);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}