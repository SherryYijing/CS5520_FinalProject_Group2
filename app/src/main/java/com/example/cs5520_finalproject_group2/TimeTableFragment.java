package com.example.cs5520_finalproject_group2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalTime;
import java.util.ArrayList;

public class TimeTableFragment extends Fragment {
    private Button newEventButton, showNavigationButton, timeTableBack;
    private TextView timeTableMon, timeTableTue, timeTableWed, timeTableThu, timeTableFri;
    private static final String ARG_DAY = "day";
    private RecyclerView eventRecyclerView;
    private ArrayList<Event> events = new ArrayList<>();

    private EventAdapter eventAdapter;
    private String selectedDay;
    private TextView dayView;
    private ITimeTableActivity iListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    public TimeTableFragment() {
        // Required empty public constructor
    }

    public static TimeTableFragment newInstance(String day) {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        Bundle args = getArguments();
        if (getArguments() != null) {
            if (args.containsKey(ARG_DAY)) {
                selectedDay = args.getString(ARG_DAY);
                if (selectedDay == null) {
                    selectedDay = "Mon";
                }
            }
        }
        loadEventFromDb();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);
        newEventButton = view.findViewById(R.id.newEventButton);
        showNavigationButton = view.findViewById(R.id.showNavigationButton);
        timeTableMon = view.findViewById(R.id.timeTableMon);
        timeTableTue = view.findViewById(R.id.timeTableTue);
        timeTableWed = view.findViewById(R.id.timeTableWed);
        timeTableThu = view.findViewById(R.id.timeTableThu);
        timeTableFri = view.findViewById(R.id.timeTableFri);
        timeTableBack = view.findViewById(R.id.timeTableBack);
        dayView = view.findViewById(R.id.dayView);

        timeTableBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iListener.toUserProfile();
            }
        });

        timeTableMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDay = "Mon";
                dayView.setText("Events for " + selectedDay);
                loadEventFromDb();
            }
        });

        timeTableTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDay = "Tue";
                dayView.setText("Events for " + selectedDay);
                loadEventFromDb();
            }
        });

        timeTableWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDay = "Wed";
                dayView.setText("Events for " + selectedDay);
                loadEventFromDb();
            }
        });

        timeTableThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDay = "Thu";
                dayView.setText("Events for " + selectedDay);
                loadEventFromDb();
            }
        });

        timeTableFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDay = "Fri";
                dayView.setText("Events for " + selectedDay);
                loadEventFromDb();
            }
        });

        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        eventAdapter = new EventAdapter(events, this.getContext());
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        eventRecyclerView.setAdapter(eventAdapter);
        Event.sort(events);
        loadEventFromDb();
        dayView.setText("Events for " + selectedDay);

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

        // Create a listener for firebase data change
        db.collection("user")
                .document(currentUser.getEmail())
                .collection(selectedDay)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            // Retrieving all the elements from firebase
                            ArrayList<Event> newEvents = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                newEvents.add(documentSnapshot.toObject(Event.class));
                            }
                            // Replace all the items in the current recycleView with the received elements
                            eventAdapter.setEvents(newEvents);
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                });

        return view;
    }

    private void loadEventFromDb() {
        ArrayList<Event> eventList = new ArrayList<>();
        db.collection("user")
                .document(currentUser.getEmail())
                .collection(selectedDay)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                Event event = queryDocumentSnapshot.toObject(Event.class);
                                eventList.add(event);
                            }
                            Log.d("load", "onComplete: " + eventList.size());
                            if (eventList.size() < 1) {
                                Toast.makeText(getContext(), "You don't have any event on this day",
                                        Toast.LENGTH_LONG).show();
                            }
                            Event.sort(eventList);
                            Log.d("sort", "onComplete: " + eventList.toString());
                            eventAdapter.setEvents(eventList);
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                });
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
        void toUserProfile();
    }

}