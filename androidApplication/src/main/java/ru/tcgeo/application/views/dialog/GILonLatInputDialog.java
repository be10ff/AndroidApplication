package ru.tcgeo.application.views.dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.views.callback.LonLatInputCallback;
import ru.tcgeo.application.views.control.GIGeometryPointControl;
import ru.tcgeo.application.views.control.LonLatInputView;
import ru.tcgeo.application.wkt.GI_WktPoint;

//import ru.tcgeo.application.utils.MaskedWatcher;

public class GILonLatInputDialog extends DialogFragment implements LonLatInputCallback {
    Geoinfo activity;
    GI_WktPoint m_point;
    GIGeometryPointControl m_control;

    @BindView(R.id.ivLonLat)
    LonLatInputView ivLonLat;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Geoinfo)context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_lonlat, null);
        ButterKnife.bind(this, v);
        ivLonLat.setCallback(this);
        ivLonLat.setPoint(m_point.LonLat());
        return v;
    }

    public GILonLatInputDialog setControl(GIGeometryPointControl control) {
        m_control = control;
        m_point = m_control.m_WKTPoint;
        return this;
    }

    public void onNewValue(GILonLat point) {
        m_point.m_lon = point.lon();
        m_point.m_lat = point.lat();
    }

    public void onCancel(DialogInterface dialog) {
        try {

            m_control.m_WKTPoint.m_lon = m_point.m_lon;
            m_control.m_WKTPoint.m_lat = m_point.m_lat;
            m_control.setWKTPoint(m_control.m_WKTPoint);
            activity.getMap().getCurrentEditingControl().invalidate();
            super.onCancel(dialog);
        } catch (NumberFormatException e) {
        }
    }

}
