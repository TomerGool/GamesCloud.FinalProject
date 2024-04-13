package com.example.android1finalproject.adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.android1finalproject.R;
import com.example.android1finalproject.models.Games;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.GamesViewHolder>   {
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<Games> datasetGames;

    public MainScreenAdapter(ArrayList<Games> datasetGames) {
        this.datasetGames = datasetGames;
    }
    private OnItemClickListener clickListener;

    public void setOnClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row,parent,false);
        GamesViewHolder gamesViewHolder = new GamesViewHolder(view);
        return gamesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GamesViewHolder holder, @SuppressLint("RecyclerView") int position) {

        int desiredWidth = 200;
        int desiredHeight = 200;


        TextView textViewName = holder.textName;
        ImageView imageView = holder.imageView;

        textViewName.setText(datasetGames.get(position).getName());
        String imgUrl = datasetGames.get(position).getImage();
        Picasso.get().load(imgUrl).resize(desiredWidth, desiredHeight).centerCrop().into(imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datasetGames.size();
    }

    public static class GamesViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        ImageView imageView;



        public GamesViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}
