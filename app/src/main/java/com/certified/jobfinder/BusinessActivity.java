package com.certified.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.certified.jobfinder.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;

public class BusinessActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BusinessActivity";
    public static BottomNavigationView mBottomNavigationView;
    private AppBarConfiguration mAppBarConfiguration;

    private NavController mNavController;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mBottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Passing each menu_individual ID as a set of Ids because each
        // menu_individual should be considered as top level destinations.

//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.homeFragment, R.id.aboutFragment, R.id.profileFragment, R.id.jobsFragment,
//                R.id.alertsFragment, R.id.contactFragment, R.id.helpFragment, R.id.nav_log_out)
//                .setDrawerLayout(mDrawer)
//                .build();

        init();
    }

    public void init() {
        mNavController = Navigation.findNavController(this, R.id.individual_host_fragment);

        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);

        NavigationUI.setupActionBarWithNavController(this, mNavController);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mDrawer);

        mNavigationView.setNavigationItemSelectedListener(this);
//        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkAuthenticationState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBottomNavigationView.setVisibility(View.VISIBLE);
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
            Toast.makeText(this, "Check your Email inbox for a verification mail" +
                    "link", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(BusinessActivity.this, StartActivity.class));
            finish();
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

                    if (level.equals(getString(R.string.individual))) {
                        Log.d(TAG, "checkAuthenticationState: User is authenticated with an individual account");
                        Intent intent = new Intent(BusinessActivity.this, IndividualActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
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

            case R.id.notify:
                notifyMe();
                break;

//            default:
//                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    private void notifyMe() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.aboutFragment:
                Navigation.findNavController(this, R.id.business_host_fragment).navigate(R.id.aboutFragment);
                break;

            case R.id.homeFragment:
                Navigation.findNavController(this, R.id.business_host_fragment).navigate(R.id.homeFragment);
                break;

            case R.id.contactFragment:
                Navigation.findNavController(this, R.id.business_host_fragment).navigate(R.id.contactFragment);
                break;

            case R.id.helpFragment:
                Navigation.findNavController(this, R.id.business_host_fragment).navigate(R.id.helpFragment);
                break;

            case R.id.nav_sign_out:
                FirebaseAuth.getInstance().signOut();
                navigateToStartActivity();
                break;

            case R.id.settingsFragment:
                return false;
        }
        mDrawer.closeDrawer(GravityCompat.START);
//        item.setChecked(true);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.business_host_fragment), mDrawer);
    }
}