package com.certified.jobfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "LoginFragment";
    public NavController mNavController;

    //    firebase
    public FirebaseAuth.AuthStateListener mAuthStateListener;

    //    widgets
    public TextView mForgotPassword, mResendEmail, mRegister;
    public TextInputEditText mEmail, mPassword;
    public ProgressBar mProgressBar;
    public Button mLogin;
    public ImageView mBack;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Login fragment created");

        setupFirebaseAuth();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmail = view.findViewById(R.id.et_email);
        mPassword = view.findViewById(R.id.et_password);
        mProgressBar = view.findViewById(R.id.progressBar);
        mLogin = view.findViewById(R.id.btn_register);
        mForgotPassword = view.findViewById(R.id.tv_password_reset);
        mRegister = view.findViewById(R.id.tv_login);
        mBack = view.findViewById(R.id.btn_back);

        mNavController = Navigation.findNavController(view);

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
//                navigateToBusinessActivity();
                Log.d(TAG, "onClick: Thread = " + Thread.currentThread().getId());
                signInUser();
                break;

            case R.id.tv_login:
                Log.d(TAG, "onClick: button register clicked");
                mNavController.navigate(R.id.action_loginFragment_to_registerFragment);
//                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.startFragment, true).build();
                break;

            case R.id.tv_password_reset:
                launchPasswordResetDialog();
                break;

            case R.id.btn_back:
                Log.d(TAG, "onClick: back clicked");
                mNavController.navigate(R.id.action_loginFragment_to_onboardingFragment);
                break;
        }
    }

    private void launchPasswordResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_password_reset, null);

        alertDialog = builder.create();
        alertDialog.setView(v);

        EditText etEmail = v.findViewById(R.id.et_email);
        TextView tvCancel = v.findViewById(R.id.tv_cancel);
        TextView tvReset = v.findViewById(R.id.tv_password_reset);
        ProgressBar progressBar = v.findViewById(R.id.progressBar);

        tvCancel.setOnClickListener(view -> alertDialog.dismiss());
        tvReset.setOnClickListener(view -> {
            if (!isEmpty(etEmail.getText().toString().trim())) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().sendPasswordResetEmail(etEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: Password reset link sent");
                                    Toast.makeText(getContext(), "Check your email for the reset link",
                                            Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(), "An error occurred: " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Email field is required", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    public void navigateToBusinessActivity() {
        Intent intent = new Intent(getContext(), BusinessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void navigateToIndividualActivity() {
        Intent intent = new Intent(getContext(), IndividualActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void signInUser() {
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!isEmpty(mEmail.getText().toString()) && !isEmpty(mPassword.getText().toString())) {

            Log.d(TAG, "onClick: Attempting to authenticate");
            mProgressBar.setVisibility(View.VISIBLE);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                    mPassword.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                checkIfUserIsVerified(user);
                                Toast.makeText(getContext(), "Authenticated with: " +
                                                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Unable to login: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        mProgressBar.setVisibility(View.GONE);
                    });
        } else {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_LONG).show();
        }
    }

    private void queryDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String accountType = documentSnapshot.getString("account_type");
                        if (accountType.equals(getString(R.string.individual))) {
                            Log.d(TAG, "checkAuthenticationState: User is authenticated with an individual account");
                            navigateToIndividualActivity();
                        } else if (accountType.equals(getString(R.string.business))) {
                            Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                            navigateToBusinessActivity();
                        }
                    }
                });
    }

    private boolean isEmpty(String String) {
        return String.equals("");
    }

    private void hideSoftKeyboard() {
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
    -------------------------------- Firebase Setup -------------------------
    */
    private void setupFirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
                    checkIfUserIsVerified(user);
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }

            }
        };
    }

    private void checkIfUserIsVerified(FirebaseUser user) {
        if (user.isEmailVerified()) {
            queryDatabase();
        } else {
            Toast.makeText(getContext(), "Check your Email inbox for a verification " +
                    "link", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
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
        } else {
            checkIfUserIsVerified(FirebaseAuth.getInstance().getCurrentUser());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
}