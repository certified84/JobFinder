package com.certified.jobfinder.model;

import android.net.Uri;

import androidx.annotation.NonNull;

public class User {

    private String name;
    private String email;
    private String phone;
    private String account_type;
    private String user_id;
    private String location;
    private Uri profile_image;

    public User(String name, String email, String phone, String account_type, String user_id, String location, Uri profile_image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.account_type = account_type;
        this.user_id = user_id;
        this.location = location;
        this.profile_image = profile_image;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Uri getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(Uri profile_image) {
        this.profile_image = profile_image;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
