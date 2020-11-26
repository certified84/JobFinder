package com.certified.jobfinder;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private ImageView splashLogo;
    private Button btnRetry;
    private ProgressBar mProgressBar;
    private Handler handler;

    private StartActivityViewModel mViewModel;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mViewModel = new StartActivityViewModel(getApplication());

//        splashLogo = findViewById(R.id.splash_logo);
        handler = new Handler();
        handler.postDelayed(() -> {
            Log.d(TAG, "run: Thread = " + Thread.currentThread().getId());
            mViewModel.checkAuthenticationState(this, mUser);
        }, 3000);
    }
}
