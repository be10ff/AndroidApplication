package ru.tcgeo.application.views.dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.control.GIGeometryPointControl;
import ru.tcgeo.application.utils.GIYandexUtils;
//import ru.tcgeo.application.utils.MaskedWatcher;
import ru.tcgeo.application.wkt.GI_WktPoint;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class ReReREGILonLatInputDialog extends DialogFragment {
    Geoinfo activity;
    GI_WktPoint m_point;
    GIGeometryPointControl m_control;

    @BindView(R.id.lon_decimal)
    EditText m_lon_dec;

    @BindView(R.id.lat_decimal)
    EditText m_lat_dec;

    @BindView(R.id.lon_can)
    EditText m_lon_can;

    @BindView(R.id.lat_can)
    EditText m_lat_can;

    @BindView(R.id.lon_grad_min)
    EditText m_lon_grad_min;

    @BindView(R.id.lat_grad_min)
    EditText m_lat_grad_min;


    public ReReREGILonLatInputDialog(GIGeometryPointControl control) {
        m_control = control;
        m_point = m_control.m_WKTPoint;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Geoinfo)context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.re_re_redialog_lonlat_input, null);
        ButterKnife.bind(this, v);
        //º   ° ctrl+shift+u +code +space

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("__.________");
        FormatWatcher formatWatcher = new MaskFormatWatcher( // форматировать текст будет вот он
                MaskImpl.createTerminated(slots)
        );
        formatWatcher.installOn(m_lon_dec);
        m_lon_dec.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

                if (m_lon_dec.hasFocus()) {
                    try {
                        m_point.m_lon = GIYandexUtils.DoubleLonLatFromString(s.toString().replaceAll("[^0-9.]", ""));
//                        m_lon_can.setMaskedText(getCanonicalCoordString(m_point.m_lon));
//                        m_lon_grad_min.setMaskedText(getGradMinCoordString(m_point.m_lon));

                    } catch (NumberFormatException e) {

                    }
                }
            }
        });

//        m_lat_dec.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//;
//            }
//
//            public void afterTextChanged(Editable s) {
//                if (m_lat_dec.hasFocus()) {
//                    try {
//                        m_point.m_lat = GIYandexUtils.DoubleLonLatFromString(m_lat_dec.getUnmaskedText().replaceAll("[^0-9.]", ""));
//                        m_lat_can.setMaskedText(getCanonicalCoordString(m_point.m_lat));
//                        m_lat_grad_min.setMaskedText(getGradMinCoordString(m_point.m_lat));
//                    } catch (NumberFormatException e) {
//
//                    }
//                }
//            }
//        });
//
//        m_lon_grad_min.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            public void afterTextChanged(Editable s) {
//                if (m_lon_grad_min.hasFocus()) {
//                    try {
//                        m_point.m_lon = GIYandexUtils.DoubleLonLatFromString(m_lon_grad_min.getUnmaskedText().replaceAll("[^0-9.]", ""));
//                        m_lon_can.setMaskedText(getCanonicalCoordString(m_point.m_lon));
//                        m_lon_dec.setMaskedText(getGradCoordString(m_point.m_lon));
//                    } catch (NumberFormatException e) {
//                    }
//                }
//            }
//        });
//;
//        m_lat_grad_min.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String ss = s.toString();
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                String ss = s.toString();
//            }
//
//            public void afterTextChanged(Editable s) {
//                if (m_lat_grad_min.hasFocus()) {
//                    try {
//                        m_point.m_lat = GIYandexUtils.DoubleLonLatFromString(m_lat_grad_min.getUnmaskedText().replaceAll("[^0-9.]", ""));
//                        m_lat_can.setMaskedText(getCanonicalCoordString(m_point.m_lat));
//                        m_lat_dec.setMaskedText(getGradCoordString(m_point.m_lat));
//                    } catch (NumberFormatException e) {
//
//                    }
//                }
//            }
//        });
//
//        m_lon_can.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            public void afterTextChanged(Editable s) {
//                if (m_lon_can.hasFocus()) {
//                    try {
//                        m_point.m_lon = GIYandexUtils.DoubleLonLatFromString(m_lon_can.getUnmaskedText().replaceAll("[^0-9.]", ""));
//                        m_lon_grad_min.setMaskedText(getGradMinCoordString(m_point.m_lon));
//                        m_lon_dec.setMaskedText(getGradCoordString(m_point.m_lon));
//
//                    } catch (NumberFormatException e) {
//
//                    }
//                }
//            }
//        });
//
//
//        m_lat_can.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                String ss = s.toString();
//            }
//
//            public void afterTextChanged(Editable s) {
//                if (m_lat_can.hasFocus()) {
//                    try {
//                        m_point.m_lat = GIYandexUtils.DoubleLonLatFromString(m_lat_can.getUnmaskedText().replaceAll("[^0-9.]", ""));
//                        m_lat_grad_min.setMaskedText(getGradMinCoordString(m_point.m_lat));
//                        m_lat_dec.setMaskedText(getGradCoordString(m_point.m_lat));
//                    } catch (NumberFormatException e) {
//                    }
//                }
//            }
//        });

        m_lon_dec.setText(getGradCoordString(m_point.m_lon));
