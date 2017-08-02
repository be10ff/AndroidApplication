package ru.tcgeo.application.views.viewholder;

import android.view.View;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class XmlLayerHolder extends LayerHolder {

    @Bind(R.id.rsbStrokeWidth)
    public RangeSeekBar rsbStrokeWidth;

    @Bind(R.id.vFillColor)
    public View vFillColor;

    @Bind(R.id.vStrokeColor)
    public View vStrokeColor;

    public XmlLayerHolder(View itemView, LayerHolderCallback callback) {
        super(itemView, callback);
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.vFillColor)
    public void OnFillColor() {
//            new AmbilWarnaDialog(getActivity(), color.Get(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
//                @Override
//                public void onOk(AmbilWarnaDialog dialog, int new_color) {
//                    color.set(new_color);
//                    mFillColor.setBackgroundColor(new_color);
//                    ((GIEditableRenderer) mItem.layer.renderer()).m_style.m_paint_brush.setColor(new_color);
//                }
//
//                @Override
//                public void onCancel(AmbilWarnaDialog dialog) {
//                }
//            }).show();
    }

    @OnClick(R.id.vStrokeColor)
    public void OnStrokeColor() {

    }

    @Override
    public void initListeners() {
        super.initListeners();

        rsbStrokeWidth.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {

            }
        });

    }

}
