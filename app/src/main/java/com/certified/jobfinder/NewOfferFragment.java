package com.certified.jobfinder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.certified.jobfinder.model.Job;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.text.TextUtils.isEmpty;


public class NewOfferFragment extends Fragment {

    public NewOfferFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_offer, container, false);
    }

    private NavController mNavController;
    private EditText etJobTitle, etRequirements, etLocation, etSalary, etDescription;
    private ProgressBar progressBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(getActivity(), R.id.business_host_fragment);
        etDescription = view.findViewById(R.id.et_description);
        etSalary = view.findViewById(R.id.et_salary);
        etLocation = view.findViewById(R.id.et_location);
        etRequirements = view.findViewById(R.id.et_requirements);
        etJobTitle = view.findViewById(R.id.et_job_title);
        progressBar = view.findViewById(R.id.progressBar);

        Button upload = view.findViewById(R.id.btn_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isEmpty(etJobTitle.getText().toString())
                        && !isEmpty(etDescription.getText().toString())
                        && !isEmpty(etLocation.getText().toString())
                        && !isEmpty(etRequirements.getText().toString())
                        && !isEmpty(etSalary.getText().toString())) {
                    uploadNewOffer();
                } else {
                    Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadNewOffer() {
        progressBar.setVisibility(View.VISIBLE);

        Job job = new Job();
        job.setBusinessName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        job.setProfileImageUrl(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        job.setJobTitle(etJobTitle.getText().toString());
        job.setLocation(etLocation.getText().toString());
        job.setRequirements(etRequirements.getText().toString());
        job.setDescription(etDescription.getText().toString());
        job.setSalary(etSalary.getText().toString());

        FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbnode_jobs))
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .setValue(job).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    mNavController.navigate(R.id.action_newOfferFragment_to_homeFragment);
                } else {
                    Toast.makeText(getContext(), "Unable to upload: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Unable to upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}