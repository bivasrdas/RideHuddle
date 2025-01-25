package com.example.ridehuddle;

import android.app.Application;

public class MyApp extends Application {
    private static MyApp instance;
    private String userId;

    private String groupId;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApp getInstance() {
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

