package com.certified.jobfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.certified.jobfinder.botton_nav_menu_fragments.AlertFragment;
import com.certified.jobfinder.botton_nav_menu_fragments.HomeFragment;
import com.certified.jobfinder.botton_nav_menu_fragments.JobsFragment;
import com.certified.jobfinder.botton_nav_menu_fragments.ProfileFragment;
import com.certified.jobfinder.model.FragmentTag;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class IndividualActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "IndividualActivity";
    private BottomNavigationView mBottomNavigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private HomeFragment mHomeFragment;
    private FragmentTransaction mTransaction;

    private ArrayList<String> mFragmentsTags = new ArrayList<>();
    private ArrayList<FragmentTag> mFragments = new ArrayList<>();
    private ProfileFragment mProfileFragment;
    private JobsFragment mJobsFragment;
    private AlertFragment mAlertFragment;
    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inidividual);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

//        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
//        init();
    }

    private void init() {
//        mHomeFragment = new HomeFragment();
//        mTransaction = getSupportFragmentManager().beginTransaction();
//        mTransaction.replace(R.id.main_content_frame, mHomeFragment, getString(R.string.tag_home_fragment));
//        mTransaction.addToBackStack(getString(R.string.tag_home_fragment));
//        mTransaction.commit();

        if(mHomeFragment == null) {
            Log.d(TAG, "init: initializing home fragment");
            mHomeFragment = new HomeFragment();
            mTransaction = getSupportFragmentManager().beginTransaction();
            mTransaction.add(R.id.nav_host_fragment, mHomeFragment, getString(R.string.tag_fragment_home));
            mTransaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_home));
            mFragments.add(new FragmentTag(mHomeFragment, getString(R.string.tag_fragment_home)));
        }
        else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_home));
            mFragmentsTags.add(getString(R.string.tag_fragment_home));
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
            Log.d(TAG, "checkAuthenticationState: User is null. Navigating login screen");
//            isIndividual();
            Intent intent = new Intent(this, IndividualStartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "checkAuthenticationState: User is authenticated with: " + FirebaseAuth.getInstance()
                    .getCurrentUser().getEmail());
            Toast.makeText(this, "Authenticated with: " + FirebaseAuth.getInstance()
                    .getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
        }
    }

    public void isIndividual() {
        Log.d(TAG, "isIndividual: checking if user is an individual");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isIndividual = preferences.getBoolean(PreferenceKeys.INDIVIDUAL, true);
        boolean isBusiness = preferences.getBoolean(PreferenceKeys.BUSINESS, true);

        if(isIndividual) {
            Log.d(TAG, "isIndividual: navigate to individual activity");
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
                startActivity(new Intent(this, ChooseActivity.class));
                finish();
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

        switch(item.getItemId()) {
            case R.id.homeFragment:
//                init();
//                mNavController.navigate(R.id.action_alertFragment_to_homeFragment);
                Log.d(TAG, "onClick: Home clicked");
                break;

            case R.id.profileFragment:
//                initProfileFragment();
//                mNavController.navigate(R.id.action_homeFragment_to_profileFragment);
                Log.d(TAG, "onClick: Profile clicked");
                break;

            case R.id.jobsFragment:
//                initJobsFragment();
//                mNavController.navigate(R.id.action_homeFragment_to_jobsFragment);
                Log.d(TAG, "onClick: Jobs clicked");
                break;

            case R.id.alertsFragment:
//                initAlertFragment();
//                mNavController.navigate(R.id.action_homeFragment_to_alertFragment);
                Log.d(TAG, "onClick: Alerts clicked");
                break;
        }
        item.setChecked(true);
        return true;
    }

    private void initAlertFragment() {
        if(mJobsFragment == null) {
            Log.d(TAG, "initAlertFragment: Initializing alerts fragment");
            mAlertFragment = new AlertFragment();
            mTransaction = getSupportFragmentManager().beginTransaction();
            mTransaction.add(R.id.nav_host_fragment, mAlertFragment, getString(R.string.tag_fragment_alert));
            mTransaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_alert));
            mFragments.add(new FragmentTag(mAlertFragment, getString(R.string.tag_fragment_alert)));
        }
        else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_alert));
            mFragmentsTags.add(getString(R.string.tag_fragment_alert));
        }
    }

    private void initJobsFragment() {
        if(mJobsFragment == null) {
            Log.d(TAG, "initAlertFragment: Initializing jobs fragment");
            mJobsFragment = new JobsFragment();
            mTransaction = getSupportFragmentManager().beginTransaction();
            mTransaction.add(R.id.nav_host_fragment, mJobsFragment, getString(R.string.tag_fragment_jobs));
            mTransaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_jobs));
            mFragments.add(new FragmentTag(mJobsFragment, getString(R.string.tag_fragment_jobs)));
        }
        else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_jobs));
            mFragmentsTags.add(getString(R.string.tag_fragment_jobs));
        }
    }

    private void initProfileFragment() {
        if(mProfileFragment == null) {
            Log.d(TAG, "initAlertFragment: Initializing profile fragment");
            mProfileFragment = new ProfileFragment();
            mTransaction = getSupportFragmentManager().beginTransaction();
            mTransaction.add(R.id.nav_host_fragment, mProfileFragment, getString(R.string.tag_fragment_profile));
            mTransaction.commit();
            mFragmentsTags.add(getString(R.string.tag_fragment_home));
            mFragments.add(new FragmentTag(mProfileFragment, getString(R.string.tag_fragment_profile)));
        }
        else {
            mFragmentsTags.remove(getString(R.string.tag_fragment_profile));
            mFragmentsTags.add(getString(R.string.tag_fragment_profile));
        }
    }

    //    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

}