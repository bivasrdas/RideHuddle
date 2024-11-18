package com.example.ridehuddle.models;

import java.util.HashSet;
import java.util.Set;

public class Group {
    private String id;
    private String name;
    private Set<User> users;

    private boolean isSelected;
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }



    // Constructor
    public Group(String groupId, String groupName) {
        this.id = groupId;
        this.name = groupName;
        this.users = new HashSet<>();
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

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        if (user != null && !users.contains(user)) {
            users.add(user);
        }
    }

    public void removeUser(User user) {
        if (user != null && users.contains(user)) {
            users.remove(user);
        }
    }

    @Override
    public String toString() {
        return "Group{" + "groupId='" + id + "', groupName='" + name + "'}";
    }
}

//public class Group {
//    private String name;
//    private boolean isSelected;
//
//    public Group(String name, boolean isSelected) {
//        this.name = name;
//        this.isSelected = isSelected;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public boolean isSelected() {
//        return isSelected;
//    }
//
//    public void setSelected(boolean selected) {
//        isSelected = selected;
//    }
//}
