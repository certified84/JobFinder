package com.certified.jobfinder.individual_bottom_nav_menu_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.certified.jobfinder.R;
import com.certified.jobfinder.adapters.JobsRecyclerAdapter;
import com.certified.jobfinder.adapters.SavedJobsAdapter;
import com.certified.jobfinder.adapters.ViewPagerAdapter;
import com.certified.jobfinder.model.Job;
import com.certified.jobfinder.model.SavedJob;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment{

    private static final String TAG = "HomeFragment";

    //    Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference jobRef = db.collection("jobs");
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference myJobsRef = db.collection("saved_jobs")
            .document(user.getUid()).collection("my_saved_jobs");

    private JobsRecyclerAdapter mJobsRecyclerAdapter;
    private SavedJobsAdapter mSavedJobsAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mJobsRecyclerView, mSavedJobsRecyclerView;
    private CardView jobDetail;
    private TextView tvDisplayName;

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
        mJobsRecyclerView = view.findViewById(R.id.recycler_view_home);
        mSavedJobsRecyclerView = view.findViewById(R.id.recycler_view_saved_jobs);
        jobDetail = view.findViewById(R.id.job_card_view);
        tvDisplayName = view.findViewById(R.id.tv_display_name);

        tvDisplayName.setText("Hello, " + user.getDisplayName());

        setUpJobsRecyclerView();
        setUpSavedJobsRecyclerView();
    }

    private void setUpJobsRecyclerView() {
        Log.d(TAG, "setUpJobsRecyclerView: called");
        Query query = jobRef.orderBy("time_created");
        FirestoreRecyclerOptions<Job> options = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        mJobsRecyclerAdapter = new JobsRecyclerAdapter(options);
        mJobsRecyclerView.setAdapter(mJobsRecyclerAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mJobsRecyclerView.setLayoutManager(mLinearLayoutManager);

        mJobsRecyclerView.setClipToPadding(false);
        mJobsRecyclerView.setClipChildren(false);
    }

    private void setUpSavedJobsRecyclerView() {
        Log.d(TAG, "setUpSavedJobsRecyclerView: called");
        Query query = myJobsRef.orderBy("time_created");
        FirestoreRecyclerOptions<SavedJob> options = new FirestoreRecyclerOptions.Builder<SavedJob>()
                .setQuery(query, SavedJob.class)
                .build();

        mSavedJobsAdapter = new SavedJobsAdapter(options);
        mSavedJobsRecyclerView.setAdapter(mSavedJobsAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mSavedJobsRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSavedJobsRecyclerView.setClipToPadding(false);
        mSavedJobsRecyclerView.setClipChildren(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mJobsRecyclerAdapter.startListening();
        mSavedJobsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mJobsRecyclerAdapter.stopListening();
        mSavedJobsAdapter.stopListening();
    }
}