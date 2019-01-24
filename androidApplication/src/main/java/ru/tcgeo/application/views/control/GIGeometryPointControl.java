package ru.tcgeo.application.views.control;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.data.GIMap;
import ru.tcgeo.application.data.gilib.models.GIBounds;
import ru.tcgeo.application.data.gilib.models.GILonLat;
import ru.tcgeo.application.data.wkt.GI_WktPoint;
import ru.tcgeo.application.views.dialog.GILonLatInputDialog;

public class GIGeometryPointControl extends LinearLayout implements GIControl, OnClickListener, OnLongClickListener {

    public GI_WktPoint m_WKTPoint;
    boolean hasClosed;
    int[] map_location = {0, 0};
    ToggleButton m_button;
    View m_LayoutView;
    int[] m_offset;
    private GIMap m_map;
    private GILonLat m_PointOriginMap;
    private Context m_context;

    public GIGeometryPointControl(Context context, GIMap map) {
        super(context);
        m_context = context;
        LayoutInflater m_LayoutInflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_LayoutView = m_LayoutInflater.inflate(R.layout.geometry_editing_point_control, this);
        m_button = (ToggleButton) m_LayoutView.findViewById(R.id.point_image);
        m_button.setBackgroundResource(R.drawable.edit_point);


        int size = m_context.getResources().getDimensionPixelSize(R.dimen.editing_poi_size);
        m_offset = new int[]{size / 2, size / 2};

        m_button.setEnabled(false);
        m_button.setClickable(false);
        setEnabled(false);
        setClickable(false);
        setMap(map);
    }

    public boolean getChecked() {
        if (m_button != null) {
            return m_button.isChecked();
        }
        return false;
    }

    public void setChecked(boolean checked) {
        if (m_button != null) {
            m_button.setChecked(checked);
        }
    }

    public void setActiveStatus(boolean active) {
        //TODO something wrong with removing listeners... may be
        if (active) {
            m_button.setBackgroundResource(R.drawable.point_selection_status);
            m_button.setOnClickListener(this);
            m_button.setOnLongClickListener(this);
            m_button.setEnabled(true);
            m_button.setClickable(true);
            m_button.setFocusable(true);
            setEnabled(true);
            setClickable(true);
            setFocusable(true);
            MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
            invalidate();
        } else {
            m_button.setBackgroundResource(R.drawable.edit_point); //measure_point
            m_button.setOnClickListener(null);
            m_button.setOnLongClickListener(null);
            m_button.setEnabled(false);
            m_button.setClickable(false);
            m_button.setFocusable(false);
            setEnabled(false);
            setClickable(false);
            setFocusable(false);
            MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
            invalidate();
        }
    }

    public void Remove() {
        this.setVisibility(View.GONE);
    }

    public void setMap(GIMap map) {
        m_map = map;
        map.registerGIControl(this);

    }

    public void onMapMove() {

        MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
    }

    public void onViewMove() {
        MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
    }

    public void afterMapFullRedraw(GIBounds bounds, Rect view_rect) {

    }

    public void afterMapImageRedraw(GIBounds bounds, Rect view_rect) {

    }

    public void onMarkerLayerRedraw(Rect view_rect) {
    }

    public void afterViewRedraw() {
    }

    private void Visibility() {
        if (hasClosed) {
            return;
        }
        if (m_map.m_view.contains(m_map.MapToScreenTempo(m_PointOriginMap).x, m_map.MapToScreenTempo(m_PointOriginMap).y)) {
            this.setVisibility(View.VISIBLE);
        } else {
            this.setVisibility(View.GONE);
        }
    }

    public void MoveTo(Point point) {
        Visibility();
        if (getVisibility() != View.VISIBLE) {
            return;
        }
        setX(point.x + map_location[0] - m_offset[0]);
        setY(point.y + map_location[1] - m_offset[1]);
        invalidate();
    }

    public void setLonLat(GILonLat lonlat) {
        m_PointOriginMap = lonlat;
        onViewMove();
    }

    public void setWKTPoint(GI_WktPoint point) {
        m_WKTPoint = point;
        m_PointOriginMap = new GILonLat(point.m_lon, point.m_lat);
        m_WKTPoint.Set(m_PointOriginMap);
        onViewMove();
    }

    public void onClick(View v) {
        ((Geoinfo) m_context).onSelectPoint(this);
    }

    public boolean onLongClick(View v) {
        if (isEnabled()) {
            new GILonLatInputDialog().setControl(this).show(((Geoinfo) m_context).getFragmentManager(), "lon_lat");
            return true;
        }
        return true;
    }

}
