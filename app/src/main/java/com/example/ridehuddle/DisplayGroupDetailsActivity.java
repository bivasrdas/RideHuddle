package com.example.ridehuddle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridehuddle.models.Group;
import com.example.ridehuddle.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DisplayGroupDetailsActivity extends AppCompatActivity {
    private static final String TAG = "DisplayGroupDetailsActivity";
    private RecyclerView recyclerView;
    private UserDetailsAdapter userDetailsAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_group_details);
            Group group = (Group) getIntent().getSerializableExtra("group");
            FloatingActionButton floatingAddUserButton = findViewById(R.id.floatingAddUser);
            FloatingActionButton floatingMapButton = findViewById(R.id.floatingMapButton);
            TextView groupHeader = findViewById(R.id.groupNameHeader);
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            if(group != null)
            {
                groupHeader.setText(group.getGroupName());
                List<String> groupUserIds = group.getUserIds();
                List<User> userList = new ArrayList<>();
                for(String userId : groupUserIds)
                {
                    db.collection("user").document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                User user = documentSnapshot.toObject(User.class);
                                Log.d(TAG, "User: " + documentSnapshot.getData());
                                if(user != null) {
                                    userList.add(user);
                                }
                                userDetailsAdapter = new UserDetailsAdapter(this, userList);
                                recyclerView.setAdapter(userDetailsAdapter);
                            }).addOnFailureListener(
                                    e -> Log.e(TAG,"Exception while creating display group details activity",e)
                            );
                }
            }
            floatingAddUserButton.setOnClickListener(v -> {
                Intent userActivity = new Intent(DisplayGroupDetailsActivity.this, DisplayUsersActivity.class);
                userActivity.putExtra("group",group);
                startActivity(userActivity);
            });
            floatingMapButton.setOnClickListener(v -> {
                Intent mapActivity = new Intent(DisplayGroupDetailsActivity.this, DisplayMap.class);
                mapActivity.putExtra("group",group);
                startActivity(mapActivity);
            });
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while creating display group details activity",e);
        }
    }
}
