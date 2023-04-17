package com.example.cs5520_finalproject_group2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class EventViewHolder extends RecyclerView.ViewHolder {
    public TextView eventName, eventLocation, eventTime;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        eventName = itemView.findViewById(R.id.eventRowName);
        eventLocation = itemView.findViewById(R.id.eventRowLocation);
        eventTime = itemView.findViewById(R.id.eventRowTime);
    }
}

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private ArrayList<Event> events;

    public EventAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent,
                false);
        EventViewHolder viewHolder = new EventViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event currentEvent = events.get(position);
        holder.eventName.setText(currentEvent.getName());
        holder.eventTime.setText(currentEvent.getStartTime() + " - " + currentEvent.getEndTime());
        holder.eventLocation.setText(currentEvent.getLocation());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
