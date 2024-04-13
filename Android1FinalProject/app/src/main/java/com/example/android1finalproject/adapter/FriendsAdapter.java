package com.example.android1finalproject.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android1finalproject.R;
import com.example.android1finalproject.models.Friend;
import com.example.android1finalproject.models.Request;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>{
    private ArrayList<Friend> friendArr;
    private FriendsAdapter.OnUserClickListener clickListener;
    private FriendsAdapter.OnUserLongClickListener longClickListener;

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_friend, parent, false);
        FriendsAdapter.FriendViewHolder viewHolder = new FriendsAdapter.FriendViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Friend currentFriend = friendArr.get(position);

        String userName = currentFriend.getName();
        String email = currentFriend.getEmail();

        holder.name.setText(userName);
        holder.email.setText(email);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(clickListener!=null )
                {
                    clickListener.onUserClick(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClickListener.OnUserLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendArr.size();
    }

    public interface OnUserClickListener {
        void onUserClick(int position);

    }
    public interface OnUserLongClickListener{
        void OnUserLongClick(int position);
    }
    public void setClickListener(FriendsAdapter.OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(OnUserLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public FriendsAdapter(ArrayList<Friend> friendArr) {
        this.friendArr = friendArr;
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView email;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username_friend);
            email = itemView.findViewById(R.id.email_friend);
        }
    }
}
