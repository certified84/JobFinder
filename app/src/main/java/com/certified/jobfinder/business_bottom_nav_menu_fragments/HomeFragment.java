package com.certified.jobfinder.business_bottom_nav_menu_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.certified.jobfinder.R;
import com.certified.jobfinder.adapters.JobSeekersAdapter;
import com.certified.jobfinder.adapters.JobsRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private JobsRecyclerAdapter mJobsRecyclerAdapter;
    private JobSeekersAdapter mJobSeekersAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView jobsRecyclerView;
    private FloatingActionButton newOffer;
    private BottomNavigationView mBottomNavigationView;
    private NavController mNavController;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth.getInstance().getCurrentUser();
        jobsRecyclerView = view.findViewById(R.id.recylcler_view_home);
        mJobSeekersAdapter = new JobSeekersAdapter(getContext());
        jobsRecyclerView.setAdapter(mJobSeekersAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        jobsRecyclerView.setLayoutManager(mLinearLayoutManager);

        mBottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        mNavController = Navigation.findNavController(getActivity(), R.id.business_host_fragment);

        newOffer = view.findViewById(R.id.fab_new_offer);
        newOffer.setOnClickListener(view1 -> {
            mNavController.navigate(R.id.action_homeFragment_to_newOfferFragment);
//            BusinessActivity.mBottomNavigationView.setVisibility(View.GONE);
        });
    }

}