//        m_lat_dec.setMaskedText(getGradCoordString(m_point.m_lat));
//
//        m_lon_grad_min.setMaskedText(getGradMinCoordString(m_point.m_lon));
//        m_lat_grad_min.setMaskedText(getGradMinCoordString(m_point.m_lat));
//
//        m_lon_can.setMaskedText(getCanonicalCoordString(m_point.m_lon));
//        m_lat_can.setMaskedText(getCanonicalCoordString(m_point.m_lat));


        return v;
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

    public String getCanonicalCoordString(double coord) {
//        int degrees = (int) Math.floor(coord);
//        int mins = (int) Math.floor((coord - degrees) * 60);
//        double secs = ((coord - degrees) * 60 - mins) * 60;
//        String res = String.format("%02d° %02d’ %07.4f”", degrees, mins, secs);
//        return res;
        long degrees = (long) Math.floor(coord) ;
        long mins = (long) Math.floor((coord - degrees) * 60) ;
        long secs = (long)Math.round(((coord - degrees) * 60 - mins) * 60*1000000);
//        String res = String.format("%02d° %02d’ %07.4f”", degrees, mins, secs);
        String res =  String.valueOf(degrees) + String.valueOf(mins) + String.valueOf(secs);
        return String.valueOf(degrees) + String.valueOf(mins) + String.valueOf(secs);
    }

    public String getGradMinCoordString(double coord) {
//        int degrees = (int) Math.floor(coord);//º   ° ctrl+shift+u +code +space
//        float mins = (float) ((coord - degrees) * 60);
//        String res = String.format("%02d° %09.6f'", degrees, mins);
//		String res =  customFormat("00", degrees)+ "° " + customFormat("00.######", mins) + "'";
//		getDoubleCoordFromGGMMMMtring(res);
        long degrees = (long) Math.floor(coord) ;
        long mins = (long) Math.round((coord - degrees) * 60 * 1000000) ;
        String res = String.valueOf(degrees) + String.valueOf(mins);
        return String.valueOf(degrees) + String.valueOf(mins);

    }

    public String getGradCoordString(double coord) {
//        int degrees = (int) Math.floor(coord);//º   ° ctrl+shift+u +code +space
//        float mins = (float) ((coord - degrees) * 60);
//        String res = String.format("%02d° %09.6f'", degrees, mins);
//		String res =  customFormat("00", degrees)+ "° " + customFormat("00.######", mins) + "'";
//		getDoubleCoordFromGGMMMMtring(res);
        long degrees = (long) Math.round(coord * 100000000) ;
        String res = String.valueOf(degrees);
        return String.valueOf(degrees);

    }

}
