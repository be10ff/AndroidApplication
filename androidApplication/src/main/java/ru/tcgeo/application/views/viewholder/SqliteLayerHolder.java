package ru.tcgeo.application.views.viewholder;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.layer.GISQLLayer;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class SqliteLayerHolder extends LayerHolder {

    @BindView(R.id.rsbRatio)
    public org.florescu.android.rangeseekbar.RangeSeekBar rsbRatio;

    @BindView(R.id.rgProjection)
    public RadioGroup rgProjection;

    @BindView(R.id.rgZoomType)
    public RadioGroup rgZoomType;

    @BindView(R.id.rbYandex)
    public RadioButton rbYandex;

    @BindView(R.id.rbGoogle)
    public RadioButton rbGoogle;

    @BindView(R.id.rbAuto)
    public RadioButton rbAuto;

    @BindView(R.id.rbSmart)
    public RadioButton rbSmart;

    @BindView(R.id.rbAdaptive)
    public RadioButton rbAdaptive;

    public SqliteLayerHolder(View itemView, LayerHolderCallback callback) {
        super(itemView, callback);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void removeListeners() {
        super.removeListeners();
        rsbRatio.setOnRangeSeekBarChangeListener(null);
        rgProjection.setOnCheckedChangeListener(null);
        rgZoomType.setOnCheckedChangeListener(null);
    }

    @Override
    public void initListeners() {
        super.initListeners();


        rsbRatio.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                callback.onRatio(SqliteLayerHolder.this);
            }
        });

        rgProjection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbYandex:
                        callback.onProjection(SqliteLayerHolder.this, GISQLLayer.GILayerType.SQL_YANDEX_LAYER);
                        break;
                    case R.id.rbGoogle:
                        callback.onProjection(SqliteLayerHolder.this, GISQLLayer.GILayerType.SQL_LAYER);
                        break;
                }
            }
        });

        rgZoomType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbAuto:
                        callback.onZoomType(SqliteLayerHolder.this, GISQLLayer.GISQLiteZoomingType.AUTO);
                        break;
                    case R.id.rbSmart:
                        callback.onZoomType(SqliteLayerHolder.this, GISQLLayer.GISQLiteZoomingType.SMART);
                        break;
                    case R.id.rbAdaptive:
                        callback.onZoomType(SqliteLayerHolder.this, GISQLLayer.GISQLiteZoomingType.ADAPTIVE);
                        break;
                }
            }
        });
    }

}
