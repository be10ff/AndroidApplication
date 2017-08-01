package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GITuple;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.views.callback.LayerHolderCallback;
import ru.tcgeo.application.views.viewholder.LayerHeaderHolder;
import ru.tcgeo.application.views.viewholder.LayerHolder;
import ru.tcgeo.application.views.viewholder.SqliteLayerHolder;


/**
 * Created by a_belov on 06.07.15.
 */
public class ReLayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DEFAULT = 1;
    private static final int TYPE_ZERODATA = 2;
    private static final int TYPE_XML = 3;
    private static final int TYPE_GROUP = 4;
    private static final int TYPE_SQL = 5;

    private LayerHolderCallback callback;
    private Context context;
    private List<GITuple> data;
    private GIProjectProperties project;

    public ReLayersAdapter(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
        this.data = builder.data;
        this.project = builder.project;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_GROUP) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layers_header, parent, false);
            return new LayerHeaderHolder(v, callback);
        } else if (viewType == TYPE_XML) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layers_xml_list, parent, false);
            return new SqliteLayerHolder(v, callback);
        } else if (viewType == TYPE_SQL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layers_sqlite_list, parent, false);
            return new LayerHolder(v, callback);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layers_list, parent, false);
            return new SqliteLayerHolder(v, callback);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_GROUP) {
            LayerHeaderHolder headerHolder = (LayerHeaderHolder) holder;
            headerHolder.etProjectName.setText(project.m_name);
            headerHolder.tvFilePath.setText(project.m_path);
            headerHolder.etDescription.setText(project.m_decription);
        } else {
            LayerHolder h = (LayerHolder) holder;
            GITuple item = data.get(position);

            h.tvLayerName.setText(item.layer.getName());
            h.cbLayerVisibility.setChecked(item.visible);
            h.tvFilePath.setText(item.layer.m_layer_properties.m_source.GetAbsolutePath());
            if (new File(item.layer.m_layer_properties.m_source.GetAbsolutePath()).exists()) {
                h.ivFileExsist.setImageResource(R.drawable.project_mark);
            } else {
                h.ivFileExsist.setImageResource(R.drawable.project_mark_fail);
            }
            h.rsbScaleRange.setSelectedMaxValue(MapUtils.scale2Z(item.scale_range.getMax()));
            h.rsbScaleRange.setSelectedMinValue(MapUtils.scale2Z(item.scale_range.getMin()));
            h.tvScaleRange.setText(context.getString(R.string.scale_range_format, (int) Math.round(1 / item.scale_range.getMin()), (int) Math.round(1 / item.scale_range.getMax())));

            if (getItemViewType(position) == TYPE_SQL) {
                SqliteLayerHolder sqlHolder = (SqliteLayerHolder) holder;

            }
            if (getItemViewType(position) == TYPE_XML) {
//                OldXMLLayerHolder xh = (OldXMLLayerHolder) holder;
//                GITuple xitem = data.get(position);
//                h.tvLayerName.setText(item.layer.getName());
//                h.cbLayerVisibility.setChecked(item.visible);
//                h.cbMarkersSource.setVisibility(View.VISIBLE);
//                h.cbMarkersSource.setChecked(((GIGPSPointsLayer) item.layer).isMarkersSource());
            } else if (getItemViewType(position) == TYPE_DEFAULT) {
//                SqliteLayerHolder sh = (SqliteLayerHolder) holder;
//                GITuple sitem = data.get(position);
//                h.tvLayerName.setText(item.layer.getName());
//                h.cbLayerVisibility.setChecked(item.visible);
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) == null) {
            return TYPE_GROUP;
        } else if (data.get(position).layer.type_ == GILayer.GILayerType.XML) {
            return TYPE_XML;
        } else if (data.get(position).layer.type_ == GILayer.GILayerType.SQL_LAYER || data.get(position).layer.type_ == GILayer.GILayerType.SQL_YANDEX_LAYER) {
            return TYPE_SQL;
        } else {
            return TYPE_DEFAULT;
        }
    }

    public GITuple getItem(int position) {
        return data.get(position);
    }

    public void addItem(GITuple tuple) {
        data.add(tuple);
        notifyItemInserted(data.size() - 1);
    }

    public void addItemAt(GITuple tuple) {
        data.add(tuple.position, tuple);
        notifyItemInserted(data.size() - 1);
    }


    public static class Builder {
        private Context context;
        private LayerHolderCallback callback;
        private List<GITuple> data;
        private GIProjectProperties project;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(LayerHolderCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(List<GITuple> data) {
            this.data = data;
            return this;
        }

        public Builder project(GIProjectProperties project) {
            this.project = project;
            return this;
        }

        public ReLayersAdapter build() {
            return new ReLayersAdapter(this);
        }

    }


}
