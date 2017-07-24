package ru.tcgeo.application;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.otto.Bus;

/**
 * Created by a_belov on 03.07.15.
 */
public class App extends Application {
    private static App sInstance;
    public Bitmap wktPointBitmap;
    private Bus bus;

//    private Observable<Location> locationObservable;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new Bus();
        wktPointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.measure_point);

        sInstance = this;
    }

    public Bus getEventBus() {
        return bus;
    }

//    public Observable<Location> getLocationObservable() {
//        return locationObservable;
//    }
//
//    public void setLocationObservable(Observable<Location> locationObservable) {
//        this.locationObservable = locationObservable;
//    }
}
