package ru.tcgeo.application.views.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.App;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.IFolderItemListener;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIEditableRenderer;
import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GITuple;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.utils.ProjectChangedEvent;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by a_belov on 22.07.15.
 */
public class ReAllSettingsFragment extends Fragment implements IFolderItemListener {

    @Bind(R.id.layer_name_edit)
    EditText mName;

    @Bind(R.id.layer_type_edit)
    Spinner mType;

    @Bind(R.id.location_type_edit)
    Spinner mLocationType;

    @Bind(R.id.layer_location_edit)
    EditText mLocation;

    @Bind(R.id.file_present_status)
    ImageView mFileOk;

//    @OnClick(R.id.layer_location_edit)
//    public void onFile(){
//        OpenFileDialog dlg = new OpenFileDialog();
//        dlg.setIFolderItemListener(this);
//        dlg.show(getChildFragmentManager(), "tag");
//    }

    @Bind(R.id.range_from)
    Spinner mRangeFrom;

    @Bind(R.id.range_to)
    Spinner mRangeTo;

    @Bind(R.id.zoom_type_edit)
    Spinner mZoomType;

    @Bind(R.id.ratio_edit)
    Spinner mRatio;

    @Bind(R.id.zoom_min_edit)
    Spinner mZoomMin;

    @Bind(R.id.zoom_max_edit)
    Spinner mZoomMax;

    @Bind(R.id.fill_color)
    View mFillColor;
    @Bind(R.id.stroke_color)
    View mStrokeColor;
    GITuple mItem;
    GIMap mMap;
    GILayer.Builder builder;

    public ReAllSettingsFragment() {
    }

    public ReAllSettingsFragment(GIMap map, GITuple item) {
        mItem = item;
        mMap = map;
        builder = new GILayer.Builder(item.layer);
    }

    @OnClick(R.id.fill_color)
    public void OnFillColor() {
        if (mItem.layer.m_layer_properties.m_style != null && mItem.layer.m_layer_properties.m_style.m_colors != null) {
            for (final GIColor color : mItem.layer.m_layer_properties.m_style.m_colors) {
                if (color.m_description.equalsIgnoreCase("fill")) {
                    new AmbilWarnaDialog(getActivity(), color.Get(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int new_color) {
                            color.set(new_color);
//                             GIVectorStyle vstyle = new GIVectorStyle(line, fill, 1);
                            mFillColor.setBackgroundColor(new_color);
                            ((GIEditableRenderer) mItem.layer.renderer()).m_style.m_paint_brush.setColor(new_color);


                        }

                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {
                        }
                    }).show();
                }
            }
        }

    }

    @OnClick(R.id.stroke_color)
    public void OnStrokeColor() {
        if (mItem.layer.m_layer_properties.m_style != null && mItem.layer.m_layer_properties.m_style.m_colors != null) {
            for (final GIColor color : mItem.layer.m_layer_properties.m_style.m_colors) {
                if (color.m_description.equalsIgnoreCase("line")) {
                    new AmbilWarnaDialog(getActivity(), color.Get(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int new_color) {
                            color.set(new_color);
                            mStrokeColor.setBackgroundColor(new_color);
                            ((GIEditableRenderer) mItem.layer.renderer()).m_style.m_paint_pen.setColor(new_color);
                        }

                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {
                        }
                    }).show();
                }
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layer_settings, container, false);
        ButterKnife.bind(this, view);

        mLocation.setEnabled(false);

        reset();

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                builder.name(mName.getText().toString());
            }
        });

        mType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String layer_type = mType.getSelectedItem().toString();
                if (layer_type.equalsIgnoreCase("SQL_LAYER")) {
                    builder.type(GILayer.GILayerType.SQL_LAYER);
                }
                if (layer_type.equalsIgnoreCase("SQL_YANDEX_LAYER")) {
                    builder.type(GILayer.GILayerType.SQL_YANDEX_LAYER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                builder.type(null);
            }
        });

        mZoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String zoom_type = mZoomType.getSelectedItem().toString();
                builder.sqldbZoomType(zoom_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                builder.sqldbZoomType(null);
            }
        });

        mRatio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int ratio = Integer.valueOf(mRatio.getSelectedItem().toString());
                builder.sqldbRatio(ratio);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                builder.sqldbZoomType(null);
            }
        });

        mZoomMax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                builder.sqldbMaxZ(Integer.valueOf(mZoomMax.getSelectedItem().toString()));
