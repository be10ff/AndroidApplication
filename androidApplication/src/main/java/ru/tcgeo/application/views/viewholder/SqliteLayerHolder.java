package ru.tcgeo.application.views.viewholder;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioGroup;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class SqliteLayerHolder extends LayerHolder {

    @Bind(R.id.rsbRatio)
    org.florescu.android.rangeseekbar.RangeSeekBar rsbRatio;

    @Bind(R.id.rgProjection)
    RadioGroup rgProjection;

    @Bind(R.id.rgZoomType)
    RadioGroup rgZoomType;

    public SqliteLayerHolder(View itemView, LayerHolderCallback callback) {
        super(itemView, callback);
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.ivMoveUp)
    public void onMoveUp() {
        callback.onMoveUp(this);
    }

    @OnClick(R.id.ivMoveDown)
    public void onMoveDown() {
        callback.onMoveDown(this);
    }

    @OnClick(R.id.ivRemove)
    public void onMoveRemove() {
        callback.onMoveRemove(this);
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
