package com.certified.jobfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.certified.jobfinder.R;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChooseActivity";

    //    firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mAuth = FirebaseAuth.getInstance();
        setupFirebaseAuth();
//        isIndividual();

        Button btnIndividual = findViewById(R.id.btn_individual);
        btnIndividual.setOnClickListener(this);
        Button btnBusiness = findViewById(R.id.btn_business);
        btnBusiness.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_individual:
                startActivity(new Intent(this, StartActivity.class));
                break;

            case R.id.btn_business:
                startActivity(new Intent(this, MainActivity.class));
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    public void checkIfIsIndividual() {
        Log.d(TAG, "isIndividual: checking if user is an individual");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isIndividual = preferences.getBoolean(PreferenceKeys.INDIVIDUAL, true);
        boolean isBusiness = preferences.getBoolean(PreferenceKeys.BUSINESS, true);

        if(isIndividual) {
            Log.d(TAG, "isIndividual: navigating to individual activity");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(PreferenceKeys.INDIVIDUAL, false);
            editor.apply();

            startActivity(new Intent(this, IndividualActivity.class));
            finish();
        } else if(isBusiness){
            Log.d(TAG, "isBusiness: navigating to business activity");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(PreferenceKeys.BUSINESS, false);
            editor.apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null.");
        } else {
            Log.d(TAG, "checkAuthenticationState: User is authenticated");
//            checkIfIsIndividual();
            startActivity(new Intent(this, IndividualActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
//        if(mAuth.getCurrentUser() != null) {
//            checkIfIsIndividual();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    /*
    -------------------------------- Firebase Setup -------------------------
    */
    private void setupFirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in " + user.getUid());
                    checkIfUserIsVerified(user);
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    private void checkIfUserIsVerified(FirebaseUser user) {
        if(user.isEmailVerified()) {
//            Log.d(TAG, "onAuthStateChanged: signed in " + user.getUid());
//            Toast.makeText(this, "Authenticated with: " + user.getEmail(),
//                    Toast.LENGTH_LONG).show();
//            checkIfIsIndividual();
            Intent intent = new Intent(this, IndividualActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Check your Email inbox for a verification " +
                    "link", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
        }
    }
}