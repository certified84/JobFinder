package com.certified.jobfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import com.certified.jobfinder.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.sql.StatementEvent;

import static android.text.TextUtils.isEmpty;

public class StartActivityRepository {

    private static final String TAG = "StartActivityRepository";

    public void signInUser(Context context, FirebaseUser user, String email, String password, ProgressBar progressBar) {
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!isEmpty(email) && !isEmpty(password)) {

            Log.d(TAG, "onClick: Attempting to authenticate");
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (user != null) {
                                checkIfUserIsVerified(context, user);
                                Toast.makeText(context, "Authenticated with: " +
                                                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "Unable to login: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        } else {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show();
        }
    }

    private void queryDatabase(Context context, FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(context.getString(R.string.dbnode_users)).document(user.getUid());
        if (user != null) {
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String accountType = documentSnapshot.getString("account_type");
                        if (accountType.equals(context.getString(R.string.individual))) {
                            Log.d(TAG, "checkAuthenticationState: User is authenticated with an individual account");
                            navigateToIndividualActivity(context);
                        } else if (accountType.equals(context.getString(R.string.business))) {
                            Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                            navigateToBusinessActivity(context);
                        }
                    });
        }
    }

    public void navigateToStartActivity(Context context) {
        Intent intent = new Intent(context, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void navigateToBusinessActivity(Context context) {
        Intent intent = new Intent(context, BusinessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void navigateToIndividualActivity(Context context) {
        Intent intent = new Intent(context, IndividualActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void checkIfUserIsVerified(Context context, FirebaseUser user) {
        if (user.isEmailVerified()) {
            queryDatabase(context, user);
        } else {
            Toast.makeText(context, "Check your Email inbox for a verification " +
                    "link", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    public void checkAuthenticationState(Context context, FirebaseUser user) {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null.");
            navigateToStartActivity(context);
        } else {
            checkIfUserIsVerified(context, user);
        }
    }

    public void resetPassword(Context context, AlertDialog alertDialog, String email, ProgressBar progressBar) {
        if (!isEmpty(email)) {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Password reset link sent");
                                Toast.makeText(context, "Check your email for the reset link",
                                        Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(context, "An error occurred: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            Toast.makeText(context, "Email field is required", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendVerificationEmail(Context context, FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Verification email sent",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Couldn't send verification " +
                            "email. Try again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void registerNewUser(Context context, FirebaseUser user, String email, String password,
                                 ProgressBar progressBar, String name, String phoneNo, Spinner spinner, NavController navController) {

        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Created new user with: " + email);

//                            Send the newly created user a verification mail
                        sendVerificationEmail(context, user);

//                            Save new user info in firestore
                        saveInfoInFirestore(context, user, email, password, progressBar, name, phoneNo, spinner, navController);

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(context, "You are already registered", Toast.LENGTH_SHORT).show();
                            redirectToLoginScreen(navController);
                        } else {
                            Toast.makeText(context, "Unable to register: "
                                    + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void saveInfoInFirestore(Context context, FirebaseUser user, String email, String password, ProgressBar progressBar,
                                     String name, String phoneNo, Spinner spinner, NavController navController) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(user.getUid());

        User newUser = new User();
        newUser.setName(name);
        newUser.setPhone(phoneNo);
        newUser.setEmail(email);
        newUser.setAccount_type(spinner.getSelectedItem().toString());
        newUser.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newUser.setLocation("");
        newUser.setProfile_image(null);

        userRef.set(user)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

//                                    Update default firestore user data
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newUser.getName())
                                .setPhotoUri(newUser.getProfile_image())
                                .build();
                        user.updateProfile(profileChangeRequest);

//                                   Sign out the user
                        FirebaseAuth.getInstance().signOut();

//                                   Redirect the user to the login screen
                        Log.d(TAG, "onComplete: redirecting to login screen");
                        redirectToLoginScreen(navController);
                    } else {
                        Log.d(TAG, "onComplete: User Detail upload failed");
                        Toast.makeText(context, "Unable to save detail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToLoginScreen(NavController navController) {
        Log.d(TAG, "redirectToLoginScreen: redirecting");
        navController.navigate(R.id.action_registerFragment_to_loginFragment);
    }
}
