package com.example.ridehuddle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.ridehuddle.models.Group;
import com.example.ridehuddle.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final String TAG = "DisplayGroupsActivity";

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_groups);
            FloatingActionButton floatingButtonAddGroup = findViewById(R.id.floatingButtonAddGroup);
            recyclerView = findViewById(R.id.recyclerView);
            groupList = new ArrayList<>();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            displayUserGroups();
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            floatingButtonAddGroup.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(DisplayGroupsActivity.this, NewGroupActivity.class);
                        startActivity(intent);
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating display groups activity", e);
        }
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
            deletedGroup.deleteGroup();
            Toast.makeText(DisplayGroupsActivity.this, "Deleted " + deletedGroup.getGroupName(), Toast.LENGTH_SHORT).show();
            groupAdapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, "Deleted " + deletedGroup.getGroupName(), Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> {
                        deletedGroup.createGroup();
                        Toast.makeText(DisplayGroupsActivity.this, "Restored " + deletedGroup.getGroupName(), Toast.LENGTH_SHORT).show();
                        groupList.add(position, deletedGroup);
                        groupAdapter.notifyItemInserted(position);
                    }).show();
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(Color.RED)
                    .addActionIcon(R.drawable.baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onItemClick(int position) {
        try {
            Intent userActivity = new Intent(DisplayGroupsActivity.this, DisplayGroupDetailsActivity.class);
            userActivity.putExtra("group", groupList.get(position));
            startActivity(userActivity);
        } catch (Exception e) {
            Log.e(TAG, "Exception while selecting group", e);
        }
    }

    @Override
    public void onLongItemClick(int position) {
        //To Edit Group Details
//        groupList.remove(position);
//        groupAdapter.notifyItemRemoved(position);
//        Intent useractivity = new Intent(DisplayGroupsActivity.this, DisplayUsersActivity.class);
//        startActivity(useractivity);
    }
    public void displayUserGroups()
    {
        try {
            firestore.collection("group")
                    .whereArrayContains("userIds", MyApp.getInstance().getUserId())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<DocumentSnapshot> groups= queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot group : groups) {
                            groupList.add(group.toObject(Group.class));
                            groupAdapter = new GroupAdapter(this, groupList, this);
                            recyclerView.setAdapter(groupAdapter);
                            Log.d(TAG, "displayUserGroups: "+group.getData());
                        }
                    }).addOnFailureListener(
                            e -> Log.e(TAG,"Error while getting user groups",e));
        }
        catch (Exception e)
            {
            Log.e(TAG,"Exception while getting user groups",e);
        }

    }
}