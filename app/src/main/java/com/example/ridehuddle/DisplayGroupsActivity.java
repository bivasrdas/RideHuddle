package com.example.ridehuddle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ridehuddle.models.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class DisplayGroupsActivity extends AppCompatActivity implements RecyclerViewClickInterface {
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private FloatingActionButton floatingActionButton;
    private static final String TAG = "DisplayGroupsActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_groups);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupList = new ArrayList<>();
        groupList.add(new Group("1","Daniel Maas"));
        groupList.add(new Group("2","Lee"));
        groupList.add(new Group("3","Marty Reyes"));
        groupList.add(new Group("4","Sam"));
        groupList.add(new Group("1","Daniel Maas"));
        groupList.add(new Group("2","Lee"));
        groupList.add(new Group("3","Marty Reyes"));
        groupList.add(new Group("4","Sam"));

        groupAdapter = new GroupAdapter(this, groupList, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(groupAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayGroupsActivity.this, DisplayUsersActivity.class);
                startActivity(intent);
            }
        });
    }

    Group deletedGroup = null;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("ShowToast")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getLayoutPosition();
            deletedGroup = groupList.get(position);
            groupList.remove(position);
            groupAdapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, "Deleted " + deletedGroup.getName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            groupList.add(position, deletedGroup);
                            groupAdapter.notifyItemInserted(position);
                        }
                    }).show();
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor((int) Color.RED)
                    .addActionIcon(R.drawable.baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onItemClick(int position) {
        Intent useractivity = new Intent(DisplayGroupsActivity.this, DisplayGroupDetailsActivity.class);
        startActivity(useractivity);
    }

    @Override
    public void onLongItemClick(int position) {
//        groupList.remove(position);
//        groupAdapter.notifyItemRemoved(position);
        Intent useractivity = new Intent(DisplayGroupsActivity.this, DisplayUsersActivity.class);
        startActivity(useractivity);
    }
}