package ru.tcgeo.application.gilib.gps;


import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import ru.tcgeo.application.wkt.GI_WktLinestring;
import ru.tcgeo.application.wkt.GI_WktPoint;

public class MockLocationProvider extends Thread 
{
    private GI_WktLinestring data;

    private LocationManager locationManager;

    private String mocLocationProvider;

    private String LOG_TAG = "faren";

    public MockLocationProvider(LocationManager locationManager,  String mocLocationProvider, GI_WktLinestring data)
    {

        this.locationManager = locationManager;
        this.mocLocationProvider = mocLocationProvider;
        this.data = data;
    }

    @Override
    public void run() {

        for(int i = 0; i < data.m_points.size(); i++)
    	//for (GI_WktPoint point : data.m_points) 
        	{
        	GI_WktPoint point = data.m_points.get(i);
        	if(i ==  data.m_points.size() - 1)
        	{
        		i = 0;
        	}

            try {

                Thread.sleep(10000);

            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            // Set one position
            Double latitude = point.m_lat;
            Double longitude =point.m_lon;
            Double altitude = 200.0 + Math.random()*10;
            Location location = new Location(mocLocationProvider);

            location.setLatitude(latitude + Math.random()*0.0001);
            location.setLongitude(longitude + Math.random()*0.0001);
            location.setAltitude(altitude);

            Log.e(LOG_TAG, location.toString());

            // set the time in the location. If the time on this location
            // matches the time on the one in the previous set call, it will be
            // ignored
            location.setTime(System.currentTimeMillis());

            locationManager.setTestProviderLocation(mocLocationProvider,
                    location);
        }
    }
}
