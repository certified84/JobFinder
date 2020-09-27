package com.certified.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.certified.jobfinder.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RegisterFragment";
    private NavController mNavController;

    //    widgets
    private EditText mEmail, mPassword, mConfirmPassword, mName, mPhoneNo;
    private TextView mLogin;
    private Button mRegister;
    private ProgressBar mProgressBar;
    private ImageView mBack, facebook, google, twitter;
    private Spinner mSpinner;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mSpinner = view.findViewById(R.id.spinner2);
        ArrayAdapter<String> level = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        level.add("Business");
        level.add("Individual");
        level.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(level);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmail = view.findViewById(R.id.et_email);
        mPassword = view.findViewById(R.id.et_password);
        mConfirmPassword = view.findViewById(R.id.et_confirm_password);
        mName = view.findViewById(R.id.et_name);
        mPhoneNo = view.findViewById(R.id.et_phone_no);
        mProgressBar = view.findViewById(R.id.progressBar);
        mRegister = view.findViewById(R.id.btn_register);
        mLogin = view.findViewById(R.id.tv_login);
        mBack = view.findViewById(R.id.back);
        facebook = view.findViewById(R.id.facebook);
        google = view.findViewById(R.id.google);
        twitter = view.findViewById(R.id.twitter);
        mSpinner = view.findViewById(R.id.spinner2);

        mBack.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        facebook.setOnClickListener(this);
        google.setOnClickListener(this);
        twitter.setOnClickListener(this);

        mNavController = Navigation.findNavController(view);

        ArrayAdapter<String> level = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        level.add("Business");
        level.add("Individual");
        level.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(level);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                registrationCheck();
                break;

            case R.id.tv_login:
                Log.d(TAG, "onClick: button login clicked");
                mNavController.navigate(R.id.action_registerFragment_to_loginFragment);
                break;

            case R.id.back:
                Log.d(TAG, "onClick: back clicked");
                mNavController.navigate(R.id.action_registerFragment_to_startFragment);
//                NavOptions navOptions = new NavOptions().Builder().setPopUpTo(R.id.startFragment, true).build();
                break;

            case R.id.google:

            case R.id.facebook:

            case R.id.twitter:

            case R.id.btn_facebook:

            case R.id.btn_google:

            case R.id.btn_twitter:
//                Toast.makeText(this, "This feature is not available yet. " +
//                        "Kindly check back later", Toast.LENGTH_LONG).show();
                Snackbar.make(v, "This feature isn't available yet. " +
                        "Kindly check back later", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void registrationCheck() {

//        check for null valued EditText fields
        if (!isEmpty(mEmail.getText().toString())
                && !isEmpty(mPassword.getText().toString())
                && !isEmpty(mConfirmPassword.getText().toString())
                && !isEmpty(mName.getText().toString())
                && !isEmpty(mPhoneNo.getText().toString())) {

//            check if user has a company email address
            if (isValidDomain(mEmail.getText().toString())) {

//                check if password length is greater than 6
                if (mPassword.getText().toString().length() >= 6) {

//                    check if password match
                    if (doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {

                        registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());

                    } else {
                        Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                        mConfirmPassword.requestFocus();
                    }
                } else {
                    Toast.makeText(getContext(), "Password shouldn't be less than 6 characters", Toast.LENGTH_SHORT).show();
                    mPassword.requestFocus();
                }
            } else {
                Toast.makeText(getContext(), "Please register with company email", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "You must fill out all fields", Toast.LENGTH_LONG).show();
        }
    }

    private void registerNewEmail(String email, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: onComplete: " + task.isSuccessful());

                        if (task.isSuccessful()) {
                            mProgressBar.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: AuthState: Authenticated with: " +
                                    FirebaseAuth.getInstance().getCurrentUser().getEmail());

//                            Send the newly created user a verification mail
                            sendVerificationEmail();

                            User user = new User();
                            user.setName(mName.getText().toString());
                            user.setPhone(mPhoneNo.getText().toString());
                            user.setEmail(mEmail.getText().toString());
                            user.setLevel(mSpinner.getSelectedItem().toString());
                            user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.dbnode_users))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

//                                   Sign out the user
                                    FirebaseAuth.getInstance().signOut();

//                                   Redirect the user to the login screen
                                    Log.d(TAG, "onComplete: redirecting to login screen");
                                    redirectToLoginScreen();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mProgressBar.setVisibility(View.GONE);
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(getContext(), "Unable to register: " +
                                            e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                                redirectToLoginScreen();
                            } else {
                                Toast.makeText(getContext(), "Unable to register: "
                                        + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void redirectToLoginScreen() {
        Log.d(TAG, "redirectToLoginScreen: redirecting");
        mNavController.navigate(R.id.action_registerFragment_to_loginFragment);
    }

    private void sendVerificationEmail() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Verification email sent",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Couldn't send verification " +
                                "email. Try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean isEmpty(String String) {
        return String.equals("");
    }

    private boolean doStringsMatch(String s1, String s2) {
        return s1.equals(s2);
    }

    private boolean isValidDomain(String email) {
        Log.d(TAG, "isValidDomain: verifying email has correct domain: " + email);
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        Log.d(TAG, "isValidDomain: users domail: " + domain);
//        return domain.equals(DOMAIN_NAME);
        return true;
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
            Log.d(TAG, "checkAuthenticationState: User is null");
        } else {
            queryDatabase();
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
}