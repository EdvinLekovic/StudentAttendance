package com.example.studentattendance.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.studentattendance.enumeration.Type;

import java.time.LocalDate;
import java.util.Map;

public class Student {


    private String uId;
    private String index;
    private String name;
    private String lastName;
    private String email;
    private Map<String,Integer> subjectsListening;
    private Type type;
    private Boolean logged;
    private Location location;

    public Student(){
        logged = true;
    }


    public Student(String uId, String index, String name, String lastName, String email, Map<String, Integer> subjectsListening) {
        this.uId = uId;
        this.index = index;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.subjectsListening = subjectsListening;
        this.type = Type.STUDENT;
        this.logged = false;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
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

    public Map<String, Integer> getSubjectsListening() {
        return subjectsListening;
    }

    public void setSubjectsListening(Map<String, Integer> subjectsListening) {
        this.subjectsListening = subjectsListening;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean isLogged() {
        return logged;
    }

    public void setLogged(Boolean logged) {
        this.logged = logged;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
