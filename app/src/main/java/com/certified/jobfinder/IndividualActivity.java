package com.certified.jobfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import me.ibrahimsn.lib.SmoothBottomBar;

public class IndividualActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "IndividualActivity";
    private SmoothBottomBar mBottomNavigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private NavOptions mNavOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

//        enableStrictMode();

        mDrawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
//        );
//        mDrawer.addDrawerListener(toggle);
//        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mBottomNavigationView = findViewById(R.id.bottomNavigationView);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.contactFragment, R.id.aboutFragment, R.id.helpFragment)
                .setOpenableLayout(mDrawer)
                .build();

        isFirstLogin();
        init();
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
            alertDialogBuilder.setIcon(R.drawable.logo_one);
            alertDialogBuilder.setTitle("WELCOME");
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void init() {
        mNavController = Navigation.findNavController(this, R.id.individual_host_fragment);

        NavigationUI.setupWithNavController(mNavigationView, mNavController);
        NavigationUI.setupActionBarWithNavController(this, mNavController);
//        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

//        mNavigationView.setNavigationItemSelectedListener(this);
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

        updateNavHeader();
    }

    private void updateNavHeader() {
        View headerView = mNavigationView.getHeaderView(0);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        ImageView navProfileImage = headerView.findViewById(R.id.nav_profile_image);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri profileImageUrl = user.getPhotoUrl();

            if (profileImageUrl != null) {
                Glide.with(this)
                        .load(profileImageUrl)
                        .into(navProfileImage);
            } else {
                Glide.with(this)
                        .load(R.drawable.icon_one)
                        .into(navProfileImage);
            }
            navUserEmail.setText(email);
            navUserName.setText(name);

            navProfileImage.setOnClickListener(view -> {
                mNavOptions = new NavOptions.Builder().setPopUpTo(R.id.profileFragment, true).build();
                Navigation.findNavController(IndividualActivity.this, R.id.individual_host_fragment).navigate(R.id.profileFragment, null, mNavOptions);
                mDrawer.closeDrawer(GravityCompat.START);
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isOpen()) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.profileFragment:
                mNavOptions = new NavOptions.Builder().setPopUpTo(R.id.profileFragment, true).build();
                Navigation.findNavController(this, R.id.individual_host_fragment).navigate(R.id.profileFragment, null, mNavOptions);
                break;

            case R.id.aboutFragment:
                mNavOptions = new NavOptions.Builder().setPopUpTo(R.id.aboutFragment, true).build();
                Navigation.findNavController(this, R.id.individual_host_fragment).navigate(R.id.aboutFragment, null, mNavOptions);
                break;

            case R.id.contactFragment:
                mNavOptions = new NavOptions.Builder().setPopUpTo(R.id.contactFragment, true).build();
                Navigation.findNavController(this, R.id.individual_host_fragment).navigate(R.id.contactFragment, null, mNavOptions);
                break;

            case R.id.helpFragment:
                mNavOptions = new NavOptions.Builder().setPopUpTo(R.id.helpFragment, true).build();
                Navigation.findNavController(this, R.id.individual_host_fragment).navigate(R.id.helpFragment, null, mNavOptions);
                break;

            case R.id.nav_sign_out:
//                FirebaseAuth.getInstance().signOut();
                navigateToStartActivity();
                break;

            case R.id.settingsFragment:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        item.setChecked(true);
        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mDrawer)
                || super.onSupportNavigateUp();
    }
}