package com.example.cs5520_finalproject_group2;

public class Week {
    private String day;

    public Week() {
    }

    public Week(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Week{" +
                "day='" + day + '\'' +
                '}';
    }
}
