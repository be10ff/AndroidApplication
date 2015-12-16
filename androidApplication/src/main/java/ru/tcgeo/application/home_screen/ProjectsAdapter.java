package ru.tcgeo.application.home_screen;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.tcgeo.application.App;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.utils.ProjectChangedEvent;

/**
 * Created by a_belov on 06.07.15.
 */
public class ProjectsAdapter extends ArrayAdapter<ProjectsAdapterItem> {

    Geoinfo mActivity;
    SharedPreferences sp;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ProjectsAdapterItem item = getItem(position);
        View v = LayoutInflater.from(getContext()).inflate(
                R.layout.project_selector_list_item, null);
        TextView text_name = (TextView) v
                .findViewById(R.id.project_list_item_name);
        TextView text_path = (TextView) v
                .findViewById(R.id.project_list_item_path);
        ImageView iv = (ImageView) v.findViewById(R.id.imageViewStatus);

        text_name.setText(item.m_project_settings.m_name);
        text_path.setText(item.m_project_settings.m_path);


        if (mActivity.getMap() != null) {
            if (!item.m_project_settings.m_path
                    .equalsIgnoreCase(mActivity.getMap().ps.m_path)) {
                iv.setImageBitmap(null);
            } else {
                text_name.setEnabled(false);
                text_name.setTextColor(Color.GRAY);
            }
        }


        text_name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!item.m_project_settings.m_path.equals(mActivity.getMap().ps.m_path)) {
                    mActivity.getMap().Clear();
                    mActivity.LoadPro(item.m_project_settings.m_path);
                    mActivity.getMap().UpdateMap();
                    sp = mActivity.getPreferences(mActivity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(mActivity.SAVED_PATH,
                            item.m_project_settings.m_path);
                    editor.apply();
                    editor.commit();
//                    mActivity.getProjectsDialog().cancel();
                    App.getInstance().getEventBus().post(new ProjectChangedEvent());
                }
            }
        });

        return v;
    }

    public ProjectsAdapter(Geoinfo activity, int resource,
                           int textViewResourceId) {
        super(activity, resource, textViewResourceId);
        mActivity = activity;
    }
}
