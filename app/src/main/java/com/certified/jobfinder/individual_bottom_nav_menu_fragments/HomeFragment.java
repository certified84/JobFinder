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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.certified.jobfinder.R;
import com.certified.jobfinder.adapters.JobsRecyclerAdapter;
import com.certified.jobfinder.model.Job;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "HomeFragment";

    //    Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference jobRef = db.collection("jobs");

    private JobsRecyclerAdapter mJobsRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView jobsRecyclerView;
    private FloatingActionButton newOffer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
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
        jobsRecyclerView = view.findViewById(R.id.recylcler_view_home);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        jobDetail = view.findViewById(R.id.job_card_view);

//        NavController navController = Navigation.findNavController(getActivity(), R.id.individual_host_fragment);

        newOffer = view.findViewById(R.id.fab_new_offer);
        newOffer.setVisibility(View.GONE);

        setUpJobsRecyclerView();
    }

    private void setUpJobsRecyclerView() {
        Log.d(TAG, "setUpJobsRecyclerView: called");
        Query query = jobRef.orderBy("time_created");
        FirestoreRecyclerOptions<Job> options = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        mJobsRecyclerAdapter = new JobsRecyclerAdapter(options);
        jobsRecyclerView.setAdapter(mJobsRecyclerAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        jobsRecyclerView.setLayoutManager(mLinearLayoutManager);
//        jobDetail.setOnClickListener(view -> {
//            NavController navController = Navigation.findNavController(getActivity(), R.id.individual_host_fragment);
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mJobsRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mJobsRecyclerAdapter.stopListening();
    }

    @Override
    public void onRefresh() {
//        mJobsRecyclerAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}