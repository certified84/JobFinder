package com.certified.jobfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.certified.jobfinder.model.User;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.text.TextUtils.isEmpty;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private ImageView splashLogo;
    private Button btnRetry;
    private ProgressBar mProgressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.setStatusBarColor(Color.TRANSPARENT);
        }

//        splashLogo = findViewById(R.id.splash_logo);
        handler = new Handler();
        handler.postDelayed(() -> {
            Log.d(TAG, "run: Thread = " + Thread.currentThread().getId());
//            checkConnectivity();
//                navigateToBusinessActivity();
            checkAuthenticationState();
        }, 3000);
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null");
            startActivity(new Intent(this, StartActivity.class));
            finish();
        } else {
            queryDatabase();
        }
    }

    private void queryDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user != null) {
            DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String accountType = documentSnapshot.getString("account_type");
                        if (accountType.equals(getString(R.string.individual))) {
                            Log.d(TAG, "checkAuthenticationState: User is authenticated with an individual account");
                            navigateToIndividualActivity();
                        } else if (accountType.equals(getString(R.string.business))) {
                            Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                            navigateToBusinessActivity();
                        }
                    });
        }
    }

    public void navigateToBusinessActivity() {
        Intent intent = new Intent(this, BusinessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToIndividualActivity() {
        Intent intent = new Intent(this, IndividualActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
