package com.certified.jobfinder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.certified.jobfinder.util.IntentExtra;

public class JobDescriptionFragment extends Fragment {

    private TextView tvQualifications;
    private TextView tvAboutJob;

    public JobDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_description, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAboutJob = view.findViewById(R.id.tv_about_job);
        tvQualifications = view.findViewById(R.id.tv_qualifications);

    }

    @Override
    public void onResume() {
        super.onResume();

        loadData();
    }


    private void loadData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String description = bundle.getString(IntentExtra.JOB_DESCRIPTION);
            tvQualifications.setText(description);
        }
    }
}