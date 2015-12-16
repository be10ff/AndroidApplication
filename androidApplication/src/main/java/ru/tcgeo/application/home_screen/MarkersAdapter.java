package ru.tcgeo.application.home_screen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIControlFloating;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.gps.GIDirectionToPOIArrow;
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
                mActivity.getMarkersDialog().cancel();
                GILonLat new_center = new GILonLat(item.m_marker.m_lon,
                        item.m_marker.m_lat);
                RelativeLayout layout = (RelativeLayout) mActivity.findViewById(R.id.root);
                GIControlFloating m_marker_point = mActivity.getMarkerPoint();
                if (m_marker_point == null) {
                    m_marker_point = new GIControlFloating(parent
                            .getContext());
                    layout.addView(m_marker_point);
                    m_marker_point.setMap(mActivity.getMap());
                }
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

                        mActivity.getMarkersDialog().cancel();
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
}
