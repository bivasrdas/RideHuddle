package com.example.ridehuddle;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridehuddle.models.User;

import java.util.ArrayList;
import java.util.List;

public class DisplayUsersActivity extends AppCompatActivity {
    private static final String TAG = "DisplayUsersActivity";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_users);

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            userList = new ArrayList<>();
            userList.add(new User("1","Daniel Maas", false));
            userList.add(new User("2","Lee", false));
            userList.add(new User("3","Marty Reyes", false));
            userList.add(new User("4","Sam", true));

            userAdapter = new UserAdapter(this, userList);
            recyclerView.setAdapter(userAdapter);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while creating display users activity",e);
        }
    }
}