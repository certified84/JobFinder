package com.certified.jobfinder.botton_nav_menu_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.certified.jobfinder.BusinessActivity;
import com.certified.jobfinder.IndividualActivity;
import com.certified.jobfinder.R;
import com.certified.jobfinder.SplashActivity;
import com.certified.jobfinder.adapters.JobSeekersAdapter;
import com.certified.jobfinder.adapters.JobsRecyclerAdapter;
import com.certified.jobfinder.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        jobsRecyclerView = view.findViewById(R.id.recylcler_view_jobs_offers);
        queryDatabase();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        jobsRecyclerView.setLayoutManager(mLinearLayoutManager);

        mBottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        mNavController = Navigation.findNavController(getActivity(), R.id.individual_host_fragment);

        newOffer = view.findViewById(R.id.fab_new_offer);
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
                        mJobsRecyclerAdapter = new JobsRecyclerAdapter(getContext());
                        jobsRecyclerView.setAdapter(mJobsRecyclerAdapter);
                        newOffer.setVisibility(View.GONE);
                    } else if (level.equals(getString(R.string.business))) {
                        Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                        mJobSeekersAdapter = new JobSeekersAdapter();
//                        jobsRecyclerView.setAdapter(mJobSeekersAdapter);
                        newOffer.setOnClickListener(view1 -> {
                            mNavController.navigate(R.id.action_homeFragment_to_newOfferFragment);
//                            BusinessActivity.mBottomNavigationView.setVisibility(View.GONE);
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}