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
    private Bus bus;
    public Bitmap wktPointBitmap;

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

    public static App getInstance(){
        return sInstance;
    }

}
