package com.certified.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.certified.jobfinder.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class IndividualActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "IndividualActivity";
    private BottomNavigationView mBottomNavigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inidividual);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        enableStrictMode();
        Log.d(TAG, "onCreate: Thread = " + Thread.currentThread().getName());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        mBottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Passing each menu_individual ID as a set of Ids because each
        // menu_individual should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.aboutFragment, R.id.contactFragment, R.id.helpFragment,
                R.id.nav_sign_out, R.id.jobsFragment, R.id.alertsFragment, R.id.profileFragment)
                .setDrawerLayout(drawer)
                .build();

        mNavController = Navigation.findNavController(this, R.id.individual_host_fragment);
        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

//        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
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
//        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null. Navigating to start activity");
            navigateToStartActivity();
        } else {
            checkIfUserIsVerified(FirebaseAuth.getInstance().getCurrentUser());
        }
    }

    private void navigateToStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);
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
        }
    }

    private void queryDatabase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users))
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //this loop will return a single result
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found user: "
                            + singleSnapshot.getValue(User.class).toString());
                    User user = singleSnapshot.getValue(User.class);
                    String level = user.getLevel();

                    if (level.equals(getString(R.string.business))) {
                        Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                        Intent intent = new Intent(IndividualActivity.this, BusinessActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_individual; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_individual, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sign_out:
                Log.d(TAG, "onOptionsItemSelected: sign out user");

//                sign user out
                FirebaseAuth.getInstance().signOut();

//                Redirecting to login activity
                navigateToStartActivity();
                break;

            case R.id.action_settings:

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        switch(item.getItemId()) {
//            case R.id.homeFragment:
//                Log.d(TAG, "onClick: Home clicked");
//                break;
//
//            case R.id.profileFragment:
//                Log.d(TAG, "onClick: Profile clicked");
//                break;
//
//            case R.id.jobsFragment:
//                Log.d(TAG, "onClick: Job clicked");
//                break;
//
//            case R.id.alertsFragment:
//                Log.d(TAG, "onClick: Alerts clicked");
//                break;
//        }
//        item.setChecked(true);
        return true;
    }

//        @Override
//        public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//        }
}