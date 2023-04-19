package com.example.cs5520_finalproject_group2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;

public class EditEventFragment extends Fragment {
    private static final String ARG_EVENT = "event";
    private Event event;
    private TextView editEventName, editStartTime, editEndTime, editEventLocation;
    private Button editSaveButton, editDeleteButton;
    private IEditEventActivity editEventActivity;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public EditEventFragment() {
        // Required empty public constructor
    }

    public static EditEventFragment newInstance(Event event) {
        EditEventFragment fragment = new EditEventFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            if (args.containsKey(ARG_EVENT)) {
                event = (Event) args.getSerializable(ARG_EVENT);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);
        editEventName = view.findViewById(R.id.editEventName);
        editStartTime = view.findViewById(R.id.editStartTime);
        editEndTime = view.findViewById(R.id.editEndTime);
        editEventLocation = view.findViewById(R.id.editEventLocation);
        editSaveButton = view.findViewById(R.id.editSaveButton);
        editDeleteButton = view.findViewById(R.id.editDeleteButton);

        editEventName.setText(event.getName());
        editEventName.setEnabled(false);
        editStartTime.setText(event.getStartTime().toString());
        editEndTime.setText(event.getEndTime().toString());
        editEventLocation.setText(event.getLocation());

        editSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editEventName.getText().toString();
                String startTime = editStartTime.getText().toString();
                String endTime = editEndTime.getText().toString();
                String location = editEventLocation.getText().toString();
                if (name.equals("")) {
                    editEventName.setError("Event Name Cannot Be Empty!");
                } else if (startTime.equals("")) {
                    editStartTime.setError("Event Start Time Cannot Be Empty!");
                } else if (endTime.equals("")) {
                    editEndTime.setError("Event End Time Cannot Be Empty!");
                } else if (!validTime(startTime, endTime)) {
                    editEndTime.setError("Start time should be earlier than end time!");
                } else if (location.equals("")) {
                    editEventLocation.setError("Event Location Cannot Be Empty!");
                } else {
                    event.setName(name);
                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    event.setLocation(location);
                    editEventInDb(event);
                    editEventActivity.editEvent(event.getDay());
                }
            }
        });

        editDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventInDb(event);
                editEventActivity.deleteEvent(event.getDay());
            }
        });

        return view;
    }

    private void deleteEventInDb(Event event) {
        db.collection("user")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection(event.getDay())
                .document(event.getName())
                .delete();
    }

    private void editEventInDb(Event event) {
        db.collection("user")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection(event.getDay())
                .document(event.getName())
                .set(event);
    }

    private Boolean validTime(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return (start.compareTo(end) < 0);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IEditEventActivity) {
            editEventActivity = (IEditEventActivity) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IEditEventActivity");
        }
    }

    public interface IEditEventActivity {
        void editEvent(String day);
        void deleteEvent(String day);
    }
}