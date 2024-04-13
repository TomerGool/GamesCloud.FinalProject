package com.example.android1finalproject.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android1finalproject.MainActivity;
import com.example.android1finalproject.R;
import com.example.android1finalproject.models.Request;
import com.example.android1finalproject.models.SearchUsersModel;
import com.example.android1finalproject.ui.SearchUsers;

import java.util.ArrayList;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.SearchUsersViewHolder>{
    private ArrayList<SearchUsersModel> userList;
    private String category_rb;

    private SearchUsersAdapter.OnUserClickListener clickListener;
    private SearchUsersAdapter.OnUserLongClickListener longClickListener;



    public void setCategoryRb(String newCategoryRb) {
        category_rb = newCategoryRb;
        notifyDataSetChanged();
    }
    public String getCategoryRb() {
        return category_rb;
    }

    public void setFilteredList(ArrayList<SearchUsersModel> list) {
        this.userList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_search_users, parent, false);
        SearchUsersAdapter.SearchUsersViewHolder viewHolder = new SearchUsersAdapter.SearchUsersViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull SearchUsersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SearchUsersModel currentUser = userList.get(position);

        String userName = currentUser.getForUser().getUsername();
        String category = currentUser.getForGame().getPlatforms().toString();


        if (category_rb != null) {
            switch (category_rb) {
                case "username":
                    category = currentUser.getForUser().getUsername();
                    holder.username.setText(userName);
                    holder.category.setText(category);
                    break;
                case "email":
                    category = currentUser.getForUser().getEmail();
                    holder.username.setText(userName);
                    holder.category.setText(category);
                    break;
                case "phone":
                    category = currentUser.getForUser().getPhone();
                    holder.username.setText(userName);
                    holder.category.setText(category);
                    break;
                case "favorite games":
                    category = currentUser.getForGame().getFavoriteGames().toString();
                    holder.username.setText(userName);
                    holder.category.setText(category);
                    break;
                case "genres":
                    category = currentUser.getForGame().getGenres().toString();
                    holder.username.setText(userName);
                    holder.category.setText(category);
                    break;
                case "platforms":
                    category = currentUser.getForGame().getPlatforms().toString();
                    holder.username.setText(userName);
                    holder.category.setText(category);
                    break;


            }
        }
        holder.username.setText(userName);
        holder.category.setText(category);

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
            public boolean onLongClick(View v) {
                longClickListener.onUserLongClicked(position);
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }



    public interface OnUserClickListener {
        void onUserClick(int position);

    }
    public interface OnUserLongClickListener {
        void onUserLongClicked(int position);
    }

    public void setClickListener(SearchUsersAdapter.OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }
    public void setLongClickListener(SearchUsersAdapter.OnUserLongClickListener longClickListener)
    {
        this.longClickListener = longClickListener;
    }

    public SearchUsersAdapter(ArrayList<SearchUsersModel> userList) {
        this.userList = userList;

    }

    public static class SearchUsersViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        TextView category;
        public SearchUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_search);
            category = itemView.findViewById(R.id.category_search);
        }
    }
}
