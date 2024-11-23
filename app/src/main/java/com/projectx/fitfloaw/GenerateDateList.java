package com.projectx.fitfloaw;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GenerateDateList {

    public GenerateDateList() {
    }

    public List<DateModel> generateDateList() {
        List<DateModel> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Save the current date to compare later
        Calendar currentDate = (Calendar) calendar.clone();

        // Set to three days before the current date
        calendar.add(Calendar.DAY_OF_MONTH, -3);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        // Loop to create a list of 7 days
        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(calendar.getTime());
            String day = dayFormat.format(calendar.getTime());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayInNumber = calendar.get(Calendar.DAY_OF_MONTH);

            DateModel item = new DateModel();
            item.setDate(date);
            item.setDay(day);
            item.setDayInNumber(dayInNumber);
            item.setLocalDate(LocalDate.of(year, month + 1, dayInNumber));

            // Set the current date as selected
            if (calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)) {
                item.setSelected(true);
            }

            dateList.add(item);

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dateList;
    }

}
