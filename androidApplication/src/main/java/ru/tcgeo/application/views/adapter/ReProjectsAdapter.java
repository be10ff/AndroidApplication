package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.views.callback.ProjectsHolderCallback;
import ru.tcgeo.application.views.callback.ZeroDataHolderCallback;
import ru.tcgeo.application.views.viewholder.ProjectHolder;
import ru.tcgeo.application.views.viewholder.ZeroDataHolder;

/**
 * Created by a_belov on 06.07.15.
 */
public class ReProjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DEFAULT = 1;
    private static final int TYPE_ZERODATA = 2;

    private ProjectsHolderCallback callback;
    private Context context;
    private List<GIProjectProperties> data;

    public ReProjectsAdapter(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
        this.data = builder.data;
        if (data.isEmpty()) {
            data.add(null);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ZERODATA) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_zero_data, parent, false);
            return new ZeroDataHolder(v, new ZeroDataHolderCallback() {
                @Override
                public void onClick(ZeroDataHolder holder) {
                    callback.onClose();
                }
            });
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_projects_list, parent, false);
            return new ProjectHolder(v, callback);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DEFAULT) {
            ProjectHolder h = (ProjectHolder) holder;
            GIProjectProperties item = data.get(position);
            h.tvProjectName.setText(item.m_name);
            h.tvFilePath.setText(item.m_path);
            h.tvDescription.setText(item.m_decription);

            if (((Geoinfo) context).getMap() != null) {
                if (item.m_path.equalsIgnoreCase(((Geoinfo) context).getMap().ps.m_path)) {
                    h.ivLoaded.setImageBitmap(null);
                    h.tvProjectName.setEnabled(true);
                    h.tvProjectName.setTextColor(Color.DKGRAY);
                    h.ivLoaded.setImageResource(R.drawable.project_mark);
                } else {
                    h.ivLoaded.setImageBitmap(null);
                    h.tvProjectName.setEnabled(false);
                    h.tvProjectName.setTextColor(Color.GRAY);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public GIProjectProperties getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) == null) {
            return TYPE_ZERODATA;
        } else {
            return TYPE_DEFAULT;
        }
    }

    public static class Builder {
        private Context context;
        private ProjectsHolderCallback callback;
        private List<GIProjectProperties> data;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(ProjectsHolderCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(List<GIProjectProperties> data) {
            this.data = data;
            return this;
        }

        public ReProjectsAdapter build() {
            return new ReProjectsAdapter(this);
        }

    }


}
