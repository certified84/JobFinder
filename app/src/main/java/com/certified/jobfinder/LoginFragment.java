package com.certified.jobfinder;

import android.app.AlertDialog;
import android.content.Context;
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
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    private StartActivityViewModel mViewModel;

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

        mViewModel = new StartActivityViewModel(getActivity().getApplication());
        setupFirebaseAuth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmail = view.findViewById(R.id.et_email);
        mPassword = view.findViewById(R.id.et_password);
        mProgressBar = view.findViewById(R.id.progressBar);
        mLogin = view.findViewById(R.id.btn_login);
        mForgotPassword = view.findViewById(R.id.tv_password_reset);
        mRegister = view.findViewById(R.id.tv_register);
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
            case R.id.btn_login:
                Log.d(TAG, "onClick: Thread = " + Thread.currentThread().getId());
                mViewModel.signInUser(
                        getContext(),
                        mUser,
                        mEmail.getText().toString().trim(),
                        mPassword.getText().toString().trim(),
                        mProgressBar
                );
                break;

            case R.id.tv_register:
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
            mViewModel.resetPassword(
                    getContext(),
                    alertDialog,
                    etEmail.getText().toString().trim(),
                    progressBar
            );
        });
        alertDialog.show();
    }

    private void hideSoftKeyboard() {
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUser != null) {
            mViewModel.checkIfUserIsVerified(getContext(), mUser);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    /*
    -------------------------------- Firebase Setup -------------------------
    */
    public void setupFirebaseAuth() {
        mAuthStateListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();
            if (mUser != null) {
                Log.d(TAG, "onAuthStateChanged: signed in" + mUser.getUid());
                mViewModel.checkIfUserIsVerified(getContext(), mUser);
            } else {
                Log.d(TAG, "onAuthStateChanged: signed out");
            }
        };
    }
}