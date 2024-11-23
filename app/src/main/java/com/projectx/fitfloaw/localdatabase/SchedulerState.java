package com.projectx.fitfloaw.localdatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scheduler_state")
public class SchedulerState {
    @PrimaryKey
    private int id = 1;
    private boolean isSchedulerState = false ;

    public SchedulerState(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSchedulerState() {
        return isSchedulerState;
    }

    public void setSchedulerState(boolean schedulerState) {
        isSchedulerState = schedulerState;
    }
}
