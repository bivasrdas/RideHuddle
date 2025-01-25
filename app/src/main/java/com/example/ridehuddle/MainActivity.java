package com.example.ridehuddle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.ridehuddle.models.FireStore;
import com.example.ridehuddle.models.FirestoreCallback;
import com.example.ridehuddle.models.Group;
import com.example.ridehuddle.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, FirebaseUIActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            MyApp.getInstance().setUserId(currentUser.getUid());
            FireStore fireStore = new FireStore();
            db.collection("user").document(currentUser.getUid()).get().addOnSuccessListener(
                    documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        MyApp.getInstance().setGroupId(user.getSelectedGroupId());
                        db.collection("group").document(user.getSelectedGroupId()).get().addOnSuccessListener(
                                documentSnapshot1 -> {
                                    Group group = documentSnapshot1.toObject(Group.class);
                                    assert group != null;
                                    Intent mainActivity = new Intent(MainActivity.this, DisplayMap.class);
                                    mainActivity.putExtra("group", group);
                                    startActivity(mainActivity);
                                    this.finish();
                                }
                        ).addOnFailureListener(
                                e -> Log.e(TAG, "Error getting documents.", e)
                        );
                    }
            ).addOnFailureListener(
                    e -> Log.e(TAG, "Error getting documents.", e)
            );
            Log.d(TAG, "onCreate: " + currentUser.getUid());
            setContentView(R.layout.activity_main);
            Intent mainActivity = new Intent(MainActivity.this, DisplayGroupsActivity.class);
            startActivity(mainActivity);
            this.finish();
        }
    }
}


