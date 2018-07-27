package ru.tcgeo.application.gilib.gps;

import android.content.Context;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;

public class GIGPSButtonView extends RelativeLayout 
{
	@BindView(R.id.imageViewStatus)
	public ImageView ivStatus;
	int speed;
	@BindView(R.id.textViewAccurancy)
	TextView m_textViewAccurancy;
	@BindView(R.id.tvSpeed)
	TextView tvSpeed;
	private View layoutView;
	private boolean blink;
	protected CompositeDisposable subscription = new CompositeDisposable();

	GIGPSLocationListener locationListener;

//	private LocationListener locationListener = new LocationListener() {
//
//		public void onLocationChanged(Location location) {
//			accurancy = location.getAccuracy();
//			m_textViewAccurancy.setText(String.format("±%02d m", (int) accurancy));
//			blink = !blink;
//			if (blink) {
//				m_textViewAccurancy.setTextColor(Color.argb(255, 63, 255, 63));
//			} else {
//				m_textViewAccurancy.setTextColor(Color.argb(255, 191, 63, 0));
//			}
//
//			speed = (int) Math.round(3.6 * location.getSpeed());
//			if (speed < 10) {
//				speed = 0;
//				tvSpeed.setVisibility(INVISIBLE);
//			} else {
//				tvSpeed.setVisibility(VISIBLE);
//				tvSpeed.setText(/*String.format("%03d", speed)*/String.valueOf(speed));
//			}
//		}
//
//
//		public void onProviderDisabled(String provider) {
//			SetGPSEnabledStatus(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
//		}
//
//
//		public void onProviderEnabled(String provider) {
//			SetGPSEnabledStatus(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
//		}
//
//
//		public void onStatusChanged(String provider, int s, Bundle extras) {
//		}
//
//	};


//	private GpsStatus.Listener lGPS = new GpsStatus.Listener() {
//		public void onGpsStatusChanged(int event) {
//			if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
//				GpsStatus status = locationManager.getGpsStatus(null);
//				countSatellite = 0;
//				Iterable<GpsSatellite> sats = status.getSatellites();
//				for (GpsSatellite sat : sats) {
//					countSatellite++;
//				}
//			}
//		}
//	};
//	private NmeaListener lNmea = new NmeaListener() {
//
//		public void onNmeaReceived(long timestamp, String nmea) {
//
//			if (accurancy < 15 || countSatellite > 5) {
//				ShowGPSStatus(1);
//			} else {
//				int[] res = ParseNmea(nmea);
//				ShowGPSStatus(res[1]);
//			}
//		}
//	};

	public GIGPSButtonView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this);
		Init(context);
	}
	public GIGPSButtonView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this);
		Init(context);
	}
	public GIGPSButtonView(Context context) 
	{
		super(context);
		LayoutInflater m_LayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutView = m_LayoutInflater.inflate(R.layout.gps_button_layout, this);
		Init(context);
	}
	private void Init(Context context)
	{
		ButterKnife.bind(this, layoutView);
		ivStatus.setImageResource(R.drawable.gps_disabeled);
		m_textViewAccurancy.setText("-- m");
		tvSpeed.setVisibility(INVISIBLE);
		blink = false;

		locationListener = ((Geoinfo) context).locationListener;
		subscription.add(locationListener.enabledBehaviorSubject
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(Boolean enabeled) {
						if (enabeled) {
							ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_out_of_service));
						} else {
							ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_disabeled));
							m_textViewAccurancy.setText("-- m");
						}
					}
				}));

		subscription.add(Observable.combineLatest(locationListener.statusBehaviorSubject.hide(), locationListener.nmeaBehaviorSubject.hide(), locationListener.locationBehaviorSubject
				, new Function3<GpsStatus, String, Location, Integer>() {
					@Override
					public Integer apply(GpsStatus gpsStatus, String nmea, Location location) {
						int countSatellite = 0;
						Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
						for (GpsSatellite sat : sats) {
							countSatellite++;
						}

						if (location.getAccuracy() < 15 || countSatellite > 5) {
							return 1;
						} else {
							int[] res = ParseNmea(nmea);
							return (res[1]);
						}
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Integer>() {
					@Override
					public void accept(Integer integer) {
						ShowGPSStatus(integer);
					}
				}));


		subscription.add(locationListener.getLocation()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Location>() {
					@Override
					public void accept(Location location) {
						float accurancy = location.getAccuracy();
						m_textViewAccurancy.setText(String.format("±%02d m", (int) accurancy));
						blink = !blink;
						if (blink) {
							m_textViewAccurancy.setTextColor(Color.argb(255, 63, 255, 63));
						} else {
							m_textViewAccurancy.setTextColor(Color.argb(255, 191, 63, 0));
						}

						speed = (int) Math.round(3.6 * location.getSpeed());
						if (speed < 10) {
							speed = 0;
							tvSpeed.setVisibility(INVISIBLE);
						} else {
							tvSpeed.setVisibility(VISIBLE);
							tvSpeed.setText(/*String.format("%03d", speed)*/String.valueOf(speed));
						}
					}
				})
		);
	}



	public void SetGPSEnabledStatus(boolean enabeled)
	{
		if(enabeled)
		{
			ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_out_of_service));
		}
		else
		{
			ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_disabeled));
			m_textViewAccurancy.setText("-- m");
		}
	}
	public void ShowGPSStatus(int status)
	{
		switch(status)
		{
			case 0:
			{
				ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_out_of_service));
				break;
			}
			case 2:
			{
				ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_unavaliable));
				break;
			}
			case 1:
			{
				ivStatus.setImageDrawable(getResources().getDrawable(R.drawable.gps_avaliable));
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
