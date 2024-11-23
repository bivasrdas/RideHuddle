package com.example.ridehuddle.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.Set;

public class User {
    private String userId;
    private String userName;

    private String userImageUrl;
    private GeoPoint locations;

    private Set<String> groupIds;
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
    public User(String userId, String userName, String userImageUrl, GeoPoint locations, Set<String> groupIds) {
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.locations = locations;
        this.groupIds = groupIds;
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

    public Set<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<String> groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + "', userName='" + userName +
                "'}";
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
