package com.example.bus_on_line;

public class DataModel {

    String BusId;
    double longitude;
    double latitude;
    String malecount;
    String femalecount;

    public DataModel(double longitude, double latitude, String malecount, String femalecount,String BusId) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.malecount = malecount;
        this.femalecount = femalecount;
        this.BusId=BusId;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getMalecount() {
        return malecount;
    }

    public String getFemalecount() {
        return femalecount;
    }

    public String getBusId() {
        return BusId;
    }
}
