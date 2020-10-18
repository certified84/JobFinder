package com.certified.jobfinder.model;

import android.net.Uri;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SavedJob {

    private String id;
    private String original_job_id;
    private String business_name;
    private String business_email;
    private String business_phone;
    private String business_location;
    private Uri business_profile_image_url;
    private String job_title;
    private String description;
    private String location;
    private String requirements;
    private String salary;
    private @ServerTimestamp Date time_created;
    private String creator_id;
    private String uid;

    public SavedJob(String id, String original_job_id, String business_name, String business_email,
                    String business_phone, String business_location, Uri business_profile_image_url,
                    String job_title, String description, String location, String requirements,
                    String salary, Date time_created, String creator_id, String uid) {

        this.id = id;
        this.original_job_id = original_job_id;
        this.business_name = business_name;
        this.business_email = business_email;
        this.business_phone = business_phone;
        this.business_location = business_location;
        this.business_profile_image_url = business_profile_image_url;
        this.job_title = job_title;
        this.description = description;
        this.location = location;
        this.requirements = requirements;
        this.salary = salary;
        this.time_created = time_created;
        this.creator_id = creator_id;
        this.uid = uid;
    }

    public SavedJob() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_job_id() {
        return original_job_id;
    }

    public void setOriginal_job_id(String original_job_id) {
        this.original_job_id = original_job_id;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_email() {
        return business_email;
    }

    public void setBusiness_email(String business_email) {
        this.business_email = business_email;
    }

    public String getBusiness_phone() {
        return business_phone;
    }

    public void setBusiness_phone(String business_phone) {
        this.business_phone = business_phone;
    }

    public String getBusiness_location() {
        return business_location;
    }

    public void setBusiness_location(String business_location) {
        this.business_location = business_location;
    }

    public Uri getBusiness_profile_image_url() {
        return business_profile_image_url;
    }

    public void setBusiness_profile_image_url(Uri business_profile_image_url) {
        this.business_profile_image_url = business_profile_image_url;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}