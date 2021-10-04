package com.example.studentattendance.model;

public class Location {

    double longitude;
    double latitude;
    String address;
    String locality;

    public Location(){};

    public Location(double longitude, double latitude, String address, String locality) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.locality = locality;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}
