package com.projectx.fitfloaw;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.projectx.fitfloaw.localdatabase.DatabaseRepository;
import com.projectx.fitfloaw.localdatabase.SchedulerState;
import com.projectx.fitfloaw.localdatabase.WalkingSession;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DailyScheduler {
    private static final String TAG = "DailyScheduler";
    private ScheduledExecutorService scheduler;
    private ActivitiesDataViewModel viewModel;
    private DatabaseRepository databaseRepository;

    public DailyScheduler(ActivitiesDataViewModel viewModel, DatabaseRepository databaseRepository) {
        scheduler = Executors.newScheduledThreadPool(1);
        this.viewModel = viewModel;
        this.databaseRepository = databaseRepository;
    }

    public void setDailyScheduler(LifecycleOwner lifecycleOwner) {
        databaseRepository.getSchedulerState().observe(lifecycleOwner, new Observer<SchedulerState>() {
            @Override
            public void onChanged(SchedulerState schedulerState) {
                // Set the daily scheduler to true if not already set
                if (schedulerState != null) {
                    if (!schedulerState.isSchedulerState()) {
                        databaseRepository.updateSchedulerState(true);
                        scheduleDailySaving();
                    }
                } else {
                    SchedulerState state = new SchedulerState();
                    state.setSchedulerState(true);
                    databaseRepository.insertSchedulerState(state);
                    scheduleDailySaving();
                }
            }
        });
    }

    public void scheduleDailySaving() {
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                dataToSave();
            }
        }, 0, getMillisUntilMidnight(), TimeUnit.SECONDS);
    }

    private long getMillisUntilMidnight() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return (c.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    private void dataToSave() {
        int stepsWalked = viewModel.getStepsWalked().getValue() != null ? viewModel.getStepsWalked().getValue() : 0;
        int minutesWalked = viewModel.getMinutesWalked().getValue() != null ? viewModel.getMinutesWalked().getValue() : 0;
        int caloriesBurned = viewModel.getCaloriesBurned().getValue() != null ? viewModel.getCaloriesBurned().getValue() : 0;

        LocalDate currentDate = LocalDate.now();
        long currentEpochDate = currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        WalkingSession session = new WalkingSession(currentEpochDate, stepsWalked, minutesWalked, caloriesBurned);

        databaseRepository.insertWalkingSession(session);
        databaseRepository.setMidNightFlag(true);
        resetViewModel();
    }

    private void resetViewModel() {
        viewModel.setStepsWalked(0);
        viewModel.setMinutesWalked(0);
        viewModel.setCaloriesBurned(0);
    }
}
