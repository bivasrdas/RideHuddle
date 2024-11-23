package com.example.ridehuddle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridehuddle.models.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<Group> groupList;
    private Context context;
    private static final String TAG = "GroupAdapter";
    private static RecyclerViewClickInterface recyclerViewClickInterface;


    public GroupAdapter(Context context, List<Group> groupList, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.groupList = groupList;
        GroupAdapter.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.groupName.setText(group.getGroupName());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        ImageView groupIcon;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            groupIcon = itemView.findViewById(R.id.groupIcon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewClickInterface != null) {
                        recyclerViewClickInterface.onItemClick(getLayoutPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (recyclerViewClickInterface != null) {
                        recyclerViewClickInterface.onLongItemClick(getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}

