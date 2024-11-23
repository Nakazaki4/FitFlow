package com.projectx.fitfloaw;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheRepository {
    private static final String TAG = "CacheRepository";
    private SharedPreferences prefs;
    private Context context;

    public CacheRepository(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE);
    }

    public void saveInitialStepCount(int initialStepCount) {
        prefs.edit().putInt("initialStepCount", initialStepCount).apply();
    }

    public void saveInitialWalkingTime(int initialActiveTime) {
        prefs.edit().putInt("totalWalkingTimeMinutes", initialActiveTime).apply();
    }

    public int getInitialStepCount() {
        return prefs.getInt("initialStepCount", -1);
    }

    public int getInitialWalkingTime() {
        return prefs.getInt("totalWalkingTimeMinutes", 0);
    }

    public void saveCaloriesBurned(int calories){
        prefs.edit().putInt("caloriesBurned", calories).apply();
    }

    public int getCaloriesBurned(){
        return prefs.getInt("caloriesBurned", 0);
    }
}
