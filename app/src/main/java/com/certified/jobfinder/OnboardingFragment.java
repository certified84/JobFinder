package com.certified.jobfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.certified.jobfinder.adapters.ViewPagerAdapter;
import com.certified.jobfinder.model.SliderItem;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

import static android.text.TextUtils.isEmpty;

public class OnboardingFragment extends Fragment{

    private static final String TAG = "OnboardingFragment";
    private NavController mNavController;

    //    firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mAuth;


    private ViewPagerAdapter mViewPagerAdapter;
    private CircleIndicator3 mIndicator;
    private List<SliderItem> mSliderItems;
    private ViewPager2 mViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseAuth();
    }

    public OnboardingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(view);
        mViewPager = view.findViewById(R.id.view_pager2);
        mIndicator = view.findViewById(R.id.indicator);

        Button btnGetStarted = view.findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(view1 -> {
            mNavController.navigate(R.id.action_onboardingFragment_to_loginFragment);
        });

        mSliderItems = new ArrayList<>();
        mSliderItems.add(new SliderItem(R.raw.animation_job_alert, "Get notified with jobs",
                "With every job that matches your\nprofile, you will get notified"));

        mViewPagerAdapter = new ViewPagerAdapter(mSliderItems, mViewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mIndicator.setViewPager(mViewPager);
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
            Log.d(TAG, "checkAuthenticationState: User is authenticated with: " +
                    FirebaseAuth.getInstance().getCurrentUser().getEmail());
//            startActivity(new Intent(getContext(), IndividualActivity.class));
            checkIfUserIsVerified(user);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
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
    private void setupFirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in " + user.getEmail());
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

    private void queryDatabase() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String accountType = preferences.getString(PreferenceKeys.ACCOUNT_TYPE, "");
        if (!isEmpty(accountType)) {
            if (accountType.equals("Business")) {
                navigateToBusinessActivity();
            } else if (accountType.equals("Individual")) {
                navigateToIndividualActivity();
            }
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (user != null) {
                DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());
                userRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String accountType1 = documentSnapshot.getString("account_type");
                            if (accountType1.equals(getString(R.string.individual))) {
                                Log.d(TAG, "checkAuthenticationState: User is authenticated with an individual account");
                                navigateToIndividualActivity();
                            } else if (accountType1.equals(getString(R.string.business))) {
                                Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                                navigateToBusinessActivity();
                            }
                        });
            }
        }

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//        Query query = reference.child(getString(R.string.dbnode_users))
//                .orderByKey()
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                //this loop will return a single result
//                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
//                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found user: "
//                            + singleSnapshot.getValue(User.class).toString());
//                    User user = singleSnapshot.getValue(User.class);
//                    String level = user.getLevel();
//
//                    if (level.equals(getString(R.string.individual))) {
//                        Log.d(TAG, "checkAuthenticationState: User is authenticated with an individual account");
//                        Intent intent = new Intent(getContext(), IndividualActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    } else if (level.equals(getString(R.string.business))) {
//                        Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
//                        Intent intent = new Intent(getContext(), BusinessActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    public void navigateToBusinessActivity() {
        Intent intent = new Intent(getContext(), BusinessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void navigateToIndividualActivity() {
        Intent intent = new Intent(getContext(), IndividualActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}