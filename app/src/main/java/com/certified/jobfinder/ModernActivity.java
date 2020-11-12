package com.certified.jobfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.certified.jobfinder.adapters.JobsRecyclerAdapter;
import com.certified.jobfinder.adapters.ViewPagerAdapter;
import com.certified.jobfinder.model.Job;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ModernActivity extends AppCompatActivity {

    private static final String TAG = "ModernActivity";
    private ViewPager2 mViewPager2;
    private ViewPagerAdapter mViewPagerAdapter;
    private JobsRecyclerAdapter mJobsRecyclerAdapter;
    private RecyclerView mJobsRecyclerView;
    private GridLayoutManager mGridLayoutManager;

    //    Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference jobRef = db.collection("jobs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modern_design_layout);

        mViewPager2 = findViewById(R.id.view_pager);
        mJobsRecyclerView = findViewById(R.id.recycler_view_home);

        setUpJobsRecyclerView();
    }

    private void setUpJobsRecyclerView() {
        Log.d(TAG, "setUpJobsRecyclerView: called");
        Query query = jobRef.orderBy("time_created");
        FirestoreRecyclerOptions<Job> options = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(query, Job.class)
                .build();

        mJobsRecyclerAdapter = new JobsRecyclerAdapter(options);
        mViewPagerAdapter = new ViewPagerAdapter();
        
        mViewPager2.setAdapter(mJobsRecyclerAdapter);
        mJobsRecyclerView.setAdapter(mJobsRecyclerAdapter);
        
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mJobsRecyclerView.setLayoutManager(mGridLayoutManager);
    }
}