//                ((GISQLLayer)mItem.m_tuple.layer).m_max =  Integer.valueOf(mZoomMax.getSelectedItem().toString());
                double con = 0.0254 * 0.0066 * 256 / (0.5 * 40000000);
                int from = (int) (1 / (Math.pow(2, Integer.valueOf(mZoomMin.getSelectedItem().toString())) * con));
                builder.rangeFrom(from);
//                mItem.scale_range.setMin(1 / ((double) from));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                builder.sqldbMaxZ(-1);
            }
        });

        mZoomMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                builder.sqldbMinZ(Integer.valueOf(mZoomMin.getSelectedItem().toString()));
//                ((GISQLLayer)mItem.m_tuple.layer).m_min =  Integer.valueOf(mZoomMin.getSelectedItem().toString());
                double con = 0.0254 * 0.0066 * 256 / (0.5 * 40000000);
                int to = (int) (1 / (Math.pow(2, Integer.valueOf(mZoomMax.getSelectedItem().toString())) * con));
                builder.rangeTo(to);
//                mItem.scale_range.setMax(1 / (double) to);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                builder.sqldbMinZ(-1);
            }
        });


        return view;
    }

    @OnClick(R.id.button_apply)
    public void apply() {
        mItem.layer = builder.build();
        ((Geoinfo) getActivity()).getMap().UpdateMap();
    }

    public void reset() {
        mName.setText(mItem.layer.getName());
        mLocation.setText(mItem.layer.m_layer_properties.m_source.m_name);

        File file = new File(mItem.layer.m_layer_properties.m_source.m_name);
        if (file.exists()) {
            mFileOk.setImageResource(R.drawable.project_mark);
        } else {
            mFileOk.setImageResource(R.drawable.project_mark_fail);
        }

//        mRangeMin.setText(String.valueOf(mItem.m_tuple.layer.m_layer_properties.m_range.m_from));
//        mRangeMax.setText(String.valueOf(mItem.m_tuple.layer.m_layer_properties.m_range.m_to));

//        mRangeMin.setText(String.valueOf(mItem.m_tuple.scale_range.getIntMin()));
//        mRangeMax.setText(String.valueOf(mItem.m_tuple.scale_range.getIntMax()));
        // layer type

        ArrayAdapter<String> layer_type_adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.layer_type));

        layer_type_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        mType.setAdapter(layer_type_adapter);

        for (int i = 0; i < getResources().getStringArray(R.array.layer_type).length; i++) {
            if (getResources().getStringArray(R.array.layer_type)[i].equals(mItem.layer.m_layer_properties.m_type.name())) {
                mType.setSelection(i);
            }
        }

        // source type
        ArrayAdapter<String> source_type_adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.source_type));

        source_type_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        mLocationType.setAdapter(source_type_adapter);

        String layer_type = mItem.layer.m_layer_properties.m_source.m_location;
        for (int i = 0; i < getResources().getStringArray(R.array.source_type).length; i++) {
            if (getResources().getStringArray(R.array.source_type)[i].equals(layer_type)) {
                mLocationType.setSelection(i);
            }
        }

        //zoom type
        if (mItem.layer.m_layer_properties.m_sqldb != null) {
            ArrayAdapter<String> zoom_type_adapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.item_spinner,
                    getResources().getStringArray(R.array.zoom_type));

            zoom_type_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            mZoomType.setAdapter(zoom_type_adapter);
            for (int i = 0; i < getResources().getStringArray(R.array.zoom_type).length; i++) {
                if (getResources().getStringArray(R.array.zoom_type)[i].equals(mItem.layer.m_layer_properties.m_sqldb.m_zoom_type)) {
                    mZoomType.setSelection(i);
                }
            }
