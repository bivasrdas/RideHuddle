package com.example.ridehuddle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ridehuddle.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewGroupActivity extends AppCompatActivity {
    Button createGroup;
    Button selectIcon;
    TextView groupNameTextView;
    private static final String TAG = "NewGroupActivity";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        createGroup = findViewById(R.id.create_group_button);
        selectIcon = findViewById(R.id.select_group_icon);
        groupNameTextView = findViewById(R.id.group_name);
        createGroup.setOnClickListener(v -> {
            String groupName=groupNameTextView.getText().toString();
            Group group = new Group(groupName,"");
            List<String> userIds = new ArrayList<>();
            userIds.add(MyApp.getInstance().getUserId());
            group.setUserIds(userIds);
            group.createGroup();
            Toast.makeText(this,"Group Created",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NewGroupActivity.this, DisplayGroupsActivity.class);
            startActivity(intent);
            this.finish();
        });
    }
}
