package com.certified.jobfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        enableStrictMode();
        Log.d(TAG, "onCreate: Thread = " + Thread.currentThread().getId());
    }

    private void enableStrictMode() {
        if(BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}