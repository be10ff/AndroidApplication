package ru.tcgeo.application.gilib.gps;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.views.callback.LocationCallback;


public class GIGPSLocationListener implements LocationListener 
{
	public static final String PROVIDER_DISABLED = "provider_disabled";
	public static final String PROVIDER_ENABLED = "provider_enabled";
	public static final int ACCEPTABLE_ACCURANCY = 60;
	public static final float ACCEPTABLE_DISTANCE = 1.5f;

	public LocationManager locationManager;
	private LocationCallback callback;

	private PublishSubject<Location> location = PublishSubject.create();
	private PublishSubject<Boolean> enabled = PublishSubject.create();

	public GIGPSLocationListener(Context context, LocationCallback callback) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	2000, 5, this);
        this.callback = callback;
//		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 5, this);

//		App.Instance().setLocationObservable(location.asObservable());
    }

    public Subject<Location> getLocation() {
        return location;
	}

	public Observable<GILonLat> getLonLat(){
        return location.map(new Function<Location, GILonLat>() {
			@Override
            public GILonLat apply(Location location) {
				return new GILonLat(location.getLongitude(), location.getLatitude());
			}
		});
	}

	public Observable<Location> getFilteredLocation(){
        return location.map(new Function<Location, Gain>() {
			@Override
            public Gain apply(Location location) {
				return new Gain(0, location);
			}
        }).scan(new BiFunction<Gain, Gain, Gain>() {
			@Override
            public Gain apply(Gain gain, Gain last) {
				GILonLat origin = new GILonLat(gain.location.getLongitude(), gain.location.getLatitude());
				GILonLat current = new GILonLat(last.location.getLongitude(), last.location.getLatitude());
				last.delta = MapUtils.GetDistance(origin, current);
				return last;
			}
        }).filter(new Predicate<Gain>() {
			@Override
            public boolean test(Gain location) {
				return location.delta > location.location.getAccuracy()*1.5f;
			}
        })
                .map(new Function<Gain, Location>() {
			@Override
            public Location apply(Gain gain) {
				return gain.location;
			}
		});
	}


	public void onLocationChanged(Location location) 
	{
		// Assuming we get wgs84 coordinates
		callback.onLocationChanged(location);
//		callback.onGPSLocationChanged(location);
        this.location.onNext(location);
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

	class Gain {
		double delta;
		Location location;

		Gain(double delta, Location location) {
			this.delta = delta;
			this.location = location;
		}
	}
}
