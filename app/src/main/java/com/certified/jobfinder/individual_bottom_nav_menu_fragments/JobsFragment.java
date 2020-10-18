package com.certified.jobfinder.individual_bottom_nav_menu_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.certified.jobfinder.R;
import com.certified.jobfinder.adapters.SavedJobsAdapter;
import com.certified.jobfinder.model.Job;
import com.certified.jobfinder.model.SavedJob;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class JobsFragment extends Fragment {

    private static final String TAG = "JobsFragment";

//    Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference myJobsRef = db.collection("saved_jobs")
            .document(user.getUid()).collection("my_saved_jobs");

    private SavedJobsAdapter mSavedJobsAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView savedJobsRecyclerView;

    public JobsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_jobs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        savedJobsRecyclerView = view.findViewById(R.id.recylcler_view_jobs);
        NavController navController = Navigation.findNavController(getActivity(), R.id.individual_host_fragment);

        setUpSavedJobsRecyclerView();
    }

    private void setUpSavedJobsRecyclerView() {
        Log.d(TAG, "setUpSavedJobsRecyclerView: called");
        Query query = myJobsRef.orderBy("time_created");
        FirestoreRecyclerOptions<SavedJob> options = new FirestoreRecyclerOptions.Builder<SavedJob>()
                .setQuery(query, SavedJob.class)
                .build();

        mSavedJobsAdapter = new SavedJobsAdapter(options);
        savedJobsRecyclerView.setAdapter(mSavedJobsAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        savedJobsRecyclerView.setLayoutManager(mLinearLayoutManager);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mSavedJobsAdapter.deleteJob(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(savedJobsRecyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        mSavedJobsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSavedJobsAdapter.stopListening();
    }
}