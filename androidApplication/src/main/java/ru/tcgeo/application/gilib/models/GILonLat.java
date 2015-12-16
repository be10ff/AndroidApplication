package ru.tcgeo.application.gilib.models;

import android.location.Location;

public class GILonLat {

	  private double m_lon;
	  private double m_lat;

	  public double lat() 
	  {
		  return m_lat;
	  }

	  public double lon() 
	  {
		  return m_lon;
	  }

	  public GILonLat(double lon, double lat) 
	  {	
		  m_lon = lon;
		  m_lat = lat;
	  }
	  
	  public static GILonLat fromLocation(Location location)
	  {	
		  return new GILonLat(location.getLongitude(), location.getLatitude());

	  }

	public GILonLat OffsetBy (double lon, double lat)
    {
		return new GILonLat(m_lon + lon, m_lat + lat);
    }
}