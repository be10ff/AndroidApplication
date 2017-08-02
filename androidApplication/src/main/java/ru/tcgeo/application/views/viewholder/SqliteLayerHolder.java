package ru.tcgeo.application.views.viewholder;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class SqliteLayerHolder extends LayerHolder {

    @Bind(R.id.rsbRatio)
    public org.florescu.android.rangeseekbar.RangeSeekBar rsbRatio;

    @Bind(R.id.rgProjection)
    public RadioGroup rgProjection;

    @Bind(R.id.rgZoomType)
    public RadioGroup rgZoomType;

    @Bind(R.id.rbYandex)
    public RadioButton rbYandex;

    @Bind(R.id.rbGoogle)
    public RadioButton rbGoogle;

    @Bind(R.id.rbAuto)
    public RadioButton rbAuto;

    @Bind(R.id.rbSmart)
    public RadioButton rbSmart;

    @Bind(R.id.rbAdaptive)
    public RadioButton rbAdaptive;

    public SqliteLayerHolder(View itemView, LayerHolderCallback callback) {
        super(itemView, callback);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void initListeners() {
        super.initListeners();

        rsbRatio.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {

            }
        });

        rgProjection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            }
        });

        rgZoomType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            }
        });
    }

//    public void apply() {
//        mItem.m_tuple.layer = builder.build();
//        ((Geoinfo) getActivity()).getMap().UpdateMap();
//    }

}
