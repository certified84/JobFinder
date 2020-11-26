package com.certified.jobfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.text.TextUtils.isEmpty;

public class OnboardingFragment extends Fragment {

    private static final String TAG = "OnboardingFragment";
    private NavController mNavController;

    //    firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    private StartActivityViewModel mViewModel;

    private ViewPagerAdapter mViewPagerAdapter;
    private PageIndicatorView mIndicator;
    private List<SliderItem> mSliderItems;
    private ViewPager2 mViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new StartActivityViewModel(getActivity().getApplication());
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
            Log.d(TAG, "onViewCreated: navigating to login fragment");
            mNavController.navigate(R.id.action_onboardingFragment_to_loginFragment);
        });

        setUpSliderItem();
        setUpViewPager();
    }

    private void setUpSliderItem() {
        mSliderItems = new ArrayList<>();
        mSliderItems.add(new SliderItem(R.raw.animation_job_alert, "Find perfect job match",
                "Finding your dream job is now much\neasier and faster like never before"));
        mSliderItems.add(new SliderItem(R.raw.animation_job_alert, "Get notified with jobs",
                "With every job that matches your\nprofile, you will get notified"));
        mSliderItems.add(new SliderItem(R.raw.animation_job_alert, "Apply and get selected",
                "Apply to your preferred job\nthat matches your skill"));
    }

    private void setUpViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(mSliderItems, mViewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.setSelection(position);
                if (position == mSliderItems.size() - 1) {
                    mIndicator.setCount(mSliderItems.size());
                    mIndicator.setSelection(position);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUser != null) {
            mViewModel.checkIfUserIsVerified(getContext(), mUser);
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
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in " + mUser.getEmail());
                    mViewModel.checkIfUserIsVerified(getContext(), mUser);
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }
}