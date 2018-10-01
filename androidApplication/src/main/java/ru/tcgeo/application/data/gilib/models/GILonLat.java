package ru.tcgeo.application.gilib.models;

import android.location.Location;

public class GILonLat {

	  private double lon;
	  private double lat;

	  public double lat() 
	  {
		  return lat;
	  }

	  public double lon() 
	  {
		  return lon;
	  }

	  public GILonLat(double lon, double lat) 
	  {	
		  this.lon = lon;
		  this.lat = lat;
	  }
	  
	  public static GILonLat fromLocation(Location location)
	  {	
		  return new GILonLat(location.getLongitude(), location.getLatitude());

	  }

	public void setLon(double lon) {
		this.lon = lon;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public GILonLat OffsetBy (double lon, double lat)
    {
		return new GILonLat(this.lon + lon, this.lat + lat);
    }
}