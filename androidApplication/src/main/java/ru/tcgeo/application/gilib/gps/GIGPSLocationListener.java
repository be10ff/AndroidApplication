package ru.tcgeo.application.gilib.gps;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.wkt.GI_WktPoint;
import rx.Notification;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

public class GIGPSLocationListener implements LocationListener 
{
	public static final String PROVIDER_DISABLED = "provider_disabled";
	public static final String PROVIDER_ENABLED = "provider_enabled";
	public static final int ACCEPTABLE_ACCURANCY = 60;
	public static final float ACCEPTABLE_DISTANCE = 1.5f;

	public LocationManager locationManager;

	private PublishSubject<Location> location = PublishSubject.create();
	private PublishSubject<Boolean> enabled = PublishSubject.create();

	public GIGPSLocationListener(Context context){
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	2000, 5, this);
//		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 5, this);

//		App.getInstance().setLocationObservable(location.asObservable());
	}

	public Observable<Location> getLocation(){
		return location.asObservable();
	}

	public Observable<GILonLat> getLonLat(){
		return location.asObservable().map(new Func1<Location, GILonLat>() {
			@Override
			public GILonLat call(Location location) {
				return new GILonLat(location.getLongitude(), location.getLatitude());
			}
		});
	}

	public Observable<Location> getFilteredLocation(){
		return location.map(new Func1<Location, Gain>() {
			@Override
			public Gain call(Location location) {
				return new Gain(0, location);
			}
		}).scan(new Func2<Gain, Gain, Gain>() {
			@Override
			public Gain call(Gain gain, Gain last) {
				GILonLat origin = new GILonLat(gain.location.getLongitude(), gain.location.getLatitude());
				GILonLat current = new GILonLat(last.location.getLongitude(), last.location.getLatitude());
				last.delta = MapUtils.GetDistance(origin, current);
				return last;
			}
		}).filter(new Func1<Gain, Boolean>() {
			@Override
			public Boolean call(Gain location) {
				return location.delta > location.location.getAccuracy()*1.5f;
			}
		}).map(new Func1<Gain, Location>() {
			@Override
			public Location call(Gain gain) {
				return gain.location;
			}
		});
	}


	public void onLocationChanged(Location location) 
	{
		// Assuming we get wgs84 coordinates
		GIEditLayersKeeper.Instance().onGPSLocationChanged(location);

		this.location.onNext(location);
	}

	class Gain{
		Gain(double delta, Location location){
			this.delta = delta;
			this.location = location;
		}
		double delta;
		Location location;
	}

	public void onProviderDisabled(String provider) 
	{
		enabled.onNext(false);
	}

	public void onProviderEnabled(String provider) 
	{
		enabled.onNext(true);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) 
	{

	}
}
