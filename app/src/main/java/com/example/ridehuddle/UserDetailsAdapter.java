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

public class UserDetailsAdapter extends RecyclerView.Adapter<UserDetailsAdapter.GroupDetailsViewHolder> {
    private List<User> userList;
    private Context context;

    public UserDetailsAdapter(Context context, List<User> userList)
    {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public GroupDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_no_action, parent, false);
        return new UserDetailsAdapter.GroupDetailsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailsAdapter.GroupDetailsViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getUserName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class GroupDetailsViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userIcon;

        public GroupDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userIcon = itemView.findViewById(R.id.userIcon);
        }
    }
}
