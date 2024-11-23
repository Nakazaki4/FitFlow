package com.projectx.fitfloaw.settings;

public class CalorieCalculator {

    // Constants
    private static final double BASE_RATE_PER_1000_STEPS = 35.0;
    private static final double AGE_FACTOR_THRESHOLD = 30;
    private static final double AGE_FACTOR_RATE = 0.005;
    private static final double GENDER_FACTOR_MALE = 1.0;
    private static final double GENDER_FACTOR_FEMALE = 0.9;
    private static final double HEIGHT_FACTOR_BASE = 170.0;

    private static int age;
    private static String gender;
    private static double heightCm;


    public CalorieCalculator() {
    }

    public static void setAge(int age) {
        CalorieCalculator.age = age;
    }

    public static void setGender(String gender) {
        CalorieCalculator.gender = gender;
    }

    public static void setHeightCm(double heightCm) {
        CalorieCalculator.heightCm = heightCm;
    }

    /**
     * Calculates calories burned for a given number of steps.
     *
     * @param steps Number of steps taken
     * @return Estimated calories burned
     */
    public static double calculateCaloriesBurned(int steps) {
        double caloriesPer1000Steps = calculateCaloriesPer1000Steps(age, gender, heightCm);
        return (steps / 1000.0) * caloriesPer1000Steps;
    }

    /**
     * Calculates calories burned per 1000 steps.
     *
     * @param age      Age of the person in years
     * @param gender   Gender of the person ("male" or "female")
     * @param heightCm Height of the person in centimeters
     * @return Estimated calories burned per 1000 steps
     */
    public static double calculateCaloriesPer1000Steps(int age, String gender, double heightCm) {
        double ageFactor = calculateAgeFactor(age);
        double genderFactor = calculateGenderFactor(gender);
        double heightFactor = calculateHeightFactor(heightCm);

        return BASE_RATE_PER_1000_STEPS * ageFactor * genderFactor * heightFactor;
    }

    private static double calculateAgeFactor(int age) {
        return (age > AGE_FACTOR_THRESHOLD) ? 1 - (AGE_FACTOR_RATE * (age - AGE_FACTOR_THRESHOLD)) : 1;
    }

    private static double calculateGenderFactor(String gender) {
        return gender.toLowerCase().equals("male") ? GENDER_FACTOR_MALE : GENDER_FACTOR_FEMALE;
    }

    private static double calculateHeightFactor(double heightCm) {
        return heightCm / HEIGHT_FACTOR_BASE;
    }
}
