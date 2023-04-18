package com.example.cs5520_finalproject_group2;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {

    private String name, day, location, startTime, endTime;

    public Event() {
    }

    public Event(String name, String location, String day, String startTime, String endTime) {
        this.name = name;
        this.day = day;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", day='" + day + '\'' +
                ", location='" + location + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

    public static void sort(ArrayList<Event> events) {
        events.sort((o1, o2)
                -> o1.getStartTime().compareTo(
                o2.getStartTime()));
    }
}
