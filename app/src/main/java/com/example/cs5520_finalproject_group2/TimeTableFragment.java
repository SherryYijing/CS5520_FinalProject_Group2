package com.example.cs5520_finalproject_group2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TimeTableFragment extends Fragment implements RecyclerViewClickListener {
    private Button newEventButton, showNavigationButton;
    private static final String ARG_DAY = "day";
    private RecyclerView weekRecyclerView, eventRecyclerView;
    private WeekAdapter weekAdapter;
    private ArrayList<Event> events = new ArrayList<>();
    private EventAdapter eventAdapter;
    private Event currentEvent;
    private String selectedDay;
    private ITimeTableActivity iListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    public TimeTableFragment() {
        // Required empty public constructor
    }

    public static TimeTableFragment newInstance() {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        loadEventFromDb("Mon");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);
        newEventButton = view.findViewById(R.id.newEventButton);
        showNavigationButton = view.findViewById(R.id.showNavigationButton);
        ArrayList<Week> weekdays = new ArrayList<>();
        weekdays.add(new Week("Mon"));
        weekdays.add(new Week("Tue"));
        weekdays.add(new Week("Wed"));
        weekdays.add(new Week("Thu"));
        weekdays.add(new Week("Fri"));
        weekRecyclerView = view.findViewById(R.id.weekRecyclerView);
        weekAdapter = new WeekAdapter(weekdays, view, this);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(
                this.getContext(), LinearLayoutManager.HORIZONTAL, false
        ));
        weekRecyclerView.setAdapter(weekAdapter);
        selectedDay = "Mon";

//        events.add(new Event("CS5200", "EastVillage", "Mon","12:00", "14:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","12:00", "14:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","15:00", "16:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","12:00", "14:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","12:00", "14:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","17:00", "18:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","12:00", "14:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","12:00", "14:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","12:00", "14:00"));
//        events.add(new Event("CS5200", "EastVillage", "Mon","12:00", "14:00"));

        Event.sort(events);
        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        eventAdapter = new EventAdapter(events, this.getContext());
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        eventRecyclerView.setAdapter(eventAdapter);
        loadEventFromDb(selectedDay);


        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iListener.toAddEvent(selectedDay);
            }
        });

        showNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iListener.toNavigation(selectedDay);
            }
        });

        return view;

    }

    private void loadEventFromDb(String day) {
        ArrayList<Event> eventList = new ArrayList<>();
        selectedDay = day;
        Log.d("get", "loadEventFromDb: ");
        db.collection("users")
                .document(currentUser.getEmail())
                .collection(selectedDay)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("get", "onFailure: ");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Event event = queryDocumentSnapshot.toObject(Event.class);
                                Log.d("get", "onComplete: " + event.toString());
                                eventList.add(event);
                            }
                            if (eventList.size() < 1) {
                                Toast.makeText(getContext(), "You have no event on this day"
                                        , Toast.LENGTH_LONG).show();
                            }
                            Event.sort(eventList);
                            updateEventRecyclerView(eventList);
                        }
                    }
                });
    }

    @Override
    public void recyclerViewListClicked(int position) {
        switch (position) {
            case 0:
                this.selectedDay = "Mon";
                break;
            case 1:
                this.selectedDay = "Tue";
                break;
            case 2:
                this.selectedDay = "Wed";
                break;
            case 3:
                this.selectedDay = "Thu";
                break;
            case 4:
                this.selectedDay = "Fri";
                break;
        }
        loadEventFromDb(this.selectedDay);
        Log.d("click", "recyclerViewListClicked: " + this.selectedDay);
    }

    public void updateEventRecyclerView(ArrayList<Event> events){
        this.events = events;
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TimeTableFragment.ITimeTableActivity) {
            iListener = (ITimeTableActivity) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement ITimeTableActivity");
        }
    }

    public interface ITimeTableActivity {
        void toAddEvent(String day);
        void toNavigation(String day);
    }

}