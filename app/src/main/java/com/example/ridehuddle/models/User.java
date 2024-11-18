package com.example.ridehuddle.models;

import com.google.android.gms.maps.model.LatLng;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String id;
    private String name;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Constructor
    public User(String id, String name, Boolean isSelected) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + id + "', userName='" + name +
                "'}";
    }
}
