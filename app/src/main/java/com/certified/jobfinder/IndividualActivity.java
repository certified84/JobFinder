package com.certified.jobfinder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.certified.jobfinder.util.PreferenceKeys;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import me.ibrahimsn.lib.SmoothBottomBar;

public class IndividualActivity extends AppCompatActivity {

    private static final String TAG = "IndividualActivity";
    private SmoothBottomBar mBottomNavigationView;
    private NavController mNavController;
    private NavOptions mNavOptions;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    private StartActivityViewModel mViewModel;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_individual);

        Log.d(TAG, "onCreate: owner onCreate");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mViewModel = new StartActivityViewModel(getApplication());
//        enableStrictMode();

        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        mNavController = Navigation.findNavController(this, R.id.individual_host_fragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
//            w.setNavigationBarColor(getResources().getColor(R.color.white));
        }

        setupFirebaseAuth();
        isFirstLogin();
        createNotificationChannel();
    }

    public void isFirstLogin() {
        Log.d(TAG, "isFirstLogin: checking if this is the first login");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstLogin = preferences.getBoolean(PreferenceKeys.FIRST_TIME_LOGIN, true);

        if (isFirstLogin) {
            Log.d(TAG, "isFirstLogin: Launching alert dialog");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getString(R.string.first_time_user_message));
            alertDialogBuilder.setPositiveButton("Agree", (dialog, which) -> {
                Log.d(TAG, "onClick: closing AlertDialog");
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(PreferenceKeys.FIRST_TIME_LOGIN, false);
                editor.apply();
                dialog.dismiss();
            });
            alertDialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                Log.d(TAG, "isFirstLogin: Closing AlertDialog and leaving the app");
                dialogInterface.dismiss();
                finish();
            });
            alertDialogBuilder.setIcon(R.drawable.logo);
            alertDialogBuilder.setTitle("WELCOME");
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
        Log.d(TAG, "owner onStart: AuthStateListener added");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUser != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
            Log.d(TAG, "onStart: AuthStateListener removed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: checking if user is authenticated");
        if (mUser != null) {
//            mViewModel.checkIfUserIsVerified(this, mUser);
        }
    }

    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Job Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setDescription("New Job alert");
            manager.createNotificationChannel(channel);

            Log.d(TAG, "createNotificationChannel: Notification channel created");
        }
    }

    /*
    -------------------------------- Firebase Setup -------------------------
    */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: Setting up firebase");
        mAuthStateListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();
            if (mUser != null) {
                Log.d(TAG, "onAuthStateChanged: signed in" + mUser.getUid());
//                mViewModel.checkIfUserIsVerified(this, mUser);
            } else {
                Log.d(TAG, "onAuthStateChanged: signed out");
            }

        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_individual_menu, menu);
        mBottomNavigationView.setupWithNavController(menu, mNavController);
        return true;
    }
}