//            ratio
            ArrayAdapter<String> ratio_adapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.item_spinner,
                    getResources().getStringArray(R.array.ratio));

            ratio_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            mRatio.setAdapter(ratio_adapter);
            for (int i = 0; i < getResources().getStringArray(R.array.zoom_type).length; i++) {
                if (getResources().getStringArray(R.array.ratio)[i].equals(String.valueOf(mItem.layer.m_layer_properties.m_sqldb.mRatio))) {
                    mRatio.setSelection(i);
                }
            }

            //zoom max
            ArrayAdapter<String> zoom_max_adapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.item_spinner,
                    getResources().getStringArray(R.array.zoom_levels));

            zoom_max_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            mZoomMax.setAdapter(zoom_max_adapter);
            for (int i = 0; i < getResources().getStringArray(R.array.zoom_levels).length; i++) {
                if (getResources().getStringArray(R.array.zoom_levels)[i].equals(String.valueOf(mItem.layer.m_layer_properties.m_sqldb.m_max_z))) {
                    mZoomMax.setSelection(i);
                }
            }
            //zoom min
            ArrayAdapter<String> zoom_min_adapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.item_spinner,
                    getResources().getStringArray(R.array.zoom_levels));

            zoom_min_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            mZoomMin.setAdapter(zoom_min_adapter);
            for (int i = 0; i < getResources().getStringArray(R.array.zoom_levels).length; i++) {
                if (getResources().getStringArray(R.array.zoom_levels)[i].equals(String.valueOf(mItem.layer.m_layer_properties.m_sqldb.m_min_z))) {
                    mZoomMin.setSelection(i);
                }
            }
        }
        // range
        ArrayAdapter<String> range = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.source_type));

        source_type_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        mRangeFrom.setAdapter(source_type_adapter);

        ArrayAdapter<String> rangeFromAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.ranges));

        rangeFromAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        mRangeFrom.setAdapter(rangeFromAdapter);
        ArrayAdapter<String> rangeToAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.ranges));

        rangeToAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        mRangeTo.setAdapter(rangeToAdapter);
        //colors\
        if (mItem.layer.m_layer_properties.m_style != null && mItem.layer.m_layer_properties.m_style.m_colors != null) {
            for (GIColor color : mItem.layer.m_layer_properties.m_style.m_colors) {
                if (color.m_description.equalsIgnoreCase("line")) {
                    mStrokeColor.setBackgroundColor(color.Get());
                } else if (color.m_description.equalsIgnoreCase("fill")) {
                    mFillColor.setBackgroundColor(color.Get());
                }
            }

        }
    }

    @OnClick(R.id.move_down)
    public void onDown(View v) {
        mMap.m_layers.moveDown(mItem);
        mMap.ps.m_Group.moveDown(mItem.layer.m_layer_properties);
        App.getInstance().getEventBus().post(new ProjectChangedEvent());

    }

    @OnClick(R.id.move_up)
    public void onUp(View v) {
        mMap.m_layers.moveUp(mItem);
        mMap.ps.m_Group.moveUp(mItem.layer.m_layer_properties);
        App.getInstance().getEventBus().post(new ProjectChangedEvent());
    }

    @OnClick(R.id.remove)
    public void onRemove(View v) {
        mMap.m_layers.m_list.remove(mItem);
        mMap.ps.m_Group.m_Entries.remove(mItem.layer.m_layer_properties);
        App.getInstance().getEventBus().post(new ProjectChangedEvent());
    }

    @Override
    public void OnCannotFileRead(File file) {
        Toast.makeText(getActivity(), "can't be read!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnFileClicked(File file) {
        mLocation.setText(file.getAbsolutePath());
        builder.sourceLocation(file.getAbsolutePath());
        builder.sourceName("absolute");
    }
}
