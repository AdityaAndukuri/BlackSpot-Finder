package com.example.adityaabhiram.locamatic;


public class SavedZones {
    public double latitude;
    public double longitude;
    public String name;
  //  public int onoff;
    public int radius;
    public String address;
    public int velocity;
  //  public int image;
    SavedZones()
    {

    }
    SavedZones(double latitude, double longitude, String name, int radius, String address,int velocity)
    {
        this.latitude=latitude;
        this.longitude=longitude;
        this.name=name;
       // this.onoff=1;
        this.radius=radius;
        this.address=address;
        this.velocity = velocity;
        //this.altitude = altitude;
      //  this.image=image;
    }

   // public double getAltitude() {
 //       return altitude;
   // }

    public String getAddress() {
        return address;

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }
   // public void setOnoff(int onoff)
    //{
      //  this.onoff=onoff;
    //}
    //public int getOnoff()
    //{
      //  return onoff;
    //}

    public int getRadius() {
        return radius;
    }

public int getvelocity(){
    return velocity;
}

}