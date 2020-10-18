package com.certified.jobfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.certified.jobfinder.model.Job;
import com.certified.jobfinder.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.text.TextUtils.isEmpty;


public class NewOfferFragment extends Fragment {

    private static final String TAG = "NewOfferFragment";

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
                Log.d(TAG, "onClick: Phone: " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "\n" +
                        "Name: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "\n" +
                        "Email: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                if (!isEmpty(etJobTitle.getText().toString().trim())
                        && !isEmpty(etDescription.getText().toString().trim())
                        && !isEmpty(etLocation.getText().toString().trim())
                        && !isEmpty(etRequirements.getText().toString().trim())
                        && !isEmpty(etSalary.getText().toString().trim())) {
                    uploadNewOffer();
                } else {
                    Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadNewOffer() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference myOfferRef = db.collection(getString(R.string.dbnode_jobs))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("my_offers").document();

        DocumentReference jobsRef = db.collection(getString(R.string.dbnode_jobs))
                .document();

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

                    String jobTitle = etJobTitle.getText().toString();
                    String businessName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String businessEmail = user.getEmail();
                    String businessPhone = user.getPhone();
                    String businessLocation = user.getLocation();
                    String description = etDescription.getText().toString();
                    String location = etLocation.getText().toString();
                    String requirement = etRequirements.getText().toString();
                    String salary = etSalary.getText().toString();
                    String id = myOfferRef.getId();
                    String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Uri profileImageUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

                    Job job = new Job(id, businessName, businessEmail, businessPhone, businessLocation, jobTitle,
                            description, location, profileImageUrl, requirement, salary, null, creatorId);

                    jobsRef.set(job)
                            .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Saved job to all jobs collection");
                        } else
                            Log.d(TAG, "onComplete: Could not save job to all jobs collection");
                    });

                    myOfferRef.set(job)
                            .addOnCompleteListener(task -> {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                                    mNavController.navigate(R.id.action_newOfferFragment_to_homeFragment);
                                } else {
                                    Toast.makeText(getContext(), "Unable to upload: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}