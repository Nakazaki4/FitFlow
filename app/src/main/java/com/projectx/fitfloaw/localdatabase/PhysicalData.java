package com.projectx.fitfloaw.localdatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "physical_data")
public class PhysicalData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int age;
    private String gender;
    private double height;

    // Constructors, getters, and setters

    public PhysicalData() {
    }

    public PhysicalData(int age, String gender, double height) {
        this.age = age;
        this.gender = gender;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
