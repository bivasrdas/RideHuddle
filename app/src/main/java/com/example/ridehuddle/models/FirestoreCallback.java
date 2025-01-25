package com.example.ridehuddle.models;

public interface FirestoreCallback<T> {
    void onCallback(T result);
}
