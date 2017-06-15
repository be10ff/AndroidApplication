package ru.tcgeo.application.home_screen.adapter;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIControlFloating;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIEditableLayer;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GIPList;
import ru.tcgeo.application.gilib.GITuple;
import ru.tcgeo.application.gilib.gps.GIXMLTrack;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.gps.GIDirectionToPOIArrow;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktLinestring;
import ru.tcgeo.application.wkt.GI_WktPoint;

/**
 * Created by a_belov on 06.07.15.
 */
public class MarkersAdapter extends ArrayAdapter<MarkersAdapterItem> {
    Geoinfo mActivity;
    @Override
    public View getView(int position, View convertView,
                        final ViewGroup parent) {
        final MarkersAdapterItem item = getItem(position);
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.markers_list_item, null);
        TextView text_name = (TextView) v
                .findViewById(R.id.markers_list_item_text);
        ImageView iv = (ImageView) v.findViewById(R.id.imageViewDirection);
        text_name.setText(item.m_marker.m_name);
        if (mActivity.getMap().ps.m_markers_source != null) {
            if (mActivity.getMap().ps.m_markers_source.equalsIgnoreCase("layer")) {
                if (GIEditLayersKeeper.Instance().m_CurrentTarget != null) {
                    if (item.m_marker.m_lat == ((GI_WktPoint) GIEditLayersKeeper
                            .Instance().m_CurrentTarget).m_lat
                            && item.m_marker.m_lon == ((GI_WktPoint) GIEditLayersKeeper
                            .Instance().m_CurrentTarget).m_lon) {
                        iv.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.getMarkersDialog().dismiss();
                GILonLat new_center = new GILonLat(item.m_marker.m_lon,
                        item.m_marker.m_lat);
                GIControlFloating m_marker_point = mActivity.getMarkerPoint();
                m_marker_point.setLonLat(new_center);
                if (item.m_marker.m_diag != 0) {
                    mActivity.getMap().SetCenter(new_center, item.m_marker.m_diag);
                } else {
                    mActivity.getMap().SetCenter(GIProjection.ReprojectLonLat(new_center,
                            GIProjection.WGS84(), mActivity.getMap().Projection()));
                }
            }
        });
        if (mActivity.getMap().ps.m_markers_source != null) {
            if (mActivity.getMap().ps.m_markers_source.equalsIgnoreCase("layer")) {
                v.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {

                        mActivity.getMarkersDialog().dismiss();
                        GILonLat new_center = new GILonLat(
                                item.m_marker.m_lon, item.m_marker.m_lat);
                        GI_WktPoint poi = new GI_WktPoint(new_center);
                        GIEditLayersKeeper.Instance().m_CurrentTarget = poi;
                        //GILocator arr = new GILocator(poi);
                        GIDirectionToPOIArrow arrow = new GIDirectionToPOIArrow(poi);
                        GIEditLayersKeeper.Instance().LocatorView(poi);
                        //GIEditLayersKeeper.Instance().AccurancyRangeView(true);
                        return false;
                    }
                });
            }
        }
        return v;
    }

    public MarkersAdapter(Geoinfo activity, int resource,
                          int textViewResourceId) {
        super(activity, resource, textViewResourceId);
        mActivity = activity;
    }

    public void AddMarkers(GIMap map) {
        if (map.ps.m_markers_source == null && map.ps.m_markers != null && !map.ps.m_markers.isEmpty()) {
            if (isEmpty()) {
                GIPList PList = new GIPList();
                PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_markers); // "/sdcard/"
                for (GIPList.GIMarker marker : PList.m_list) {
                    add(new MarkersAdapterItem(marker));
                }
            }
        }
        if (map.ps.m_markers_source != null) {
            if (map.ps.m_markers_source.equalsIgnoreCase("file")) {
                if (isEmpty()) {
                    GIPList PList = new GIPList();
                    PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + map.ps.m_markers);// "/sdcard/"
                    for (GIPList.GIMarker marker : PList.m_list) {
                        add(new MarkersAdapterItem(marker));
                    }
                }
            }
            if (map.ps.m_markers_source.equalsIgnoreCase("layer")) {
                GIEditableLayer layer = null;
                for (GITuple tuple : map.m_layers.m_list) {
                    if (tuple.layer.getName()
                            .equalsIgnoreCase(map.ps.m_markers)) {
                        layer = (GIEditableLayer) tuple.layer;
                        break;
                    }
                }
                if (layer != null){
                    clear();
                    GIPList list = new GIPList();
                    for (GI_WktGeometry geom : layer.m_shapes) {
                        if(geom instanceof GI_WktPoint){
                            GI_WktPoint point = (GI_WktPoint) geom;
                            if (point != null) {
                                GIPList.GIMarker marker = list.new GIMarker();
                                if (geom.m_attributes.containsKey("Name")) {
                                    marker.m_name = (String) geom.m_attributes.get("Name").m_value.toString();
                                } else if (!geom.m_attributes.keySet().isEmpty()) {
                                    marker.m_name = (String) geom.m_attributes.get(geom.m_attributes.keySet().toArray()[0]).m_value;
                                } else {
                                    marker.m_name = String.valueOf(geom.m_ID);
                                }
                                marker.m_lon = point.m_lon;
                                marker.m_lat = point.m_lat;
                                marker.m_description = "";
                                marker.m_image = "";
                                marker.m_diag = 0;
                                add(new MarkersAdapterItem(marker));
                            }
                        } else if(geom instanceof GIXMLTrack){
                            GIXMLTrack track = (GIXMLTrack) geom;
                            if(track != null&&track.m_points != null && !track.m_points.isEmpty()){
                                GIPList.GIMarker marker = list.new GIMarker();
                                if (geom.m_attributes.containsKey("Project")) {
                                    marker.m_name = (String) geom.m_attributes.get("Project").m_value.toString();
                                    if(geom.m_attributes.containsKey("Description")){
                                        String data = GIEditLayersKeeper.getTime((String) geom.m_attributes.get("Description").m_value.toString());
                                        if(!data.isEmpty()){
                                            marker.m_name =  marker.m_name + " " + data;
                                        } else {
                                            marker.m_name =  marker.m_name + " " + (String) geom.m_attributes.get("Description").m_value.toString();
                                        }

                                    }
                                } else if (!geom.m_attributes.keySet().isEmpty()) {
                                    marker.m_name = (String) geom.m_attributes.get(geom.m_attributes.keySet().toArray()[0]).m_value;
                                } else {
                                    marker.m_name = String.valueOf(geom.m_ID);
                                }
                                marker.m_lon = ((GI_WktPoint)track.m_points.get(0)).m_lon;
                                marker.m_lat = ((GI_WktPoint)track.m_points.get(0)).m_lat;
                                marker.m_description = "";
                                marker.m_image = "";
                                marker.m_diag = 0;
                                add(new MarkersAdapterItem(marker));
                            }
                        } else if(geom instanceof GI_WktLinestring){
                            GI_WktLinestring line = (GI_WktLinestring) geom;
                            if(line != null&&line.m_points != null && !line.m_points.isEmpty()){
                                GIPList.GIMarker marker = list.new GIMarker();
                                if (geom.m_attributes.containsKey("Project")) {
                                    marker.m_name = (String) geom.m_attributes.get("Project").m_value.toString();
                                    if(geom.m_attributes.containsKey("Description")){
                                        String data = GIEditLayersKeeper.getTime((String) geom.m_attributes.get("Description").m_value.toString());
                                        if(!data.isEmpty()){
                                            marker.m_name =  marker.m_name + " " + data;
                                        } else {
                                            marker.m_name =  marker.m_name + " " + (String) geom.m_attributes.get("Description").m_value.toString();
                                        }

                                    }
                                } else if (!geom.m_attributes.keySet().isEmpty()) {
                                    marker.m_name = (String) geom.m_attributes.get(geom.m_attributes.keySet().toArray()[0]).m_value;
                                } else {
                                    marker.m_name = String.valueOf(geom.m_ID);
                                }
                                marker.m_lon = ((GI_WktPoint)line.m_points.get(0)).m_lon;
                                marker.m_lat = ((GI_WktPoint)line.m_points.get(0)).m_lat;
                                marker.m_description = "";
                                marker.m_image = "";
                                marker.m_diag = 0;
                                add(new MarkersAdapterItem(marker));
                            }
                        }
                    }
                }
            }
        }
    }
}
