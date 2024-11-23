package com.projectx.fitfloaw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.projectx.fitfloaw.R;
import com.projectx.fitfloaw.localdatabase.DatabaseRepository;
import com.projectx.fitfloaw.localdatabase.PhysicalData;

public class PhysicalInformationFragment extends Fragment {
    private TextInputEditText ageInput;
    private TextInputEditText heightInput;
    private RadioGroup genderRadioGroup;
    private Button saveButton;


    public PhysicalInformationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_information, container, false);
        uiElements(view);
        setToolbar(view);
        return view;
    }

    private void uiElements(View view) {
        ageInput = view.findViewById(R.id.ageInput);
        heightInput = view.findViewById(R.id.heightInput);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        saveButton = view.findViewById(R.id.saveButton);
        saveButton();
    }

    private void saveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input values as Strings
                String age = ageInput.getText().toString().trim();
                String height = heightInput.getText().toString().trim();
                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                // Check if any fields are empty and display an error message

                if (inputsCheck(age, height, selectedGenderId, view)){
                    savePhysicalData(new PhysicalData(Integer.getInteger(age),
                            selectedGenderId == R.id.maleRadioButton ? "male" : "female",
                            Double.parseDouble(height)));
                    Toast.makeText(getContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    private boolean inputsCheck(String age, String height, int selectedGenderId, View view) {
        // Check if any fields are empty and display an error message
        if (age.isEmpty()) {
            ageInput.setError("Please enter your age");
            return false; // Stop the function if the field is empty
        }

        if (height.isEmpty()) {
            heightInput.setError("Please enter your height");
            return false;
        }

        if (selectedGenderId == -1) { // No radio button selected
            Toast.makeText(view.getContext(), "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void savePhysicalData(PhysicalData data) {
        DatabaseRepository.getInstance(getContext()).insertPhysicalData(data);
    }

    private void setToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_fragment);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Personal Information");  // Set the title text
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
}