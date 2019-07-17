package com.example.adityaabhiram.locamatic;

public class SpotsNew {
    public String name="";
    public String address="";
    public String date="";
    public String time="";
    public String image="";
    public String desc="";
    public int radius=0;
    public int velocity=0;
    public double latitude=0;
    public double longitude=0;
    public boolean resolve=false;
    public boolean notify=false;
    SpotsNew(String name,String adress,String date,String time,String image,String desc,int radius,int velocity,double latitude,double longitude,boolean resolve,boolean notify)
    {
        this.name=name;
        this.address=address;
        this.date=date;
        this.time=time;
        this.image=image;
        this.desc=desc;
        this.radius=radius;
        this.velocity=velocity;
        this.latitude=latitude;
        this.longitude=longitude;
        this.resolve=resolve;
        this.notify=notify;
    }
}
