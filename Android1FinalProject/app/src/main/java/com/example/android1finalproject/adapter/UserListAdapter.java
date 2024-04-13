package com.example.android1finalproject.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android1finalproject.R;
import com.example.android1finalproject.models.Likes;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(int position);

    }
    public interface OnUserLongClickListener
    {
        void onUserLongClicked(int position);
    }

    public void setClickListener(OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private OnUserClickListener clickListener;
    private OnUserLongClickListener longClickListener;
    public void setLongClickListener(OnUserLongClickListener longClickListener)
    {
        this.longClickListener = longClickListener;
    }


    private ArrayList<Likes> likedUsers;


    public UserListAdapter(ArrayList<Likes> likedUsers) {
        this.likedUsers = likedUsers;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_chat_for_game, parent, false);
        UserListViewHolder viewHolder = new UserListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String userName = likedUsers.get(position).getUsername();
        holder.userNameTextView.setText(userName);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(clickListener!=null)
                {
                    clickListener.onUserClick(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                longClickListener.onUserLongClicked(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return likedUsers.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name);
        }
    }
}
