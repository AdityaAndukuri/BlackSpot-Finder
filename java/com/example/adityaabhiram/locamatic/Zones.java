package com.example.adityaabhiram.locamatic;

public class Zones {
 private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitutde() {
        return longitutde;
    }

    public void setLongitutde(double longitutde) {
        this.longitutde = longitutde;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String time;
    private String imageuri;
    private double latitude;
 private double longitutde;
 private String desc;




 public Zones(){}
 public Zones(String date, String time, double latitude, double longitutde, String desc, String imageuri){

      this.date=date;
      this.time=time;
      this.latitude=latitude;
      this.longitutde=longitutde;
      this.desc=desc;
      this.imageuri=imageuri;
 }

}
