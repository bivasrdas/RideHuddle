package com.example.ridehuddle;

import android.app.Application;

public class MyApp extends Application {
    private static MyApp instance;
    private String userId;

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
}

