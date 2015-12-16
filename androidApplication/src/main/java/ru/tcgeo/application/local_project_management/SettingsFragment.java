package ru.tcgeo.application.local_project_management;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;

import butterknife.OnClick;
import ru.tcgeo.application.App;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIGroupLayer;
import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GISQLLayer;
import ru.tcgeo.application.home_screen.LayersAdapterItem;
import ru.tcgeo.application.utils.ProjectChangedEvent;

/**
 * Created by a_belov on 22.07.15.
 */
public class SettingsFragment extends Fragment {

    @Bind(R.id.layer_name_edit)
    EditText mName;

    @Bind(R.id.layer_type_edit)
    Spinner mType;

    @Bind(R.id.location_type_edit)
    Spinner mLocationType;

    @Bind(R.id.layer_location_edit)
    EditText mLocation;

    @Bind(R.id.zoom_type_edit)
    Spinner mZoomType;

    @Bind(R.id.zoom_min_edit)
    Spinner mZoomMin;

    @Bind(R.id.zoom_max_edit)
    Spinner mZoomMax;

    LayersAdapterItem mItem;
    GIMap mMap;

    public SettingsFragment(){}

    public SettingsFragment(GIMap map, LayersAdapterItem item){
        mItem = item;
        mMap = map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layer_settings, container, false);
        ButterKnife.bind(this, view);

        reset();

        return view;
    }

    @OnClick(R.id.button_apply)
    public void apply(){

        mItem.m_tuple.layer.setName(mName.getText().toString());


        String layer_type = mType.getSelectedItem().toString();

        if(layer_type.equalsIgnoreCase("SQL_LAYER"))
        {
            mItem.m_tuple.layer.m_layer_properties.m_type = GILayer.GILayerType.SQL_LAYER;
            ((GISQLLayer)mItem.m_tuple.layer).type_ = GILayer.GILayerType.SQL_LAYER;
        }
        if(layer_type.equalsIgnoreCase("SQL_YANDEX_LAYER"))
        {
            mItem.m_tuple.layer.m_layer_properties.m_type = GILayer.GILayerType.SQL_YANDEX_LAYER;
            ((GISQLLayer)mItem.m_tuple.layer).type_ = GILayer.GILayerType.SQL_YANDEX_LAYER;
        }

        //zomming type
        mItem.m_tuple.layer.m_layer_properties.m_sqldb.m_zoom_type =  mZoomType.getSelectedItem().toString();
        String zoom_type = mZoomType.getSelectedItem().toString();
        if(zoom_type.equalsIgnoreCase("adaptive")) {
            ((GISQLLayer)mItem.m_tuple.layer).m_zooming_type = GISQLLayer.GISQLiteZoomingType.ADAPTIVE;
        } else if(zoom_type.equalsIgnoreCase("smart")) {
            ((GISQLLayer)mItem.m_tuple.layer).m_zooming_type = GISQLLayer.GISQLiteZoomingType.SMART;
        } else {
            ((GISQLLayer)mItem.m_tuple.layer).m_zooming_type = GISQLLayer.GISQLiteZoomingType.AUTO;
        }
        mItem.m_tuple.layer.m_layer_properties.m_sqldb.m_zoom_type = zoom_type;
        //zoom
        mItem.m_tuple.layer.m_layer_properties.m_sqldb.m_max_z =  Integer.valueOf(mZoomMax.getSelectedItem().toString());
        mItem.m_tuple.layer.m_layer_properties.m_sqldb.m_min_z =  Integer.valueOf(mZoomMin.getSelectedItem().toString());

        ((GISQLLayer)mItem.m_tuple.layer).m_max =  Integer.valueOf(mZoomMax.getSelectedItem().toString());
        ((GISQLLayer)mItem.m_tuple.layer).m_min =  Integer.valueOf(mZoomMin.getSelectedItem().toString());


        //range
        double con = 0.0254*0.0066*256/(0.5*40000000);
        int from = (int)( 1/(Math.pow(2,  Integer.valueOf(mZoomMin.getSelectedItem().toString()))*con));
        int to =  (int) ( 1/(Math.pow(2,  Integer.valueOf(mZoomMax.getSelectedItem().toString()))*con));

        mItem.m_tuple.layer.m_layer_properties.m_range.m_from = from;/*Integer.valueOf(mRangeMin.getText().toString());*/
        mItem.m_tuple.layer.m_layer_properties.m_range.m_to = to;/*Integer.valueOf(mRangeMax.getText().toString());*/


        mItem.m_tuple.scale_range.setMin(1 / ((double)from));
        mItem.m_tuple.scale_range.setMax(1/(double)to);

        ((Geoinfo)getActivity()).getMap().UpdateMap();
    }

    public void reset(){
        mName.setText(mItem.m_tuple.layer.getName());
        mLocation.setText(mItem.m_tuple.layer.m_layer_properties.m_source.m_name);

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

        for(int i = 0; i < getResources().getStringArray(R.array.layer_type).length; i++){
            if(getResources().getStringArray(R.array.layer_type)[i].equals( mItem.m_tuple.layer.m_layer_properties.m_type.name())){
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

        String layer_type = mItem.m_tuple.layer.m_layer_properties.m_source.m_location;
        for(int i = 0; i < getResources().getStringArray(R.array.source_type).length; i++){
            if(getResources().getStringArray(R.array.source_type)[i].equals(layer_type)){
                mLocationType.setSelection(i);
            }
        }

        //zoom type
        ArrayAdapter<String> zoom_type_adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.zoom_type));

        zoom_type_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        mZoomType.setAdapter(zoom_type_adapter);
        for(int i = 0; i < getResources().getStringArray(R.array.zoom_type).length; i++){
            if(getResources().getStringArray(R.array.zoom_type)[i].equals(mItem.m_tuple.layer.m_layer_properties.m_sqldb.m_zoom_type)){
                mZoomType.setSelection(i);
            }
        }

        //zoom max
        ArrayAdapter<String> zoom_max_adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_spinner,
                getResources().getStringArray(R.array.zoom_levels));

        zoom_max_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        mZoomMax.setAdapter(zoom_max_adapter);
        for(int i = 0; i < getResources().getStringArray(R.array.zoom_levels).length; i++){
            if(getResources().getStringArray(R.array.zoom_levels)[i].equals(String.valueOf(mItem.m_tuple.layer.m_layer_properties.m_sqldb.m_max_z))){
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
        for(int i = 0; i < getResources().getStringArray(R.array.zoom_levels).length; i++){
            if(getResources().getStringArray(R.array.zoom_levels)[i].equals(String.valueOf(mItem.m_tuple.layer.m_layer_properties.m_sqldb.m_min_z))){
                mZoomMin.setSelection(i);
            }
        }
    }

    @OnClick(R.id.move_down)
    public void onDown(View v){
        mMap.m_layers.moveDown(mItem.m_tuple);
        mMap.ps.m_Group.moveDown(mItem.m_tuple.layer.m_layer_properties);
        App.getInstance().getEventBus().post(new ProjectChangedEvent());

    }
    @OnClick(R.id.move_up)
    public void onUp(View v){
        mMap.m_layers.moveUp(mItem.m_tuple);
        mMap.ps.m_Group.moveUp(mItem.m_tuple.layer.m_layer_properties);
        App.getInstance().getEventBus().post(new ProjectChangedEvent());
    }
}
