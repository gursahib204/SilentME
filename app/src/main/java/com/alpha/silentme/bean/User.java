package com.alpha.silentme.bean;

public class User {
    public String email;
    public String name;
    public String college;
    public Long id;
    private String profilePictureUrl;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public User() {
        // Default constructor required for Firebase
    }

    public User(String email, String name, String college, String profilePictureUrl,Long id) {
        this.email = email;
        this.name = name;
        this.college = college;
        this.id = id;
        this.profilePictureUrl = profilePictureUrl;
    }


}
