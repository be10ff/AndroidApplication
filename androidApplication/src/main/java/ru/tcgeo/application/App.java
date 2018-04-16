package ru.tcgeo.application;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ru.tcgeo.application.utils.SPUtils;

/**
 * Created by a_belov on 03.07.15.
 */
public class App extends Application {
    private static App sInstance;
    public Bitmap wktPointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.measure_point);
    public String dateTimeFormat = getString(R.string.date_format);
    private SPUtils sp;

//    private Observable<Location> locationObservable;

    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sp = new SPUtils(this);
    }

    public SPUtils getPreference() {
        return sp;
    }



//    public Observable<Location> getLocationObservable() {
//        return locationObservable;
//    }
//
//    public void setLocationObservable(Observable<Location> locationObservable) {
//        this.locationObservable = locationObservable;
//    }
}
