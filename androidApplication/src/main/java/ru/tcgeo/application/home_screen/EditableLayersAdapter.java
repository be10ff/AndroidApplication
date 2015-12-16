package ru.tcgeo.application.home_screen;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIEditableLayer;

/**
 * Created by a_belov on 06.07.15.
 */
public class EditableLayersAdapter extends ArrayAdapter<EditableLayersAdapterItem> {
    Geoinfo mActivity;
    @Override
    public View getView(int position, View convertView,
                        final ViewGroup parent) {
        final EditableLayersAdapterItem item = getItem(position);
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.markers_list_item, null);
        TextView text_name = (TextView) v
                .findViewById(R.id.markers_list_item_text);

        text_name.setText(item.m_layer.getName());

        GIEditableLayer layer = (GIEditableLayer) item.m_layer;
        switch (layer.m_Status) {
            case UNEDITED: {
                text_name.setTextColor(Color.BLACK);
                break;
            }
            case EDITED: {
                text_name.setTextColor(Color.BLUE);
                break;
            }
            case UNSAVED: {
                text_name.setTextColor(Color.RED);
                break;
            }
            default: {
                text_name.setTextColor(Color.BLACK);
                break;
            }
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GIEditableLayer layer = (GIEditableLayer) item.m_layer;
                // GIEditLayersKeeper.Instance().setMap(map);
                GIEditLayersKeeper.Instance().StartEditing(layer);
                // layer.Serialize();
                mActivity.getEditablelayersDialog().cancel();
            }
        });

        return v;
    }

    public EditableLayersAdapter(Geoinfo activity, int resource,
                                 int textViewResourceId) {
        super(activity, resource, textViewResourceId);
        mActivity = activity;
    }
}
