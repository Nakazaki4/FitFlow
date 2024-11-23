package com.projectx.fitfloaw.localdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {WalkingSession.class, SchedulerState.class, PhysicalData.class, GoalsData.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LocalDB extends RoomDatabase {
    private static LocalDB instance;

    public abstract ActivityDao activityDao();

    public static synchronized LocalDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            LocalDB.class, "fitness_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
