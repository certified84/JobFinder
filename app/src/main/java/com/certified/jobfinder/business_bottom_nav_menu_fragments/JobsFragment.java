package com.certified.jobfinder.business_bottom_nav_menu_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.certified.jobfinder.R;

public class JobsFragment extends Fragment {

    private static final String TAG = "JobsFragment";

    public JobsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_jobs, container, false);
        return view;
    }
}