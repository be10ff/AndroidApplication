package ru.tcgeo.application.views.control;


import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.utils.GIYandexUtils;
import ru.tcgeo.application.utils.MaskedEditText;
import ru.tcgeo.application.views.callback.LonLatInputCallback;
import ru.tcgeo.application.views.callback.LonLatInputTextWatcher;

import static ru.tcgeo.application.utils.Mercator.getCanonicalCoordString;
import static ru.tcgeo.application.utils.Mercator.getGradCoordString;
import static ru.tcgeo.application.utils.Mercator.getGradMinCoordString;


public class LonLatInputView extends LinearLayout {

    GILonLat m_point;

    @BindView(R.id.lon_decimal)
    MaskedEditText m_lon_dec;

    @BindView(R.id.lat_decimal)
    MaskedEditText m_lat_dec;

    @BindView(R.id.lon_can)
    MaskedEditText m_lon_can;

    @BindView(R.id.lat_can)
    MaskedEditText m_lat_can;

    @BindView(R.id.lon_grad_min)
    MaskedEditText m_lon_grad_min;

    @BindView(R.id.lat_grad_min)
    MaskedEditText m_lat_grad_min;

    LonLatInputCallback callback;

    public LonLatInputView(Context context) {
        super(context);
        init(context);
    }

    public LonLatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LonLatInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_lonlat_input, this);
        ButterKnife.bind(this, v);
        //º   ° ctrl+shift+u +code +space
        m_lon_dec.addTextChangedListener(new LonLatInputTextWatcher() {

            public void afterTextChanged(Editable s) {

                if (m_lon_dec.hasFocus()) {
                    try {
                        String res = m_lon_dec.getUnmaskedText();
                        m_point.setLon(GIYandexUtils.DoubleLonLatFromString(m_lon_dec.getUnmaskedText().replaceAll("[^0-9.]", "")));
                        m_lon_can.setMaskedText(getCanonicalCoordString(m_point.lon()));
                        m_lon_grad_min.setMaskedText(getGradMinCoordString(m_point.lon()));
                        callback.onNewValue(m_point);

                    } catch (NumberFormatException e) {

                    }
                }
            }
        });

        m_lat_dec.addTextChangedListener(new LonLatInputTextWatcher() {
            public void afterTextChanged(Editable s) {
                if (m_lat_dec.hasFocus()) {
                    try {
                        m_point.setLat(GIYandexUtils.DoubleLonLatFromString(m_lat_dec.getUnmaskedText().replaceAll("[^0-9.]", "")));
                        m_lat_can.setMaskedText(getCanonicalCoordString(m_point.lat()));
                        m_lat_grad_min.setMaskedText(getGradMinCoordString(m_point.lat()));
                        callback.onNewValue(m_point);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        });

        m_lon_grad_min.addTextChangedListener(new LonLatInputTextWatcher() {
            public void afterTextChanged(Editable s) {
                if (m_lon_grad_min.hasFocus()) {
                    try {
                        m_point.setLon(GIYandexUtils.DoubleLonLatFromString(m_lon_grad_min.getUnmaskedText().replace(" ", "0").replaceAll("[^0-9.]", "")));
                        m_lon_can.setMaskedText(getCanonicalCoordString(m_point.lon()));
                        m_lon_dec.setMaskedText(getGradCoordString(m_point.lon()));
                        callback.onNewValue(m_point);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        });

        m_lat_grad_min.addTextChangedListener(new LonLatInputTextWatcher() {
            public void afterTextChanged(Editable s) {
                if (m_lat_grad_min.hasFocus()) {
                    try {
                        m_point.setLat(GIYandexUtils.DoubleLonLatFromString(m_lat_grad_min.getUnmaskedText().replace(" ", "0").replaceAll("[^0-9.]", "")));
                        m_lat_can.setMaskedText(getCanonicalCoordString(m_point.lat()));
                        m_lat_dec.setMaskedText(getGradCoordString(m_point.lat()));
                        callback.onNewValue(m_point);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        });

        m_lon_can.addTextChangedListener(new LonLatInputTextWatcher() {
            public void afterTextChanged(Editable s) {
                if (m_lon_can.hasFocus()) {
                    try {
                        m_point.setLon(GIYandexUtils.DoubleLonLatFromString(m_lon_can.getUnmaskedText().replace(" ", "0").replaceAll("[^0-9.]", "")));
                        m_lon_grad_min.setMaskedText(getGradMinCoordString(m_point.lon()));
                        m_lon_dec.setMaskedText(getGradCoordString(m_point.lon()));
                        callback.onNewValue(m_point);

                    } catch (NumberFormatException e) {

                    }
                }
            }
        });

        m_lat_can.addTextChangedListener(new LonLatInputTextWatcher() {
            public void afterTextChanged(Editable s) {
                if (m_lat_can.hasFocus()) {
                    try {
                        m_point.setLat(GIYandexUtils.DoubleLonLatFromString(m_lat_can.getUnmaskedText().replace(" ", "0").replaceAll("[^0-9.]", "")));
                        m_lat_grad_min.setMaskedText(getGradMinCoordString(m_point.lat()));
                        m_lat_dec.setMaskedText(getGradCoordString(m_point.lat()));
                        callback.onNewValue(m_point);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        });
    }

    public void setPoint(GILonLat point) {

        this.m_point = point;

        m_lon_dec.setMaskedText(getGradCoordString(m_point.lon()));
        m_lat_dec.setMaskedText(getGradCoordString(m_point.lat()));

        m_lon_grad_min.setMaskedText(getGradMinCoordString(m_point.lon()));
        m_lat_grad_min.setMaskedText(getGradMinCoordString(m_point.lat()));

        m_lon_can.setMaskedText(getCanonicalCoordString(m_point.lon()));
        m_lat_can.setMaskedText(getCanonicalCoordString(m_point.lat()));

    }

    public void setCallback(LonLatInputCallback callback) {
        this.callback = callback;
    }


}
