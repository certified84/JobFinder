package com.certified.jobfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.certified.jobfinder.util.PreferenceKeys;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private ImageView splashLogo;
    private Button btnRetry;
    private ProgressBar mProgressBar;
    private Handler handler;

    private Repository mRepository;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        mRepository = new Repository(getApplication());

//        splashLogo = findViewById(R.id.splash_logo);
        handler = new Handler();
        handler.postDelayed(() -> {
            mRepository.checkAuthenticationState();
//            checkAuthenticationState();
        }, 3000);
    }

    public void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");
        if (mUser == null) {
            Log.d(TAG, "checkAuthenticationState: User is null.");
            navigateToStartActivity();
        } else {
            checkIfUserIsVerified();
        }
    }

    public void checkIfUserIsVerified() {
        if (mUser != null) {
            if (mUser.isEmailVerified()) {
                Log.d(TAG, "checkIfUserIsVerified: User is email verified");
                queryDatabase();
            } else {
                Toast.makeText(this, "Check your Email inbox for a verification " +
                        "link", Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
            }
        }
    }

    private void queryDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(mUser.getUid());
        Log.d(TAG, "queryDatabase: Querying database...");
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String accountType = documentSnapshot.getString("account_type");

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(PreferenceKeys.ACCOUNT_TYPE, accountType);
                            editor.apply();

                            if (accountType.equals(getString(R.string.individual))) {
                                Log.d(TAG, "queryDatabase: User is authenticated with an individual account");
                                navigateToIndividualActivity();
                            } else if (accountType.equals(getString(R.string.business))) {
                                Log.d(TAG, "queryDatabase: User is authenticated with a business account");
                                navigateToBusinessActivity();
                            }
                        }
                    }
                });
    }

    public void navigateToStartActivity() {
        Log.d(TAG, "navigateToStartActivity: Navigating to start activity");
        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void navigateToBusinessActivity() {
        Log.d(TAG, "navigateToBusinessActivity: Navigating to business activity");
        Intent intent = new Intent(this, BusinessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

    public void navigateToIndividualActivity() {
        Log.d(TAG, "navigateToIndividualActivity: Navigating to individual activity");
        Intent intent = new Intent(this, IndividualActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
