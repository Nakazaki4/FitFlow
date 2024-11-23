package com.projectx.fitfloaw.firebase;

public class User {
    private int uid;
    private String name;
    private String email;
    private String gender;
    private int age;
    private int height;

    public User(){

    }

    public User(int uid, String name, String email, String gender, int age, int height) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.height = height;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
