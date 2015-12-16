package ru.tcgeo.application.home_screen;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;

/**
 * Created by a_belov on 06.07.15.
 */
public class LayersAdapter extends ArrayAdapter<LayersAdapterItem> {
    Geoinfo mActivity;
//    View.OnClickListener mListener;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayersAdapterItem item = getItem(position);
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.layers_list_item, null);
        TextView name = ((TextView) v.findViewById(R.id.layers_list_item_text));
        name.setText(item.m_tuple.layer.getName());
//        name.setOnClickListener(mListener);
        CheckBox checkbox = (CheckBox) v.findViewById(R.id.layers_list_item_switch);
        checkbox.setChecked(item.m_tuple.visible);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                item.m_tuple.visible = isChecked;
                mActivity.getMap().UpdateMap();
            }
        });


        return v;
    }

    public LayersAdapter(Geoinfo activity, int resource,
                         int textViewResourceId/*,View.OnClickListener listener*/) {
        super(activity, resource, textViewResourceId);
        mActivity = activity;
//        mListener = listener;
    }
}
