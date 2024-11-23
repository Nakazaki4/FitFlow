package com.projectx.fitfloaw;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActivitiesDataViewModel extends ViewModel {
    private MutableLiveData<Integer> stepsWalked = new MutableLiveData<>();
    private MutableLiveData<Integer> minutesWalked = new MutableLiveData<>();
    private MutableLiveData<Integer> caloriesBurned = new MutableLiveData<>();

    public void setStepsWalked(int stepsWalked) {
        this.stepsWalked.setValue(stepsWalked);
    }

    public MutableLiveData<Integer> getStepsWalked() {
        return stepsWalked;
    }

    public void setMinutesWalked(int minutesWalked) {
        this.minutesWalked.setValue(minutesWalked);
    }

    public MutableLiveData<Integer> getMinutesWalked() {
        return minutesWalked;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned.setValue(caloriesBurned);
    }

    public MutableLiveData<Integer> getCaloriesBurned() {
        return caloriesBurned;
    }
}
