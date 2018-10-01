package ru.tcgeo.application;

import android.app.Application;

import ru.tcgeo.application.data.GIMap;
import ru.tcgeo.application.utils.SPUtils;
import ru.tcgeo.application.wkt.GI_WktGeometry;

/**
 * Created by a_belov on 03.07.15.
 */
public class App extends Application {
    private static App sInstance;
    //    public Bitmap wktPointBitmap /*= BitmapFactory.decodeResource(getResources(), R.drawable.measure_point)*/;
    public String dateTimeFormat /*= getString(R.string.date_format)*/;
    private SPUtils sp;
    private GIMap map;

//    private Observable<Location> locationObservable;

    public static App Instance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
//        wktPointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.measure_point);
        dateTimeFormat = getString(R.string.date_format);
        sp = new SPUtils(this);
    }

    public SPUtils getPreference() {
        return sp;
    }

    public GIMap getMap() {
        return map;
    }

    public void setMap(GIMap map) {
        this.map = map;
    }

    public GI_WktGeometry getCurrentTrack() {
        if (map != null) {
            return map.getCurrentTrack();
        }
        return null;
    }


    //    public Observable<Location> getLocationObservable() {
//        return locationObservable;
//    }
//
//    public void setLocationObservable(Observable<Location> locationObservable) {
//        this.locationObservable = locationObservable;
//    }
}
