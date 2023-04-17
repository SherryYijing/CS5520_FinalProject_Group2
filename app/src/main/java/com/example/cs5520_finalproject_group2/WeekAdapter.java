package com.example.cs5520_finalproject_group2;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {

    private ArrayList<Week> items;
    private int index = -1;
    private RecyclerViewClickListener rListener;
    private View view;

    public WeekAdapter(ArrayList<Week> items, View view, RecyclerViewClickListener rListener) {
        this.items = items;
        this.view = view;
        this.rListener = rListener;
    }

    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_cell, parent, false);
        int height = parent.getHeight();
        int width = parent.getMeasuredWidth() / 5;
        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        WeekViewHolder viewHolder = new WeekViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Week current = items.get(position);
        holder.cellDayText.setText(current.getDay());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = position;
                rListener.recyclerViewListClicked(position);
                notifyDataSetChanged();
            }
        });

        if (index == position) {
            holder.cellDayText.setTextColor(R.color.purple_500);
        } else {
            holder.cellDayText.setTextColor(R.color.black);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class WeekViewHolder extends RecyclerView.ViewHolder{
        TextView cellDayText;

        public WeekViewHolder(@NonNull View itemView) {
            super(itemView);
            cellDayText = itemView.findViewById(R.id.cellDayText);
        }

    }


}
