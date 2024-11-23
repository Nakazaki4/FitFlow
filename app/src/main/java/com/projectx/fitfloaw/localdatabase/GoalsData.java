package com.projectx.fitfloaw.localdatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals_table")
public class GoalsData {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int goalSteps;
    private int goalCalories;

    public GoalsData() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GoalsData(int goalSteps, int goalCalories) {
        this.goalSteps = goalSteps;
        this.goalCalories = goalCalories;
    }

    public int getGoalSteps() {
        return goalSteps;
    }

    public void setGoalSteps(int goalSteps) {
        this.goalSteps = goalSteps;
    }

    public int getGoalCalories() {
        return goalCalories;
    }

    public void setGoalCalories(int goalCalories) {
        this.goalCalories = goalCalories;
    }
}
