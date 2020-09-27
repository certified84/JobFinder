package com.certified.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.certified.jobfinder.model.User;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "LoginFragment";
    public NavController mNavController;

    //    firebase
    public FirebaseAuth.AuthStateListener mAuthStateListener;

    //    widgets
    public TextView mForgotPassword, mResendEmail, mRegister;
    public EditText mEmail, mPassword;
    public ProgressBar mProgressBar;
    public Button mLogin;
    public ImageView mBack, google, facebook, twitter;

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
        mLogin = view.findViewById(R.id.btn_login);
        mForgotPassword = view.findViewById(R.id.tv_password_reset);
        mRegister = view.findViewById(R.id.tv_register);
        mBack = view.findViewById(R.id.back);
        google = view.findViewById(R.id.google);
        facebook = view.findViewById(R.id.facebook);
        twitter = view.findViewById(R.id.twitter);

        mNavController = Navigation.findNavController(view);

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mBack.setOnClickListener(this);
        google.setOnClickListener(this);
        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
//                navigateToBusinessActivity();
                Log.d(TAG, "onClick: Thread = " + Thread.currentThread().getId());
                signInUser();
                break;

            case R.id.tv_register:
                Log.d(TAG, "onClick: button register clicked");
                mNavController.navigate(R.id.action_loginFragment_to_registerFragment);
//                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.startFragment, true).build();
                break;

            case R.id.tv_password_reset:
                sendPasswordResetLink();
                break;

            case R.id.back:
                Log.d(TAG, "onClick: back clicked");
                mNavController.navigate(R.id.action_loginFragment_to_startFragment);
                break;

            case R.id.google:

            case R.id.facebook:

            case R.id.twitter:
                Snackbar.make(v, "This feature isn't available yet. " +
                        "Kindly check back later", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void sendPasswordResetLink() {
    }

    private void navigateToBusinessActivity() {
        startActivity(new Intent(getContext(), BusinessActivity.class));
        getActivity().finish();
    }

    private void signInUser() {
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!isEmpty(mEmail.getText().toString()) && !isEmpty(mPassword.getText().toString())) {

            Log.d(TAG, "onClick: Attempting to authenticate");
            mProgressBar.setVisibility(View.VISIBLE);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                    mPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                Log.d(TAG, "onComplete: signed in with: " + user.getEmail());
                                mProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Authenticated with: " +
                                                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                                Toast.LENGTH_LONG).show();

//                                        Log.d(TAG, "onComplete: Navigating to individual activity");

//                                        Intent intent = new Intent(getContext(), IndividualActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(intent);

                                checkIfUserIsVerified(FirebaseAuth.getInstance().getCurrentUser());

                            } else {
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Unable to register. " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onFailure: Unable to register. " + Thread.currentThread().getId());
                }
            });
        } else {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(getContext(), IndividualActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if (level.equals(getString(R.string.business))) {
                        Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                        Intent intent = new Intent(getContext(), BusinessActivity.class);
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

//    public void checkIfUserIsVerified(FirebaseUser user) {
//        if (user.isEmailVerified()) {
//            Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
//            Toast.makeText(getContext(), "Authenticated with: " + user.getEmail(),
//                    Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(getContext(), IndividualActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        } else {
//            Toast.makeText(getContext(), "Check your Email inbox for a verification " +
//                    "link", Toast.LENGTH_LONG).show();
//            FirebaseAuth.getInstance().signOut();
//        }
//    }

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