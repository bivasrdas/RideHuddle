package com.example.ridehuddle.models;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
public class User {
    private String userId;
    private String userName;

    private String userImageUrl;
    private GeoPoint locations;

    private String selectedGroupId;
    private boolean isSelected;

    // Constructor
    public User()
    {

    }

    public User(String id, String name, Boolean isSelected) {
        this.userId = id;
        this.userName = name;
        this.isSelected = isSelected;
    }
    public User(String userId, String userName, String userImageUrl, GeoPoint locations, String selectedGroupId) {
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.locations = locations;
        this.selectedGroupId = selectedGroupId;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public GeoPoint getLocations() {
        return locations;
    }

    public void setLocations(GeoPoint locations) {
        this.locations = locations;
    }

    public String getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(String selectedGroupId) {
        this.selectedGroupId = selectedGroupId;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userImageUrl='" + userImageUrl + '\'' +
                ", locations=" + locations +
                ", selectedGroupId='" + selectedGroupId + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
