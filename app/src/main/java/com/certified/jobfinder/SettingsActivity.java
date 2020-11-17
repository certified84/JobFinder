package com.certified.jobfinder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout mLayout;
    private CircleImageView ivProfileImage;
    private TextView tvName, tvEmail;

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

        mLayout = findViewById(R.id.layout);
        ivProfileImage = findViewById(R.id.iv_profile_image);
        tvName = findViewById(R.id.tv_display_name);
        tvEmail = findViewById(R.id.tv_email);

//        mLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                N
//            }
//        });

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