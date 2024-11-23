package com.projectx.fitfloaw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.projectx.fitfloaw.R;
import com.projectx.fitfloaw.localdatabase.DatabaseRepository;
import com.projectx.fitfloaw.localdatabase.GoalsData;


public class ActivityGoalsFragment extends Fragment {
    private TextInputEditText stepsInput;
    private TextInputEditText caloriesInput;
    private Button saveButton;

    public ActivityGoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_goals, container, false);
        uiElements(view);
        setToolbar(view);
        return view;
    }

    private void uiElements(View view) {
        stepsInput = view.findViewById(R.id.stepsGoalInput);
        caloriesInput = view.findViewById(R.id.caloriesGoalInput);
        saveButton = view.findViewById(R.id.saveButton);
        saveButton();
    }

    private void saveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int steps = Integer.parseInt(stepsInput.getText().toString().trim());
                int calories = Integer.parseInt(caloriesInput.getText().toString().trim());

                if (inputsCheck(steps, calories)) {
                    saveGoalData(new GoalsData(steps, calories));

                    Toast.makeText(getContext(), "Goals saved successfully!", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    private void setToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_fragment);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Set Goals");  // Set the title text
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // Show the back button
            toolbar.setTitleTextAppearance(getContext(), R.style.CustomToolbarTitle);  // Apply custom text style
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
    }

    private boolean inputsCheck(int steps, int calories) {
        // Check if any fields are empty and display an error message
        if (steps == 0) {
            stepsInput.setError("Please enter your steps goal");
            return false; // Stop the function if the field is empty
        }

        if (calories == 0) {
            caloriesInput.setError("Please enter your calories goal");
            return false;
        }

        return true;
    }

    private void saveGoalData(GoalsData goalData) {
        DatabaseRepository.getInstance(getContext()).insertGoalData(goalData);
    }
}