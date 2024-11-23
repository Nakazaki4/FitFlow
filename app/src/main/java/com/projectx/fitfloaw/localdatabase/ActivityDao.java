package com.projectx.fitfloaw.localdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ActivityDao {

    @Insert
    void insertWalkingSession(WalkingSession walkingSession);

    @Query("SELECT * FROM walking_session where date = :date")
    LiveData<WalkingSession> getWalkingSessionsForDate(long date);

    @Query("SELECT * FROM walking_session")
    LiveData<List<WalkingSession>> getAllWalkingSessions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSchedulerState(SchedulerState schedulerState);

    @Update
    void updateSchedulerState(SchedulerState schedulerState);

    @Query("SELECT * FROM scheduler_state WHERE id = 1")
    LiveData<SchedulerState> getSchedulerState();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPhysicalData(PhysicalData physicalData);

    @Query("SELECT * FROM physical_data WHERE id = 1")
    LiveData<PhysicalData> getPhysicalData();

    @Insert
    void insertGoalData(GoalsData goalsData);

    @Query("SELECT * FROM goals_table WHERE id = 1")
    LiveData<GoalsData> getGoalData();
}
