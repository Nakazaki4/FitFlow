package com.projectx.fitfloaw.localdatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "walking_session")
public class WalkingSession {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long date;
    public int steps;
    public int minutes;
    public int calories;

    public WalkingSession(long date, int steps, int minutes, int calories) {
        this.date = date;
        this.steps = steps;
        this.minutes = minutes;
        this.calories = calories;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
