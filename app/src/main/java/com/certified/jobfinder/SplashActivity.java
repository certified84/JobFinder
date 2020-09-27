package com.certified.jobfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.certified.jobfinder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        handler = new Handler();
        handler.postDelayed(() -> {
            Log.d(TAG, "run: Thread = " + Thread.currentThread().getId());
            checkConnectivity();
//                navigateToBusinessActivity();
        }, 3000);
    }

    private void checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnected();
        if (isConnected) {
            Log.d(TAG, "checkConnectivity: Connected");
            checkAuthenticationState();
        } else {
            Log.d(TAG, "checkConnectivity: No connection");
//            displayFailureDialog();
            initFailureFragment();
        }
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null");
            startActivity(new Intent(this, StartActivity.class));
            finish();
//            navigateToBusinessActivity();
        } else {
            queryDatabase();
//            navigateToBusinessActivity();
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
                        Intent intent = new Intent(SplashActivity.this, IndividualActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if (level.equals(getString(R.string.business))) {
                        Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                        Intent intent = new Intent(SplashActivity.this, BusinessActivity.class);
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

    public void navigateToBusinessActivity() {
        startActivity(new Intent(this, BusinessActivity.class));
        finish();
    }

    private void navigateToIndividualActivity() {
        startActivity(new Intent(this, IndividualActivity.class));
        finish();
    }

    private void initFailureFragment() {
        Log.d(TAG, "initFailureFragment: called");
        FailureFragment failureFragment = new FailureFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.splash_frame, failureFragment, "Second Fragment");
        transaction.commit();
//        finish();
    }

    public void displayFailureDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.failure_dialog, null);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(v);
        alertDialog.show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                finish();
            }
        });
    }
}
