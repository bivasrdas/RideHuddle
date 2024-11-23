package com.example.ridehuddle.models;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ridehuddle.MyApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Group implements Serializable {
    private String groupId;
    private String groupName;

    private String groupIconURL;

    private List<String> userIds;


    // Constructor
    public Group()
    {

    }
    public Group(String groupName,String iconURL) {
        this.groupId = UUID.randomUUID().toString();
        this.groupName = groupName;
        this.groupIconURL = iconURL;
    }
    public Group(String groupName, String groupId, String groupIconURL, List<String> users) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupIconURL = groupIconURL;
        this.userIds = users;
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupIconURL() {
        return groupIconURL;
    }

    public void setGroupIconURL(String groupIconURL) {
        this.groupIconURL = groupIconURL;
    }
    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public void createGroup()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("group").document(this.groupId).set(this).addOnSuccessListener(aVoid -> {
            Log.d("Group", "DocumentSnapshot successfully written!");
        }).addOnFailureListener(e -> {
            Log.e("Group", "Error writing document", e);
        });
    }
    public void deleteGroup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("group").document(this.getGroupId()).delete()
                .addOnSuccessListener(unused -> Log.d("Group", "Group deleted from DB"))
                .addOnFailureListener(e -> Log.e("Group", "Error while deleting group", e));
    }
    public boolean checkUserPresentInGroup(String userId)
    {
        return this.userIds.contains(userId);
    }

    public void addUserToGroup(String userId) {
        if(!(this.userIds.contains(userId)))
        {
          this.userIds.add(userId);
        }
    }
    public void updateUserFromGroup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("group").document(this.groupId).update("userIds", this.userIds).addOnSuccessListener(
                aVoid -> Log.d("Group", "DocumentSnapshot successfully written!")
        ).addOnFailureListener(
                e -> Log.e("Group", "Error writing document", e)
        );
    }
    public void removeUserfromGroup()
    {
        this.userIds.remove(MyApp.getInstance().getUserId());
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupIconURL='" + groupIconURL + '\'' +
                ", userIds=" + userIds +
                '}';
    }
}