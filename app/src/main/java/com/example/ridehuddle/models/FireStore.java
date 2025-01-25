package com.example.ridehuddle.models;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class FireStore {
    private final FirebaseFirestore db;

    public FireStore() {
        db = FirebaseFirestore.getInstance();
    }

    public <T> void getDocument(String collection, String documentId, Class<T> clazz, FirestoreCallback<T> callback) {
        db.collection(collection).document(documentId).get().addOnSuccessListener(
                documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onCallback(documentSnapshot.toObject(clazz));
                    } else {
                        callback.onCallback(null); // Pass null if the document doesn't exist
                    }
                }
        ).addOnFailureListener(
                e -> {
                    Log.e("FireStore", "Error getting document.", e);
                    callback.onCallback(null); // Pass null in case of error
                }
        );
    }
    public <T> void addDocument(String collection, String documentId, T data, FirestoreCallback<Boolean> callback) {
        db.collection(collection).document(documentId).set(data)
                .addOnSuccessListener(aVoid -> {
                    callback.onCallback(true); // Return true on success
                })
                .addOnFailureListener(e -> {
                    Log.e("FireStore", "Error adding document.", e);
                    callback.onCallback(false); // Return false on failure
                });
    }
}
