package com.example.adityaabhiram.locamatic;

public class AddZones {
    public String date;
    public String desc;
    public String imageuri;
    public String latitude;
    public String longitude;
    public String time;
    AddZones(){


    }
    AddZones(String date,String desc,String imageuri,String latitude,String longitude,String time){
        this.date=date;
        this.desc=desc;
        this.imageuri=imageuri;
        this.latitude=latitude;
        this.longitude=longitude;
        this.time=time;
    }
}
