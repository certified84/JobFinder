package com.certified.jobfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.certified.jobfinder.R;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    //    firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.d(TAG, "onCreate: called");

        setupFirebaseAuth();

    }

    public void clickHandler(View view) {
        switch(view.getId()) {

            case R.id.google:

            case R.id.facebook:

            case R.id.twitter:

            case R.id.btn_facebook:

            case R.id.btn_google:

            case R.id.btn_twitter:
//                Toast.makeText(this, "This feature is not available yet. " +4
                Snackbar.make(view, "This feature isn't available yet. " +
                        "Kindly check back later", Snackbar.LENGTH_LONG).show();
                break;

            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.tv_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
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
//            isIndividual();
        } else {
            Log.d(TAG, "checkAuthenticationState: User is authenticated");
            startActivity(new Intent(this, IndividualActivity.class));
            finish();
        }
    }

    public void isIndividual() {
        Log.d(TAG, "isIndividual: checking if user is an individual");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isIndividual = preferences.getBoolean(PreferenceKeys.INDIVIDUAL, true);
        boolean isBusiness = preferences.getBoolean(PreferenceKeys.BUSINESS, true);

        if (isIndividual) {
            Log.d(TAG, "isIndividual: navigate to individual activity");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(PreferenceKeys.INDIVIDUAL, false);
            editor.apply();

            startActivity(new Intent(this, IndividualActivity.class));
            finish();
        } else if (isBusiness) {
            Log.d(TAG, "isBusiness: navigating to business activity");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(PreferenceKeys.BUSINESS, false);
            editor.apply();

            startActivity(new Intent(this, MainActivity.class));
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

//            navigate back to choose fragment
        }
    }
}