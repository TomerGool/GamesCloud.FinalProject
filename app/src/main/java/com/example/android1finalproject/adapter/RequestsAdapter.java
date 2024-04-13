package com.example.android1finalproject.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android1finalproject.R;
import com.example.android1finalproject.models.Request;
import com.example.android1finalproject.ui.Requests;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {
    private ArrayList<Request> requestArr;
    private RequestsAdapter.OnUserClickListener clickListener;
    public interface OnUserClickListener {
        void onUserClick(int position);

    }
    public void setClickListener(RequestsAdapter.OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public RequestsAdapter(ArrayList<Request> requestArr) {
        this.requestArr = requestArr;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_request, parent, false);
        RequestsAdapter.RequestViewHolder viewHolder = new RequestsAdapter.RequestViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Request currentRequest = requestArr.get(position);

        String userName = currentRequest.getName();
        String status = currentRequest.getStatus();

        holder.name.setText(userName);
        holder.status.setText(status);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event (open user profile fragment)
                if(clickListener!=null )
                {
                    clickListener.onUserClick(position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return requestArr.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView status;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username_request);
            status = itemView.findViewById(R.id.status);
        }
    }
}
