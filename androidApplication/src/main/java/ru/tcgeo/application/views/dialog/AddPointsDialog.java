package ru.tcgeo.application.views.dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.data.gilib.models.GILonLat;
import ru.tcgeo.application.utils.CommonUtils;
import ru.tcgeo.application.views.callback.LonLatInputCallback;
import ru.tcgeo.application.views.widget.LonLatInputView;

public class AddPointsDialog extends DialogFragment implements LonLatInputCallback {
    Geoinfo activity;
    GILonLat m_point;
//    GIGeometryPointControl m_control;

    @BindView(R.id.tvName)
    EditText tvName;

    @BindView(R.id.ivLonLat)
    LonLatInputView ivLonLat;

    @OnClick(R.id.tvAddNext)
    public void onNextPoint(){
        String name = tvName.getText().toString();
        if(name == null || name.isEmpty()) {
            name = CommonUtils.getCurrentTime();
        }
        activity.getMap().AddPointToPOI(m_point, name);
        activity.getMap().UpdateMap();
    }

    @OnClick(R.id.tvClose)
    public void onClose(){
        dismiss();
    }


    static public String customFormat(String pattern, double value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Geoinfo)context;
        m_point = activity.getMap().getWGSCenter();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState) {
//		getDialog().setTitle("Dialog");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_add_points, null);
        ButterKnife.bind(this, v);
        ivLonLat.setCallback(this);
        ivLonLat.setPoint(m_point);
        return v;
    }

    public void onCancel(DialogInterface dialog) {

        try {

            super.onCancel(dialog);
        } catch (NumberFormatException e) {

        }
    }

    @Override
    public void onNewValue(GILonLat point) {
        m_point.setLon(point.lon());
        m_point.setLat(point.lat());
    }
}
