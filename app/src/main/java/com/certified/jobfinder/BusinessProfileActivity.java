package com.certified.jobfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.util.IntentExtra;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusinessProfileActivity extends AppCompatActivity {

    private static final String TAG = "BusinessProfileActivity";

    //    Widgets
//    private CircleImageView ivBusinessProfileImage;
//    private ImageView ivProfileImageChange;
//    private TextView tvBusinessEmail;
//    private TextView tvBusinessName;
//    private TextView tvBusinessPhone;
//    private TextView tvBusinessLocation;
//    private TextView tvHintName;
//    private Button btnUploadResume;
//    private Group phoneGroup;
//    private Group emailGroup;
//    private Group locationGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

//        ivBusinessProfileImage = findViewById(R.id.profile_image);
//        ivProfileImageChange = findViewById(R.id.profile_image_change);
//        tvBusinessEmail = findViewById(R.id.tv_email);
//        tvBusinessName = findViewById(R.id.tv_name);
//        tvBusinessPhone = findViewById(R.id.tv_phone);
//        tvBusinessLocation = findViewById(R.id.tv_location);
//        tvHintName = findViewById(R.id.tv_hint_name);
//        btnUploadResume = findViewById(R.id.btn_upload_resume);
//        phoneGroup = findViewById(R.id.group_user_phone);
//        emailGroup = findViewById(R.id.group_user_email);
//        locationGroup = findViewById(R.id.group_user_location);
//
//        tvHintName.setText(R.string.business_name);
//        btnUploadResume.setVisibility(View.GONE);
//        ivProfileImageChange.setVisibility(View.GONE);
//        phoneGroup.setOnClickListener(this::onClick);
//        emailGroup.setOnClickListener(this::onClick);
//        locationGroup.setOnClickListener(this::onClick);
//
//        loadBusinessDetails();
    }
//
//    private void loadBusinessDetails() {
//        Intent intent = getIntent();
//        String businessProfileImage = intent.getStringExtra(IntentExtra.BUSINESS_PROFILE_IMAGE);
//        String businessName = intent.getStringExtra(IntentExtra.BUSINESS_NAME);
//        String businessEmail = intent.getStringExtra(IntentExtra.BUSINESS_EMAIL);
//        String businessPhone = intent.getStringExtra(IntentExtra.BUSINESS_PHONE);
//        String businessLocation = intent.getStringExtra(IntentExtra.BUSINESS_LOCATION);
//
//        Glide.with(this)
//                .load(businessProfileImage)
//                .into(ivBusinessProfileImage);
//
//        tvBusinessName.setText(businessName);
//        tvBusinessEmail.setText(businessEmail);
//        tvBusinessPhone.setText(businessPhone);
//        tvBusinessLocation.setText(businessLocation);
//    }
//
//    public void onClick(View view) {
//        int id = view.getId();
//        switch (id) {
//            case R.id.group_user_phone:
//                Log.d(TAG, "onClick: Dialing business phone");
//                dialBusiness();
//                break;
//
//            case R.id.group_user_location:
//                Log.d(TAG, "onClick: Opening map");
//                showBusinessLocation();
//                break;
//
//            case R.id.group_user_email:
//                Log.d(TAG, "onClick: Opening Mail app");
//                sendBusinessMail();
//                break;
//        }
//    }
//
//    private void sendBusinessMail() {
//        Uri uri = Uri.parse(tvBusinessEmail.getText().toString().trim());
//        Intent mailIntent = new Intent(Intent.ACTION_SEND);
//        mailIntent.setDataAndType(uri, "message/rfc2822");
//        startActivity(mailIntent);
//    }
//
//    private void showBusinessLocation() {
//        Uri uri = Uri.parse("geo:0,0?q=" + tvBusinessLocation.getText().toString().trim());
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(mapIntent);
//    }
//
//    private void dialBusiness() {
//        Uri uri = Uri.parse("tel:" + tvBusinessPhone.getText().toString().trim());
//        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, uri);
//        startActivity(phoneIntent);
//    }
}