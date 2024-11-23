package com.projectx.fitfloaw.localdatabase;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseRepository {
    private static final String TAG = "DatabaseRepository";
    private static DatabaseRepository instance;
    private ActivityDao activityDao;
    private ExecutorService executorService;

    private MutableLiveData<Integer> stepsWalked = new MutableLiveData<>();
    private MutableLiveData<Integer> minutesWalked = new MutableLiveData<>();
    private MutableLiveData<Integer> caloriesBurned = new MutableLiveData<>();

    private MutableLiveData<Integer> stepsProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> caloriesProgress = new MutableLiveData<>();

    private MutableLiveData<Boolean> midNightFlag = new MutableLiveData<>();
    private MutableLiveData<Boolean> goalsFlag = new MutableLiveData<>();

    public DatabaseRepository() {

    }

    public DatabaseRepository(Context context) {
        LocalDB db = LocalDB.getInstance(context.getApplicationContext());
        activityDao = db.activityDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public static synchronized DatabaseRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseRepository(context.getApplicationContext());
        }
        return instance;
    }

    public void insertWalkingSession(WalkingSession walkingSession) {
        executorService.execute(() -> activityDao.insertWalkingSession(walkingSession));
    }

    public WalkingSession getWalkingSessionByDate(long date) {
        return activityDao.getWalkingSessionsForDate(date).getValue();
    }

    public LiveData<List<WalkingSession>> getAllWalkingSessions() {
        return activityDao.getAllWalkingSessions();
    }


    public void insertSchedulerState(SchedulerState state) {
        executorService.execute(() -> activityDao.insertSchedulerState(state));
    }

    public LiveData<SchedulerState> getSchedulerState() {
        return activityDao.getSchedulerState();
    }

    public void updateSchedulerState(boolean isSchedulerState) {
        executorService.execute(() -> {
            SchedulerState state = getSchedulerState().getValue();
            if (state != null) {
                state.setSchedulerState(isSchedulerState);
                activityDao.updateSchedulerState(state);
            }
        });
    }

    public LiveData<Integer> getStepsWalked() {
        return stepsWalked;
    }

    public void setStepsWalked(int steps) {
        stepsWalked.setValue(steps);
    }

    public LiveData<Integer> getMinutesWalked() {
        return minutesWalked;
    }

    public void setMinutesWalked(int minutes) {
        minutesWalked.setValue(minutes);
    }

    public LiveData<Boolean> getMidNightFlag() {
        return midNightFlag;
    }

    public void setMidNightFlag(boolean midNightFlag) {
        this.midNightFlag.setValue(midNightFlag);
    }

    public void setCaloriesBurned(int calories) {
        caloriesBurned.setValue(calories);
    }

    public LiveData<Integer> getCaloriesBurned() {
        return caloriesBurned;
    }

    public void insertPhysicalData(PhysicalData physicalData) {
        executorService.execute(() -> activityDao.insertPhysicalData(physicalData));
    }

    public LiveData<PhysicalData> getPhysicalData() {
        return activityDao.getPhysicalData();
    }

    public void insertGoalData(GoalsData goalsData) {
        executorService.execute(() -> activityDao.insertGoalData(goalsData));
    }

    public LiveData<GoalsData> getGoalData() {
        return activityDao.getGoalData();
    }

    public void setStepsProgress(int steps) {
        stepsProgress.setValue(steps);
    }

    public LiveData<Integer> getStepsProgress() {
        return stepsProgress;
    }

    public void setCaloriesProgress(int calories) {
        caloriesProgress.setValue(calories);
    }

    public LiveData<Integer> getCaloriesProgress() {
        return caloriesProgress;
    }

    public void setGoalsFlag(boolean flag) {
        goalsFlag.setValue(flag);
    }
    public LiveData<Boolean> getGoalsFlag() {
        return goalsFlag;
    }
}
