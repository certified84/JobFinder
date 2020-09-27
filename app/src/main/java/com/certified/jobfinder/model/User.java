package com.certified.jobfinder.model;

public class User {

    private String name;
    private String email;
    private String phone;
    private String level;
    private String user_id;

    public User(String name, String email, String phone, String level, String user_id) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.level = level;
        this.user_id = user_id;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
