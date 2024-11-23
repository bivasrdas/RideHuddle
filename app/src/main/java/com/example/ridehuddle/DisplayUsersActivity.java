package com.example.ridehuddle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridehuddle.models.Group;
import com.example.ridehuddle.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DisplayUsersActivity extends AppCompatActivity {
    private static final String TAG = "DisplayUsersActivity";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;

    private Button buttonAddUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_users);
            Group group = (Group) getIntent().getSerializableExtra("group");
            recyclerView = findViewById(R.id.recyclerView);
            buttonAddUser = findViewById(R.id.buttonAddUser);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            userList = new ArrayList<>();
            db.collection("user").get().addOnSuccessListener(
                    queryDocumentSnapshots -> {
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            User user = queryDocumentSnapshots.getDocuments().get(i).toObject(User.class);
                            userList.add(user);
                        }
                        assert group != null;
                        for(User user : userList)
                        {
                            user.setSelected(group.checkUserPresentInGroup(user.getUserId()));
                        }
                        userAdapter = new UserAdapter(this, userList);
                        recyclerView.setAdapter(userAdapter);
                    }
            );
            userAdapter = new UserAdapter(this, userList);
            recyclerView.setAdapter(userAdapter);
            buttonAddUser.setOnClickListener(
                    v -> {
                        if(group !=null)
                        {
                            group.setUserIds(new ArrayList<>());
                            for(User user : userList)
                            {
                                if(user.isSelected())
                                {
                                    group.addUserToGroup(user.getUserId());
                                }
                            }
                            group.updateUserFromGroup();
                        }
                        userList.add(new User("5","New User", false));
                        Intent intent = new Intent(DisplayUsersActivity.this, DisplayGroupDetailsActivity.class);
                        intent.putExtra("group",group);
                        startActivity(intent);
                        this.finish();
                    }
            );
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while creating display users activity",e);
        }
    }
}