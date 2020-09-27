package com.certified.jobfinder.model;

import android.net.Uri;

public class Job {

    private String businessName;
    private String jobTitle;
    private String location;
    private Uri profileImageUrl;
    private String requirements;
    private String description;
    private String salary;

    public Job(String businessName, String jobTitle, String location, Uri profileImageUrl, String requirements, String description, String salary) {
        this.businessName = businessName;
        this.jobTitle = jobTitle;
        this.location = location;
        this.profileImageUrl = profileImageUrl;
        this.requirements = requirements;
        this.description = description;
        this.salary = salary;
    }

    public Job() {

    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Uri getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(Uri profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
