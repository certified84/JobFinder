package com.certified.jobfinder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView ivProfileImage;
    private TextView tvName, tvEmail;
    private SwitchMaterial switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ivProfileImage = findViewById(R.id.iv_profile_image);
        tvName = findViewById(R.id.tv_display_name);
        tvEmail = findViewById(R.id.tv_email);
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkMode = preferences.getBoolean(PreferenceKeys.DARK_MODE, false);

        switchDarkMode.setOnClickListener(view -> {
            if (!darkMode) {
                switchDarkMode.setChecked(true);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(PreferenceKeys.DARK_MODE, true);
                editor.apply();

                int nightMode = AppCompatDelegate.getDefaultNightMode();
                //Set the theme mode for the restarted activity
                if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_NO);
                }
                recreate();
            }
        });

        Glide.with(this)
                .load(R.drawable.logo)
                .into(ivProfileImage);
        tvName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        tvEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}