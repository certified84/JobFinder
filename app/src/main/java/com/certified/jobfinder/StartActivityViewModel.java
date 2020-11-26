package com.certified.jobfinder;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.NavController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivityViewModel extends AndroidViewModel {

    private final StartActivityRepository mRepository;

    public StartActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository = new StartActivityRepository();
    }

    public void signInUser(Context context, FirebaseUser user, String email, String password, ProgressBar progressBar) {
        mRepository.signInUser(context, user, email, password, progressBar);
    }

    public void checkIfUserIsVerified(Context context, FirebaseUser user) {
        mRepository.checkIfUserIsVerified(context, user);
    }

    public void checkAuthenticationState(Context context, FirebaseUser user) {
        mRepository.checkAuthenticationState(context, user);
    }

    public void resetPassword(Context context, AlertDialog alertDialog, String email, ProgressBar progressBar) {
        mRepository.resetPassword(context, alertDialog, email, progressBar);
    }

    public void registerNewUser(Context context, FirebaseUser user, String email, String password,
                                ProgressBar progressBar, String name, String phoneNo, Spinner spinner, NavController navController) {

        mRepository.registerNewUser(context, user, email, password, progressBar, name, phoneNo, spinner, navController);
    }
}