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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AddEventFragment extends Fragment {
    private static final String ARG_DAY = "day";
    TextView addEventName, addStartTime, addEndTime, addEventLocation;
    RadioGroup addGroup;
    Button addSaveButton;
    Event event;
    IAddEventActivity addEventActivity;
    public AddEventFragment() {
        // Required empty public constructor
    }
    public static AddEventFragment newInstance(String day) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (getArguments() != null) {
            if (args.containsKey(ARG_DAY)) {
                event.setDay(args.getString(ARG_DAY));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        Bundle args = getArguments();
        String day = args.getString(ARG_DAY);
        addEventName = view.findViewById(R.id.addEventName);
        addStartTime = view.findViewById(R.id.addStartTime);
        addEndTime = view.findViewById(R.id.addEndTime);
        addEventLocation = view.findViewById(R.id.addEventLocation);
        addGroup = view.findViewById(R.id.addGroup);
        addSaveButton = view.findViewById(R.id.addSaveButton);

        addGroup.check(getDefaultCheckedDay(day));

        addSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addEventName.getText().toString();
                String startTime = addStartTime.getText().toString();
                String endTime = addEndTime.getText().toString();
                String location = addEventLocation.getText().toString();
                if (name.equals("")) {
                    addEventName.setError("Event Name Cannot Be Empty!");
                } else if (startTime.equals("")) {
                    addStartTime.setError("Event Start Time Cannot Be Empty!");
                } else if (endTime.equals("")) {
                    addEndTime.setError("Event End Time Cannot Be Empty!");
                } else if (location.equals("")) {
                    addEventLocation.setError("Event Location Cannot Be Empty!");
                } else {
                    event.setName(name);
                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    event.setLocation(location);
                    Log.d("EVENT", "onClick: " + event.toString());
                    addEventToDb(event);
                    addEventActivity.addEvent();
                }
            }
        });

        return view;
    }

    private void addEventToDb(Event event) {

    }

    private int getDefaultCheckedDay(String day) {
        if (day.equals("Mon")) {
            return 0;
        } else if (day.equals("Tue")) {
            return 1;
        } else if (day.equals("Wed")) {
            return 2;
        } else if (day.equals("Thu")) {
            return 3;
        } else if (day.equals("Fri")) {
            return 4;
        }
        return 0;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IAddEventActivity) {
            addEventActivity = (IAddEventActivity) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement IAddEventActivity");
        }
    }

    public interface IAddEventActivity {
        void addEvent();
    }
}