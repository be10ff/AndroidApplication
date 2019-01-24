package ru.tcgeo.application.views.viewholder;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.data.gilib.layer.GILayer;
import ru.tcgeo.application.views.callback.LayerHolderCallback;

/**
 * Created by artem on 14.07.17.
 */

public class XmlLayerHolder extends LayerHolder {

    @BindView(R.id.rsbStrokeWidth)
    public RangeSeekBar rsbStrokeWidth;

    @BindView(R.id.vFillColor)
    public View vFillColor;

    @BindView(R.id.vStrokeColor)
    public View vStrokeColor;

    @BindView(R.id.rgEditableType)
    public RadioGroup rgEditableType;

    @BindView(R.id.rbPoint)
    public RadioButton rbPoint;

    @BindView(R.id.rbLine)
    public RadioButton rbLine;

    @BindView(R.id.rbPolygon)
    public RadioButton rbPolygon;

    @BindView(R.id.cbPoiLayer)
    public CheckBox cbPoiLayer;

    public boolean isMarkersSource;

    public XmlLayerHolder(View itemView, LayerHolderCallback callback) {
        super(itemView, callback);
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.vFillColor)
    public void OnFillColor() {
        callback.onFillColor(this);
    }

    @OnClick(R.id.vStrokeColor)
    public void OnStrokeColor() {
        callback.onStrokeColor(this);
    }

    @Override
    public void removeListeners() {
        super.removeListeners();
        rsbStrokeWidth.setOnRangeSeekBarChangeListener(null);
        rgEditableType.setOnCheckedChangeListener(null);
        cbPoiLayer.setOnCheckedChangeListener(null);
    }

    @Override
    public void initListeners() {
        super.initListeners();

        rsbStrokeWidth.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                callback.onWidth(XmlLayerHolder.this);
            }
        });

        flMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMarkersSource = !isMarkersSource;
                callback.onMarkersSourceCheckChanged(XmlLayerHolder.this, isMarkersSource);
            }
        });

        rgEditableType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbPoint:
                        callback.onEditable(XmlLayerHolder.this, GILayer.EditableType.POI);
                        break;
                    case R.id.rbLine:
                        callback.onEditable(XmlLayerHolder.this, GILayer.EditableType.LINE);
                        break;
                    case R.id.rbPolygon:
                        callback.onEditable(XmlLayerHolder.this, GILayer.EditableType.POLYGON);
                        break;
                    default:
                        callback.onEditable(XmlLayerHolder.this, GILayer.EditableType.POI);
                        break;
                }
            }
        });

        cbPoiLayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callback.onSetPoiLayer(XmlLayerHolder.this, isChecked);
            }
        });

    }

}
