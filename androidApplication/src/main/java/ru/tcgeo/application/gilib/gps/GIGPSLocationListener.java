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
import io.reactivex.functions.Function3;
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
    private BehaviorSubject<Location> locationBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> enabledBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<GpsStatus> statusBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<String> nmeaBehaviorSubject = BehaviorSubject.create();
    private PublishSubject<GpsStatus> status = PublishSubject.create();
    private PublishSubject<String> nmea = PublishSubject.create();

    private Observable<LonLatEvent> positionObservable = Observable.empty();
    private PublishSubject<Integer> runningSubject = PublishSubject.create();
    private PublishSubject<Integer> trackSubject = PublishSubject.create();
    private PublishSubject<Integer> followSubject = PublishSubject.create();

    private Observable<LonLatEvent> runningObservable = Observable.empty();
    private Observable<LonLatEvent> trackObservable = Observable.empty();
    private Observable<LonLatEvent> followObservable = Observable.empty();

    public GIGPSLocationListener(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        locationManager.addNmeaListener(this);
        locationManager.addGpsStatusListener(this);
        location.subscribe(locationBehaviorSubject);
        enabled.subscribe(enabledBehaviorSubject);
        status.subscribe(statusBehaviorSubject);
        nmea.subscribe(nmeaBehaviorSubject);

        //position control
        positionObservable = Observable.combineLatest(getLonLat(), runningSubject.hide(),
                new BiFunction<LonLatEvent, Integer, LonLatEvent>() {
                    @Override
                    public LonLatEvent apply(LonLatEvent giLonLat, Integer running) {
                        giLonLat.actions = 0 | running;
                        return giLonLat;
                    }
                })
                .filter(new Predicate<LonLatEvent>() {
                    @Override
                    public boolean test(LonLatEvent lonLatEvent) {
                        return (lonLatEvent.actions & LonLatEvent.FLAG_RUNNING) != 0;
                    }
                });

        //writing track
        trackObservable = Observable.combineLatest(getLonLat(), trackSubject.hide(),
                new BiFunction<LonLatEvent, Integer, LonLatEvent>() {
                    @Override
                    public LonLatEvent apply(LonLatEvent lonLatEvent, Integer track) {
                        lonLatEvent.actions = 0 | track;
                        return lonLatEvent;
                    }
                })
                .filter(new Predicate<LonLatEvent>() {
                    @Override
                    public boolean test(LonLatEvent lonLatEvent) {
                        return (lonLatEvent.actions & LonLatEvent.FLAG_TRACK) != 0;
                    }
                });

        //autofollow

        followObservable = Observable.combineLatest(
                getLonLat(),
                followSubject.hide(),
                runningSubject.hide(),
                new Function3<LonLatEvent, Integer, Integer, LonLatEvent>() {
                    @Override
                    public LonLatEvent apply(LonLatEvent lonLatEvent, Integer follow, Integer running) {
                        lonLatEvent.actions = 0 | follow;
                        lonLatEvent.actions = lonLatEvent.actions | running;
                        return lonLatEvent;
                    }
                })
                .filter(new Predicate<LonLatEvent>() {
                    @Override
                    public boolean test(LonLatEvent lonLatEvent) {
                        return (lonLatEvent.actions & LonLatEvent.FLAG_FOLLOW) != 0 && (lonLatEvent.actions & LonLatEvent.FLAG_RUNNING) != 0;
                    }
                });

    }

    public Subject<Location> getLocation() {
        return locationBehaviorSubject;
    }

    public BehaviorSubject<GpsStatus> getStatusBehaviorSubject() {
        return statusBehaviorSubject;
    }

    public PublishSubject<GpsStatus> getStatus() {
        return status;
    }

    public PublishSubject<Integer> getRunningSubject() {
        return runningSubject;
    }

    public PublishSubject<Integer> getTrackSubject() {
        return trackSubject;
    }

    public Observable<LonLatEvent> getPositionObservable() {
        return positionObservable;
    }

    public PublishSubject<Integer> getFollowSubject() {
        return followSubject;
    }

    public Observable<LonLatEvent> getLonLat() {
        return locationBehaviorSubject.map(new Function<Location, LonLatEvent>() {
            @Override
            public LonLatEvent apply(Location location) {
                LonLatEvent result = new LonLatEvent();
                result.lonlat = new GILonLat(location.getLongitude(), location.getLatitude());
                result.accurancy = location.getAccuracy();
                result.actions = 0;
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

    public Observable<LonLatEvent> getRunningObservable() {
        return runningObservable;
    }

    public Observable<LonLatEvent> getTrackObservable() {
        return trackObservable;
    }

    public Observable<LonLatEvent> getFollowObservable() {
        return followObservable;
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

    public BehaviorSubject<Boolean> getEnabledBehaviorSubject() {
        return enabledBehaviorSubject;
    }

    public BehaviorSubject<String> getNmeaBehaviorSubject() {
        return nmeaBehaviorSubject;
    }

    public BehaviorSubject<Location> getLocationBehaviorSubject() {
        return locationBehaviorSubject;
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
