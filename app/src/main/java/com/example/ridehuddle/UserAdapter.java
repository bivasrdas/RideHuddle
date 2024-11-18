package com.example.ridehuddle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridehuddle.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;

    public UserAdapter(Context context, List<User> userList)
    {
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getName());

        if (user.isSelected()) {
            holder.addIcon.setVisibility(View.GONE);
            holder.checkIcon.setVisibility(View.VISIBLE);
        } else {
            holder.addIcon.setVisibility(View.VISIBLE);
            holder.checkIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            user.setSelected(!user.isSelected());
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView addIcon, checkIcon, userIcon;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            addIcon = itemView.findViewById(R.id.addIcon);
            checkIcon = itemView.findViewById(R.id.checkIcon);
            userIcon = itemView.findViewById(R.id.userIcon);
        }
    }
}
