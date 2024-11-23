package com.projectx.fitfloaw.sensors;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.projectx.fitfloaw.CacheRepository;
import com.projectx.fitfloaw.R;
import com.projectx.fitfloaw.localdatabase.DatabaseRepository;
import com.projectx.fitfloaw.localdatabase.GoalsData;
import com.projectx.fitfloaw.settings.CalorieCalculator;

public class WalkingActivity extends LifecycleService implements SensorEventListener2 {
    public static final long INACTIVE_THRESHOLD = 5000;
    private static final String TAG = "StepCounting";

    private SensorManager sensorManager;
    private CacheRepository cacheRepository;
    private DatabaseRepository databaseRepository;

    private int initialStepCount = -1;
    private int lastStepCount = 0;
    private long activityStartTime = 0;
    private long lastStepTimestamp = 0;
    private int totalActiveTimeMillis = 0;
    private int initialWalkingTime = 0;
    private int totalSteps = 0;

    private int initialCaloriesBurned = 0;

    private int goalSteps;
    private int goalCalories;
    private double caloriesBurned;

    public WalkingActivity() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundNotification();
        initializeStepCounterAndChronometer();
        getGoalsData();
    }

    public void initializeStepCounterAndChronometer() {
        getSensorManager();
        databaseRepository = DatabaseRepository.getInstance(this);
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Step counter available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Step counter hardware not available in your device", Toast.LENGTH_SHORT).show();
        }
        sharedPreferences();
        databaseRepository.setMinutesWalked(initialWalkingTime);
        databaseRepository.setCaloriesBurned(initialCaloriesBurned);
        resetAtMidnight();
    }

    private void startForegroundNotification() {
        NotificationChannel channel = new NotificationChannel("StepCounterService", "Step Counter Service", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, "StepCounterService")
                .setContentTitle("FitFlow")
                .setContentText("Your physical activity is being tracked.")
                .setSmallIcon(R.drawable.steps_icon)
                .build();

        startForeground(1, notification);
    }

    private void getSensorManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use the type-safe method for API 23 (Marshmallow) and above
            sensorManager = this.getSystemService(SensorManager.class);
        } else {
            // Use the older method for API levels below 23
            sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        }
    }

    private void sharedPreferences() {
        cacheRepository = new CacheRepository(this);
        initialStepCount = cacheRepository.getInitialStepCount();
        initialWalkingTime = cacheRepository.getInitialWalkingTime();
        initialCaloriesBurned = cacheRepository.getCaloriesBurned();
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int stepCount = (int) sensorEvent.values[0];
            long currentTimeStamp = System.currentTimeMillis();

            if (initialStepCount == -1) {
                initialStepCount = stepCount;
            }

            int currentSteps = stepCount - initialStepCount;
            int stepsSinceLastUpdate = currentSteps - lastStepCount;

            if (stepsSinceLastUpdate > 0) {
                if (activityStartTime == 0 || currentTimeStamp - lastStepTimestamp > INACTIVE_THRESHOLD) {
                    startWalkingTimer();
                }
                totalSteps += stepsSinceLastUpdate;
                lastStepTimestamp = currentTimeStamp;
            } else if (activityStartTime != 0 && currentTimeStamp - lastStepTimestamp > INACTIVE_THRESHOLD) {
                stopWalkingTimer();
            }

            lastStepCount = currentSteps;
            updateActiveTime(currentTimeStamp);
            updateStrokeProgress();
            updatePrefsAndViewModel();
        }
    }

    private void updateStrokeProgress() {
        // Calculating the progress of the steps and calories burned
        int stepsProgress = (totalSteps / goalSteps * 100);
        int caloriesProgress = (int) (caloriesBurned / goalCalories * 100);

        // Notifying the UI about the progress
        DatabaseRepository.getInstance(this).setStepsProgress(stepsProgress);
        DatabaseRepository.getInstance(this).setCaloriesProgress(caloriesProgress);

        // If the goals achieved and and goals flag is false then set the flag to true
        if (stepsProgress >= 100 && caloriesProgress >= 100) {
            boolean isGoalsFlag = DatabaseRepository.getInstance(this).getGoalsFlag().getValue();
            if (!isGoalsFlag) {
                DatabaseRepository.getInstance(this).setGoalsFlag(true);
            }
        }
    }

    private void getGoalsData() {
        DatabaseRepository.getInstance(this).getGoalData().observe(this, new Observer<GoalsData>() {
            @Override
            public void onChanged(GoalsData goalsData) {
                if (goalsData != null) {
                    goalSteps = goalsData.getGoalSteps();
                    goalCalories = goalsData.getGoalCalories();
                }
            }
        });
    }

    private void updatePrefsAndViewModel() {
        int totalActiveMinutes = totalActiveTimeMillis / 60000;
        caloriesBurned = CalorieCalculator.calculateCaloriesBurned(totalSteps);

        cacheRepository.saveInitialStepCount(totalSteps);
        cacheRepository.saveInitialWalkingTime(totalActiveMinutes);
        cacheRepository.saveCaloriesBurned((int) caloriesBurned);

        databaseRepository.setStepsWalked(totalSteps);
        databaseRepository.setMinutesWalked(totalActiveMinutes);
        databaseRepository.setCaloriesBurned((int) caloriesBurned);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void startWalkingTimer() {
        if (activityStartTime == 0) {
            activityStartTime = System.currentTimeMillis();
            Toast.makeText(this, "Timer started", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopWalkingTimer() {
        if (activityStartTime != 0) {
            long elapsedMillis = System.currentTimeMillis() - activityStartTime;
            totalActiveTimeMillis += (int) elapsedMillis;
            activityStartTime = 0;
            Toast.makeText(this, "Timer stopped", Toast.LENGTH_SHORT).show();
            updatePrefsAndViewModel();
        }
    }

    private void updateActiveTime(long currentTimeStamp) {
        if (activityStartTime != 0) {
            totalActiveTimeMillis += (int) (currentTimeStamp - lastStepTimestamp);
        }
    }

    private void resetAtMidnight() {
        databaseRepository.getMidNightFlag().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean flagValue) {
                if (flagValue != null && flagValue) {
                    resetActivitiesData();
                    databaseRepository.setMidNightFlag(false);
                }
            }
        });
    }

    private void resetActivitiesData() {
        initialStepCount = -1;
        lastStepCount = 0;
        activityStartTime = 0;
        lastStepTimestamp = 0;
        totalActiveTimeMillis = 0;
        initialWalkingTime = 0;
        initialCaloriesBurned = 0;
        resetCache();
        resetRepository();
        DatabaseRepository.getInstance(this).setGoalsFlag(false);
    }

    private void resetCache() {
        cacheRepository.saveInitialStepCount(-1);
        cacheRepository.saveInitialWalkingTime(0);
        cacheRepository.saveCaloriesBurned(0);
    }

    private void resetRepository() {
        databaseRepository.setStepsWalked(0);
        databaseRepository.setMinutesWalked(0);
        databaseRepository.setCaloriesBurned(0);
    }
}
