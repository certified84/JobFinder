package com.certified.jobfinder;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.preference.PreferenceManager;

import com.certified.jobfinder.model.User;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.text.TextUtils.isEmpty;

public class Repository {

    private static final String TAG = "Repository";

    private final Application mApplication;
    private final FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public Repository(Application application) {
        mApplication = application;
        mAuth = FirebaseAuth.getInstance();
    }

    public void signInUser(String email, String password, ProgressBar progressBar) {
        if (!isEmpty(email) && !isEmpty(password)) {
            Log.d(TAG, "onClick: Attempting to authenticate");
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
//                            if (mUser != null) {
//                            Log.d(TAG, "signInUser: Authenticated with: " + mUser.getEmail());
                            mUser = mAuth.getCurrentUser();
                            checkIfUserIsVerified();
                            Toast.makeText(mApplication, "Authenticated with: " + mUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
//                            }
                        } else {
                            Log.d(TAG, "signInUser: Unable to login: " + task.getException());
                            Toast.makeText(mApplication, "Unable to login: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        } else {
            Toast.makeText(mApplication, "All fields are required", Toast.LENGTH_LONG).show();
        }
    }

    public void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            Log.d(TAG, "checkAuthenticationState: User is null.");
            navigateToStartActivity();
        } else {
            checkIfUserIsVerified();
        }
    }

    public void checkIfUserIsVerified() {
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            if (mUser.isEmailVerified()) {
                Log.d(TAG, "checkIfUserIsVerified: User is email verified");
                queryDatabase();
            } else {
                Toast.makeText(mApplication, "Check your Email inbox for a verification " +
                        "link", Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
            }
        }
    }

    private void queryDatabase() {
        mUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(mUser.getUid());
        Log.d(TAG, "queryDatabase: Querying database...");
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String accountType = documentSnapshot.getString("account_type");

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mApplication);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(PreferenceKeys.ACCOUNT_TYPE, accountType);
                        editor.apply();

                        if (accountType.equals(mApplication.getString(R.string.individual))) {
                            Log.d(TAG, "queryDatabase: User is authenticated with an individual account");
                            navigateToIndividualActivity();
                        } else if (accountType.equals(mApplication.getString(R.string.business))) {
                            Log.d(TAG, "queryDatabase: User is authenticated with a business account");
                            navigateToBusinessActivity();
                        }
                    }
                });
    }

    public void navigateToStartActivity() {
        Log.d(TAG, "navigateToStartActivity: Navigating to start activity");
        Intent intent = new Intent(mApplication, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mApplication.startActivity(intent);
    }

    public void navigateToBusinessActivity() {
        Log.d(TAG, "navigateToBusinessActivity: Navigating to business activity");
        Intent intent = new Intent(mApplication, BusinessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mApplication.startActivity(intent);
    }

    public void navigateToIndividualActivity() {
        Log.d(TAG, "navigateToIndividualActivity: Navigating to individual activity");
        Intent intent = new Intent(mApplication, IndividualActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mApplication.startActivity(intent);
    }

    public void resetPassword(AlertDialog alertDialog, String email, ProgressBar progressBar) {
        if (!isEmpty(email)) {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Password reset link sent");
                            Toast.makeText(mApplication, "Check your email for the reset link",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(mApplication, "An error occurred: " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        } else {
            Toast.makeText(mApplication, "Email field is required", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendVerificationEmail() {
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            mUser.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(mApplication, "Verification email sent",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mApplication, "Couldn't send verification " +
                            "email. Try again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void registerNewUser(String email, String password, ProgressBar progressBar, String name,
                                String phoneNo, Spinner spinner, NavController navController) {

        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Created new user with: " + email);

//                            Send the newly created user a verification mail
                        sendVerificationEmail();

//                            Save new user info in firestore
                        saveInfoInFirestore(email, name, phoneNo, spinner, navController);

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(mApplication, "You are already registered", Toast.LENGTH_SHORT).show();
                            redirectToLoginScreen(navController);
                        } else {
                            Toast.makeText(mApplication, "Unable to register: "
                                    + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void saveInfoInFirestore(String email, String name, String phoneNo, Spinner spinner, NavController navController) {
        mUser = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(mUser.getUid());

        User newUser = new User();
        newUser.setName(name);
        newUser.setPhone(phoneNo);
        newUser.setEmail(email);
        newUser.setAccount_type(spinner.getSelectedItem().toString());
        newUser.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newUser.setLocation("");
        newUser.setProfile_image(null);

        userRef.set(mUser)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

//                                    Update default firestore user data
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newUser.getName())
                                .setPhotoUri(newUser.getProfile_image())
                                .build();
                        mUser.updateProfile(profileChangeRequest);

//                                   Sign out the user
                        FirebaseAuth.getInstance().signOut();

//                                   Redirect the user to the login screen
                        Log.d(TAG, "onComplete: redirecting to login screen");
                        redirectToLoginScreen(navController);
                    } else {
                        Log.d(TAG, "onComplete: User Detail upload failed");
                        Toast.makeText(mApplication, "Unable to save detail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToLoginScreen(NavController navController) {
        Log.d(TAG, "redirectToLoginScreen: redirecting");
        navController.navigate(R.id.action_registerFragment_to_loginFragment);
    }
}
