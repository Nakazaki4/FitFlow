package com.projectx.fitfloaw;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.navigation.NavigationView;
import com.projectx.fitfloaw.databinding.ActivityMainBinding;
import com.projectx.fitfloaw.firebase.AuthManager;
import com.projectx.fitfloaw.firebase.LoginActivity;
import com.projectx.fitfloaw.fragments.ActivityGoalsFragment;
import com.projectx.fitfloaw.fragments.PhysicalInformationFragment;
import com.projectx.fitfloaw.localdatabase.DatabaseRepository;
import com.projectx.fitfloaw.localdatabase.PhysicalData;
import com.projectx.fitfloaw.sensors.WalkingActivity;
import com.projectx.fitfloaw.settings.CalorieCalculator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 100;
    private RecyclerView mDateRecyclerView;
    private GenerateDateList generateDateList;
    private ActivityMainBinding binding;
    private ActivitiesDataViewModel viewModel;
    private DailyScheduler dailyScheduler;
    private DatabaseRepository databaseRepository;
    private ChartWork chartWork;
    private BarChart barChart;
    private CalorieCalculator calorieCalculator;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkUserAuthentication();

        viewModel = new ViewModelProvider(this).get(ActivitiesDataViewModel.class);


        setToolbar();

        checkPermissionsToStartStepCountingService();
        databaseRepository = DatabaseRepository.getInstance(this);

        getPhysicalData();

        bindUiElements();
        generateDateList = new GenerateDateList();
        initializeUiElements();
        chartWork = new ChartWork(this, barChart);
    }

    private void checkUserAuthentication(){
        authManager = new AuthManager(this);
        if (!authManager.isUserLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void getPhysicalData() {
        databaseRepository.getPhysicalData().observe(this, new Observer<PhysicalData>() {
            @Override
            public void onChanged(PhysicalData physicalData) {
                if (physicalData != null) {
                    int age = physicalData.getAge();
                    String gender = physicalData.getGender();
                    double height = physicalData.getHeight();

                    CalorieCalculator.setAge(age);
                    CalorieCalculator.setGender(gender);
                    CalorieCalculator.setHeightCm(height);
                }
            }
        });
    }

    private void setToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Set the custom toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    private void bindUiElements() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        setChart();
        setNavigationView();

        binding.toolbarProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        viewModel.getStepsWalked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer stepsCount) {
                binding.tvStepCount.setText(String.valueOf(stepsCount));
            }
        });
        viewModel.getMinutesWalked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.tvMinutesWalked.setText(String.valueOf(integer + " min"));
            }
        });

        viewModel.getCaloriesBurned().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.tvCaloriesCount.setText(String.valueOf(integer + "kcal"));
            }
        });


        startLiveDataObservers();
    }

    private void setNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_physical_information) {
                    PhysicalInformationFragment fragment = new PhysicalInformationFragment();
                    setFragment(fragment);
                    closeDrawer();

                } else if (itemId == R.id.nav_activity_goals) {
                    ActivityGoalsFragment fragment = new ActivityGoalsFragment();
                    setFragment(fragment);
                    closeDrawer();

                } else if (itemId == R.id.nav_logout) {
                    // logout out of the account and go to login screen
                }
                return false;
            }
        });
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void startLiveDataObservers() {
        databaseRepository.getStepsWalked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                viewModel.setStepsWalked(integer);
            }
        });
        databaseRepository.getMinutesWalked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                viewModel.setMinutesWalked(integer);
            }
        });

        databaseRepository.getCaloriesBurned().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                viewModel.setCaloriesBurned(integer);
            }
        });

        databaseRepository.getStepsProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.progressSteps.setProgress(integer);
            }
        });
        databaseRepository.getCaloriesProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.progressCalories.setProgress(integer);
            }
        });

        // Check if goals are accomplished, then send notification and play animation
        databaseRepository.getGoalsFlag().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean goalsAccomplished) {
                if (goalsAccomplished) {
                    sendCongratulationsNotification();
                    binding.animationView.playAnimation();
                }
            }
        });
    }

    private void sendCongratulationsNotification() {
        String channelId = "goals_channel";
        String channelName = "Goals Notifications";
        int notificationId = 1;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_win_gesture)
                .setContentTitle("FitFlow!")
                .setContentText("Congratulations you've accomplished your goals!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    private void initDailyScheduler() {
        dailyScheduler = new DailyScheduler(viewModel, databaseRepository);
        dailyScheduler.setDailyScheduler(this);
    }

    private void initializeUiElements() {
        mDateRecyclerView = findViewById(R.id.recyclerview_date);
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        DateAdapter mAdapter = new DateAdapter(this, this, generateDateList.generateDateList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mDateRecyclerView.setAdapter(mAdapter);
        mDateRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void checkPermissionsToStartStepCountingService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACTIVITY_RECOGNITION
            }, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted for ACTIVITY_RECOGNITION
                startStepCountingService();
            } else {
                // Permission denied, handle accordingly
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showPermissionExplanationDialog();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("It is necessary to give the physical activity permission so that your steps can be tracked")
                .setPositiveButton("Try again", (dialog, which) -> {
                    // Request the permission again
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION},
                            REQUEST_CODE);
                })
                .setNeutralButton("Open Settings", (dialog, which) -> {
                    openAppSettings();
                })
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void startStepCountingService() {
        Intent intent = new Intent(this, WalkingActivity.class);
        ContextCompat.startForegroundService(this, intent);
        initDailyScheduler();
    }

    private void setChart() {
        barChart = findViewById(R.id.chart_bar);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setFitBars(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setBackgroundColor(Color.parseColor("#F0E6FF"));
    }

    public void staticUiElementsBinding(int steps, int minutes, int calories) {
        binding.tvStepCount.setText(String.valueOf(steps));
        binding.tvMinutesWalked.setText(String.valueOf(minutes + " min"));
        binding.tvCaloriesCount.setText(String.valueOf(calories + "kcal"));
    }

    public void stopLiveDataObservers() {
        databaseRepository.getStepsWalked().removeObservers(this);
        databaseRepository.getMinutesWalked().removeObservers(this);
        databaseRepository.getCaloriesBurned().removeObservers(this);
    }
}