package ru.tcgeo.application.control;

/**
 * текущее положение и направление
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.widget.RelativeLayout;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.data.GIGPSLocationListener;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.LonLatEvent;

public class GIPositionControl extends View implements GIControl {

    protected CompositeDisposable subscription = new CompositeDisposable();
    int[] map_location = {0, 0};
    Bitmap image;
    Matrix matrix;
    Point current_pos_on_screen;
    private GIMap m_map;
    private GIGPSLocationListener locationListener;
    private RelativeLayout m_root;
    private GILonLat m_CurrentPosition;
    private GILonLat m_OriginPosition;

    public GIPositionControl(Context context, GIMap map, GIGPSLocationListener locationListener) {
        super(context);
        m_root = (RelativeLayout) map.getParent();
        m_root.addView(this);
        bringToFront();
        image = BitmapFactory.decodeResource(getResources(), R.drawable.position_arrow);
        setMap(map);
        matrix = new Matrix();

        this.locationListener = locationListener;
        subscription.add(locationListener.getPositionObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LonLatEvent>() {
                    @Override
                    public void accept(LonLatEvent lonLatEvent) {
                        setLonLat(lonLatEvent.lonlat);
                    }
                }));


    }

    public void setMap(GIMap map) {
        m_map = map;
        map.registerGIControl(this);
        int[] screen_location = {0, 0};
        m_map.getLocationInWindow(screen_location);

        m_map.getLocationOnScreen(map_location);
        map_location[0] -= image.getHeight() / 2;
        map_location[1] -= image.getWidth() / 2 + m_map.getOffsetY();
    }

    public void onMapMove() {
        if (m_CurrentPosition != null) {
            MoveTo(m_map.MapToScreenTempo(m_CurrentPosition));
        }

    }

    public void onViewMove() {
        if (m_CurrentPosition != null) {
            MoveTo(m_map.MapToScreenTempo(m_CurrentPosition));
        }
    }

    public void afterMapFullRedraw(GIBounds bounds, Rect view_rect) {
    }

    public void afterMapImageRedraw(GIBounds bounds, Rect view_rect) {
    }

    public void onMarkerLayerRedraw(Rect view_rect) {
    }

    public void afterViewRedraw() {
    }

    public void MoveTo(Point point) {
        m_map.getLocationOnScreen(map_location);
        map_location[0] -= image.getHeight() / 2;
        map_location[1] -= image.getWidth() / 2 + m_map.getOffsetY();

        current_pos_on_screen = point;
        setX(point.x + map_location[0]);
        setY(point.y + map_location[1]);
        invalidate();
    }

    public void setLonLat(GILonLat lonlat) {
        if (m_CurrentPosition != null)// && m_CurrentPosition != m_OriginPosition)
        {
            m_OriginPosition = m_CurrentPosition;
        }
        m_CurrentPosition = lonlat;
        onViewMove();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        double direction = -Math.PI / 2;

        if (m_OriginPosition != null) {
            double hypot = Math.hypot(m_CurrentPosition.lon() - m_OriginPosition.lon(), m_CurrentPosition.lat() - m_OriginPosition.lat());
            if (hypot != 0) {
                double dir_cos = (m_CurrentPosition.lon() - m_OriginPosition.lon()) / hypot;
                double dir_sin = (m_CurrentPosition.lat() - m_OriginPosition.lat());
                direction = Math.acos(dir_cos);
                if (dir_sin > 0) {
                    direction = -direction;
                }
            }
        }

        direction = Math.toDegrees(direction);
        matrix.reset();
        //matrix.setTranslate(m_accurancy/2, m_accurancy/2);
        matrix.setRotate((float) direction, image.getWidth() / 2, image.getHeight() / 2);
        canvas.drawBitmap(image, matrix, null);
//		bringToFront();
    }
}
