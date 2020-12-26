package com.certified.jobfinder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.certified.jobfinder.adapters.ViewPagerAdapter;
import com.certified.jobfinder.model.SliderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private static final String TAG = "OnboardingFragment";
    private NavController mNavController;

    //    firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    private Repository mRepository;

    private ViewPagerAdapter mViewPagerAdapter;
    private PageIndicatorView mIndicator;
    private List<SliderItem> mSliderItems;
    private ViewPager2 mViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = new Repository(getActivity().getApplication());
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
                "Finding your dream job is now much\neasier and faster like never before", R.drawable.image_search));
        mSliderItems.add(new SliderItem(R.raw.animation_job_alert, "Get notified with jobs",
                "With every job that matches your\nprofile, you will get notified", R.drawable.undraw_push_notifications_im0o));
        mSliderItems.add(new SliderItem(R.raw.animation_job_alert, "Apply and get selected",
                "Apply to your preferred job\nthat matches your skill", R.drawable.image_apply_2));
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
            mRepository.checkIfUserIsVerified();
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
        mAuthStateListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();
            if (mUser != null) {
                Log.d(TAG, "onAuthStateChanged: signed in " + mUser.getEmail());
                mRepository.checkIfUserIsVerified();
            } else {
                Log.d(TAG, "onAuthStateChanged: signed out");
            }
        };
    }
}