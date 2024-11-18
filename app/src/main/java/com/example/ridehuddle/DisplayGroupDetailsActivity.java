package com.example.ridehuddle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridehuddle.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DisplayGroupDetailsActivity extends AppCompatActivity {
    private static final String TAG = "DisplayGroupDetailsActivity";
    private RecyclerView recyclerView;
    private GroupDetailsAdapter groupDetailsAdapter;

    private FloatingActionButton floatingAddUserButton;
    private FloatingActionButton floatingMapButton;
    private List<User> userList;

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_group_details);
            floatingAddUserButton = findViewById(R.id.floatingAddUser);
            floatingMapButton = findViewById(R.id.floatingMapButton);
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            userList = new ArrayList<>();
            userList.add(new User("1","Daniel Maas", false));
            userList.add(new User("2","Lee", false));
            userList.add(new User("3","Marty Reyes", false));
            userList.add(new User("4","Sam", true));

            groupDetailsAdapter = new GroupDetailsAdapter(this,userList);
            recyclerView.setAdapter(groupDetailsAdapter);
            floatingAddUserButton.setOnClickListener(v -> {
                Intent useractivity = new Intent(DisplayGroupDetailsActivity.this, DisplayUsersActivity.class);
                startActivity(useractivity);
            });
            floatingMapButton.setOnClickListener(v -> {
                Intent mapactivity = new Intent(DisplayGroupDetailsActivity.this, DisplayMap.class);
                startActivity(mapactivity);
            });
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while creating display group details activity",e);
        }
    }
}
