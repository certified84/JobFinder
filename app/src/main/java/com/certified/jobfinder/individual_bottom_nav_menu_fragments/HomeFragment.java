package com.certified.jobfinder.individual_bottom_nav_menu_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.google.android.material.tabs.TabLayoutMediator;
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
    private ViewPagerAdapter mViewPagerAdapter;
    private GridLayoutManager mGridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;
    private RecyclerView mJobsRecyclerView;
    private CardView jobDetail;

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
        mViewPager2 = view.findViewById(R.id.view_pager);
        mTabLayout = view.findViewById(R.id.tabLayout);

        jobDetail = view.findViewById(R.id.job_card_view);
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
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mJobsRecyclerView.setLayoutManager(mLinearLayoutManager);
    }

    private void setUpSavedJobsRecyclerView() {
        Log.d(TAG, "setUpSavedJobsRecyclerView: called");
        Query query = myJobsRef.orderBy("time_created");
        FirestoreRecyclerOptions<SavedJob> options = new FirestoreRecyclerOptions.Builder<SavedJob>()
                .setQuery(query, SavedJob.class)
                .build();

        mSavedJobsAdapter = new SavedJobsAdapter(options);
        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager2.setAdapter(mSavedJobsAdapter);

        mViewPager2.setClipToPadding(false);
        mViewPager2.setClipChildren(false);
        mViewPager2.setOffscreenPageLimit(3);
        mViewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.05f + r + 0.05f);
        });

        mViewPager2.setPageTransformer(compositePageTransformer);
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