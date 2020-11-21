package com.certified.jobfinder;

import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_individual);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        enableStrictMode();

        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        mNavController = Navigation.findNavController(this, R.id.individual_host_fragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
//            w.setNavigationBarColor(getResources().getColor(R.color.white));
        }

        isFirstLogin();
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
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null. Navigating to start activity");
            navigateToStartActivity();
        } else {
            Log.d(TAG, "checkAuthenticationState: Authenticated with: " + user.getEmail());
            checkIfUserIsVerified(FirebaseAuth.getInstance().getCurrentUser());
        }
    }

    private void navigateToStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void navigateToBusinessActivity() {
        Intent intent = new Intent(this, BusinessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void checkIfUserIsVerified(FirebaseUser user) {

        if (user.isEmailVerified()) {
            queryDatabase();
        } else {
            Toast.makeText(this, "Check your Email inbox for a verification " +
                    "link", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
            navigateToStartActivity();
        }
    }

    private void queryDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    String accountType = documentSnapshot.getString("account_type");
                    if (accountType.equals(getString(R.string.business))) {
                        Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                        navigateToBusinessActivity();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_individual_menu, menu);
        mBottomNavigationView.setupWithNavController(menu, mNavController);
        return true;
    }
}