package com.example.cs5520_finalproject_group2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private IEventAdapterAction eListener;
    private ArrayList<Event> events;

    public EventAdapter(ArrayList<Event> events, Context context) {
        this.events = events;
        if(context instanceof IEventAdapterAction){
            this.eListener = (IEventAdapterAction) context;
        }
        else{
            throw new RuntimeException(context.toString() + " must implement IEventAdapterAction!");
        }
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
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
        Log.d("edit", "onBindViewHolder: " + currentEvent.toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("edit", "onBindViewHolder: " + currentEvent.toString());
                eListener.toEdit(currentEvent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface IEventAdapterAction {
        void toEdit(Event event);
    }
}
