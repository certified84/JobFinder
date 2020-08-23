package com.certified.jobfinder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "StartFragment";
    private NavController mNavController;

    //    firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseAuth();
    }

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvRegister = view.findViewById(R.id.tv_register);
        TextView tvLogin = view.findViewById(R.id.tv_login);
        Button google = view.findViewById(R.id.btn_google);
        Button facebook = view.findViewById(R.id.btn_facebook);
        Button twitter = view.findViewById(R.id.btn_twitter);

        tvRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        google.setOnClickListener(this);
        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);

        mNavController = Navigation.findNavController(view);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                Log.d(TAG, "onClick: button register clicked");
                mNavController.navigate(R.id.action_startFragment_to_registerFragment);
                break;

            case R.id.tv_login:
                Log.d(TAG, "onClick: button login clicked");
                mNavController.navigate(R.id.action_startFragment_to_loginFragment);
                break;

            case R.id.btn_google:
                Log.d(TAG, "onClick: google clicked");

            case R.id.btn_facebook:
                Log.d(TAG, "onClick: facebook clicked");

            case R.id.btn_twitter:
                Log.d(TAG, "onClick: twitter clicked");
                Snackbar.make(v, "This feature isn't available yet. Kindly check back later.", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null.");
//            isIndividual();
        } else {
            Log.d(TAG, "checkAuthenticationState: User is authenticated");
            startActivity(new Intent(getContext(), IndividualActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
//        if(mAuth.getCurrentUser() != null) {
//            checkIfIsIndividual();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    /*
-------------------------------- Firebase Setup -------------------------
*/
    private void setupFirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in " + user.getUid());
                    checkIfUserIsVerified(user);
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    private void checkIfUserIsVerified(FirebaseUser user) {
        if(user.isEmailVerified()) {
//            Log.d(TAG, "onAuthStateChanged: signed in " + user.getUid());
//            Toast.makeText(this, "Authenticated with: " + user.getEmail(),
//                    Toast.LENGTH_LONG).show();
//            checkIfIsIndividual();
            Intent intent = new Intent(getContext(), IndividualActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Check your Email inbox for a verification " +
                    "link", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
        }
    }
}