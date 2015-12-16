package ru.tcgeo.application.gilib.gps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.NmeaListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.tcgeo.application.R;

public class GIGPSButtonView extends RelativeLayout 
{
	Bitmap m_btn_on;
	Bitmap m_btn_off;
	Bitmap m_btn_disable;
	int m_count;
	float m_accurancy;
	Paint m_paint_text;
	TextView m_textViewAccurancy;
	public ImageView m_StatusImage;
	private View m_LayoutView;
	Context m_context;
	private boolean blink;
	
	private LocationManager locationManager;
	  private LocationListener locationListener = new LocationListener() {
	
		    public void onLocationChanged(Location location) 
		    {
		    	m_accurancy = location.getAccuracy();
		    	m_textViewAccurancy.setText(String.format("±%02d m", (int)m_accurancy));
		    	blink = !blink;
		    	if(blink)
		    	{
		    		m_textViewAccurancy.setTextColor(Color.argb(255, 63, 255, 63));
		    	}
		    	else
		    	{
		    		m_textViewAccurancy.setTextColor(Color.argb(255, 191, 63, 0));
		    	}
		    }
	
	
		    public void onProviderDisabled(String provider) 
		    {
		    	SetGPSEnabledStatus(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
		    }
	
	
		    public void onProviderEnabled(String provider) 
		    {
		    	SetGPSEnabledStatus(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
		    }
	
	
		    public void onStatusChanged(String provider, int s, Bundle extras) 
		    {
		    }
	
		  };
		  private GpsStatus.Listener lGPS = new GpsStatus.Listener() 
		  {
		        public void onGpsStatusChanged(int event) 
		        {
		            if( event == GpsStatus.GPS_EVENT_SATELLITE_STATUS)
		            {
		                GpsStatus status = locationManager.getGpsStatus(null);
		                m_count = 0;
		                Iterable<GpsSatellite> sats = status.getSatellites();
		                for(GpsSatellite sat : sats)
		                {
		                	m_count++;
		                }
		            }
		        }
		  };
		  private NmeaListener lNmea = new NmeaListener() {

			public void onNmeaReceived(long timestamp, String nmea) 
			{
				
				if(m_accurancy < 15 || m_count > 5)
				{
					ShowGPSStatus(1);
				}
				else
				{
					int[] res = ParseNmea(nmea);
					ShowGPSStatus(res[1]);
				}
			}
		};
		
	public GIGPSButtonView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_LayoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this, true);
		Init(context);
	}
	public GIGPSButtonView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_LayoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this, true);
		Init(context);
	}
	public GIGPSButtonView(Context context) 
	{
		super(context);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_LayoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this, true);
		Init(context);
	}
	private void Init(Context context)
	{
		m_context = context;
		m_textViewAccurancy = (TextView)findViewById(R.id.textViewAccurancy);
		m_StatusImage = (ImageView)findViewById(R.id.imageViewStatus);
		m_StatusImage.setImageResource(R.drawable.gps_disabeled);
		m_textViewAccurancy.setText("-- m");
		blink = false;
//		GIEditLayersKeeper.Instance().setGPSButton(this);

		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}


	public void onResume()
	{

	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000 * 10, 10, locationListener);
        locationManager.addGpsStatusListener(lGPS);
        locationManager.addNmeaListener(lNmea);
        SetGPSEnabledStatus(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));	
	}

	public void onPause()
	{
		locationManager.removeUpdates(locationListener);
	}


	public void SetGPSEnabledStatus(boolean enabeled)
	{
		if(enabeled)
		{
			m_StatusImage.setImageDrawable(getResources().getDrawable(R.drawable.gps_out_of_service));				
		}
		else
		{
			m_StatusImage.setImageDrawable(getResources().getDrawable(R.drawable.gps_disabeled));				
			m_textViewAccurancy.setText("-- m");
		}
	}
	public void ShowGPSStatus(int status)
	{
		switch(status)
		{
			case 0:
			{
				m_StatusImage.setImageDrawable(getResources().getDrawable(R.drawable.gps_out_of_service));
				break;
			}
			case 2:
			{
				m_StatusImage.setImageDrawable(getResources().getDrawable(R.drawable.gps_unavaliable));
				break;
			}
			case 1:
			{
				m_StatusImage.setImageDrawable(getResources().getDrawable(R.drawable.gps_avaliable));
				break;
			}

		}

	}

	public int[] ParseNmea(String nmea)
	{
		/*
 *      1         Fix quality: 0 = invalid
                   1 = GPS fix (SPS)
                   2 = DGPS fix
                   3 = PPS fix
			       4 = Real Time Kinematic
			       5 = Float RTK
                   6 = estimated (dead reckoning) (2.3 feature)
			       7 = Manual input mode
			       8 = Simulation mode
		 */
		String[] fields = nmea.split(",");
		int quality = 0;
		int count = 0;
		
		if(fields != null)
		{
			if(fields.length > 0)
			{
				if(fields[0].equalsIgnoreCase("$GPGGA"))
				{
					if(!fields[7].equals(""))
					{
						count = Integer.valueOf(fields[7]);
					}
					if(!fields[6].equals(""))
					{
						quality = Integer.valueOf(fields[6]);
					}
				}
				/*if(fields[0].equalsIgnoreCase("$GPGSA"))
				{
					count = 0;
					if(!fields[2].equals(""))
					{
						if(Integer.valueOf(fields[2]) > 1)
						{
							quality = 1;
						}
					}
					else
					{
						quality = 2;
					}
				}
				if(fields[0].equalsIgnoreCase("$GPGSV"))
				{
					if(!fields[3].equals(""))
					{
						count = Integer.valueOf(fields[3]);
					}
					quality = 2;
				}
				if(fields[0].equalsIgnoreCase("$GPRMC"))
				{
					count = 0;
					quality = 1;
				}
				if(fields[0].equalsIgnoreCase("$GPGLL"))
				{
					count = 0;
					quality = 1;
				}
				if(fields[0].equalsIgnoreCase("$GPGLL"))
				{
					count = 0;
					quality = 1;
				}*/

			}
			
		}
		int[] res = new int[2];
		res[0] = count;
		res[1] = quality;
		return res;
	}
}
