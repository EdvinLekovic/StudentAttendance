package com.example.studentattendance.model;

import android.os.Parcelable;

import com.example.studentattendance.enumeration.Type;

import java.util.List;

public class Professor {

    private String uId;
    private String name;
    private String lastName;
    private String email;
    private List<String> subjectsTeaching;
    private Type type;
    private Location location;

    public Professor(){}

    public Professor(String uId, String name, String lastName,String email, List<String> subjectsTeaching) {
        this.uId = uId;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.subjectsTeaching = subjectsTeaching;
        this.type = Type.PROFESSOR;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSubjectsTeaching() {
        return subjectsTeaching;
    }

    public void setSubjectsTeaching(List<String> subjectsTeaching) {
        this.subjectsTeaching = subjectsTeaching;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
