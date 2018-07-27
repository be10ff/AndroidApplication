package ru.tcgeo.application.gilib.gps;


import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.LonLatEvent;
import ru.tcgeo.application.utils.MapUtils;
//todo
//https://stackoverflow.com/questions/42842092/locationlistner-no-longer-listen-for-gps-status-change-after-upgrade-to-android

public class GIGPSLocationListener implements LocationListener, GpsStatus.Listener, GpsStatus.NmeaListener {
    public static final String PROVIDER_DISABLED = "provider_disabled";
    public static final String PROVIDER_ENABLED = "provider_enabled";
    public static final int ACCEPTABLE_ACCURANCY = 60;
    public static final float ACCEPTABLE_DISTANCE = 1.5f;

    public LocationManager locationManager;

    private PublishSubject<Location> location = PublishSubject.create();
    private PublishSubject<Boolean> enabled = PublishSubject.create();
    public BehaviorSubject<Location> locationBehaviorSubject = BehaviorSubject.create();
    public BehaviorSubject<Boolean> enabledBehaviorSubject = BehaviorSubject.create();
    public BehaviorSubject<GpsStatus> statusBehaviorSubject = BehaviorSubject.create();
    public BehaviorSubject<String> nmeaBehaviorSubject = BehaviorSubject.create();
    private PublishSubject<GpsStatus> status = PublishSubject.create();
    private PublishSubject<String> nmea = PublishSubject.create();

    public GIGPSLocationListener(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);
        locationManager.addNmeaListener(this);
        locationManager.addGpsStatusListener(this);
        location.subscribe(locationBehaviorSubject);
        enabled.subscribe(enabledBehaviorSubject);
        status.subscribe(statusBehaviorSubject);
        nmea.subscribe(nmeaBehaviorSubject);
    }

    public Subject<Location> getLocation() {
        return locationBehaviorSubject;
    }

    public Observable<LonLatEvent> getLonLat() {
        return locationBehaviorSubject.map(new Function<Location, LonLatEvent>() {
            @Override
            public LonLatEvent apply(Location location) {
                LonLatEvent result = new LonLatEvent();
                result.lonlat = new GILonLat(location.getLongitude(), location.getLatitude());
                result.accurancy = location.getAccuracy();
                return result;
            }
        });
    }

    public Observable<Location> getFilteredLocation() {
        return locationBehaviorSubject.map(new Function<Location, Gain>() {
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
                return location.delta > location.location.getAccuracy() * 1.5f;
            }
        })
                .map(new Function<Gain, Location>() {
                    @Override
                    public Location apply(Gain gain) {
                        return gain.location;
                    }
                });
    }


    public void onLocationChanged(Location location) {
        this.location.onNext(location);
    }

    public void onProviderDisabled(String provider) {
        enabled.onNext(false);
    }

    public void onProviderEnabled(String provider) {
        enabled.onNext(true);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            status.onNext(locationManager.getGpsStatus(null));
        }
    }

    @Override
    public void onNmeaReceived(long l, String s) {
        nmea.onNext(s);
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
