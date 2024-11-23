package com.projectx.fitfloaw;

import java.time.LocalDate;

public class DateModel {
    private boolean isSelected;
    private LocalDate localDate;
    private String date;
    private String day;
    private int dayInNumber;

    public DateModel() {
    }

    public DateModel(LocalDate localDate, String date, String day, int dayInNumber, boolean isSelected) {
        this.localDate = localDate;
        this.date = date;
        this.day = day;
        this.dayInNumber = dayInNumber;
        this.isSelected = isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public String getDate() {
        return date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getDayInNumber() {
        return dayInNumber;
    }

    public void setDayInNumber(int dayInNumber) {
        this.dayInNumber = dayInNumber;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}
