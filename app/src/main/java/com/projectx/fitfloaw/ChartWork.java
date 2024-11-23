package com.projectx.fitfloaw;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectx.fitfloaw.localdatabase.DatabaseRepository;
import com.projectx.fitfloaw.localdatabase.WalkingSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartWork {
    private static ChartWork instance;
    private BarChart barChart;
    private List<WalkingSession> walkingSessions;
    private List<Long> datesList;
    private Context context;

    private int averageSteps;

    public ChartWork(Context context, BarChart barChart) {
        this.barChart = barChart;
        this.context = context;
        mockChartData();
        initChart();
        barChart.setRenderer(new RoundedBarChartRenderer(barChart, barChart.getAnimator(), barChart.getViewPortHandler()));
    }

    private void mockChartData() {
        walkingSessions = new ArrayList<>();

        // Example mock data
        /*walkingSessions.add(new WalkingSession(1727049600000L, 1000, 10, 100));
        walkingSessions.add(new WalkingSession(1727136000000L, 1100, 10, 100));
        walkingSessions.add(new WalkingSession(1727222400000L, 1200, 10, 100));
        walkingSessions.add(new WalkingSession(1727308800000L, 1300, 10, 100));
        walkingSessions.add(new WalkingSession(1727395200000L, 1400, 10, 100));
        walkingSessions.add(new WalkingSession(1727481600000L, 1500, 10, 100));
        walkingSessions.add(new WalkingSession(1727568000000L, 1600, 10, 100));*/

    }

    /**
     * Initializes the chart by fetching walking sessions and setting the data.
     */
    private void initChart() {
        walkingSessions = DatabaseRepository.getInstance(context).getAllWalkingSessions().getValue();

        if (walkingSessions == null || walkingSessions.isEmpty()) {
            // Handle the case when no data is available
            barChart.clear();
            barChart.setNoDataText("No walking sessions available yet.");
            return;
        }

        datesList = new ArrayList<>();  // Store actual dates (epoch time)
        // Prepare chart data
        List<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (WalkingSession session : walkingSessions) {
            datesList.add(session.getDate());
            entries.add(new BarEntry(index++, session.getSteps()));
            averageSteps += session.getSteps();
        }

        // Configure dataset
        BarDataSet barDataSet = new BarDataSet(entries, "Steps Walked");
        barDataSet.setColor(Color.parseColor("#FF8100"));
        barDataSet.setValueTextSize(11f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.52f);

        addAverageLine();

        // Set data and customize the x-axis
        barChart.setData(barData);
        barChart.animateY(3000);
        customizeXAxis();
        customizeYAxis();
        barChart.invalidate();  // Refresh chart view
    }

    private void addAverageLine() {
        YAxis leftAxis = barChart.getAxisLeft();

        averageSteps = averageSteps / walkingSessions.size();
        LimitLine averageLine = new LimitLine(averageSteps, "avg. steps " + averageSteps);
        averageLine.setLineColor(Color.GRAY);  // Color for the line
        averageLine.setLineWidth(2f);  // Line thickness
        averageLine.setTextColor(Color.GRAY);  // Label text color
        averageLine.setTextSize(10f);  // Label text size
        averageLine.enableDashedLine(15, 10, 0);

        leftAxis.addLimitLine(averageLine);
    }

    /**
     * Customizes the x-axis to display dates in MM/dd format.
     */
    private void customizeXAxis() {
        XAxis xAxis = barChart.getXAxis();
        barChart.setVisibleXRangeMaximum(6);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(-0.5f);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;  // Cast the float index back to an int
                long epochMillis = datesList.get(index);  // Get the date from the list

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
                return dateFormat.format(new Date(epochMillis));  // Return formatted date
            }
        });
    }


    private void customizeYAxis() {
        barChart.getAxisRight().setEnabled(false); // Disable right y-axis

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // Remove grid lines
        leftAxis.setAxisMinimum(0f);
    }
}

