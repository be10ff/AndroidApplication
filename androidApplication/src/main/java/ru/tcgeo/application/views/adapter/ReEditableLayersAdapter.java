package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.tcgeo.application.R;
import ru.tcgeo.application.data.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.views.callback.EditableLayerHolderCallback;
import ru.tcgeo.application.views.callback.ZeroDataHolderCallback;
import ru.tcgeo.application.views.viewholder.EdiableLayerHolder;
import ru.tcgeo.application.views.viewholder.ZeroDataHolder;

/**
 * Created by a_belov on 06.07.15.
 */
public class ReEditableLayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DEFAULT = 1;
    private static final int TYPE_ZERODATA = 2;

    private EditableLayerHolderCallback callback;
    private Context context;
    private List<GIEditableLayer> data;

    public ReEditableLayersAdapter(Builder builder) {
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
                    .inflate(R.layout.item_editable_layers_list, parent, false);
            return new EdiableLayerHolder(v, callback);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DEFAULT) {
            EdiableLayerHolder h = (EdiableLayerHolder) holder;
            GIEditableLayer item = data.get(position);
            h.tvEditableLayerName.setText(item.getName());
            switch (item.m_Status) {
                case UNEDITED: {
                    h.tvEditableLayerName.setTextColor(Color.BLACK);
                    break;
                }
                case EDITED: {
                    h.tvEditableLayerName.setTextColor(Color.BLUE);
                    break;
                }
                case UNSAVED: {
                    h.tvEditableLayerName.setTextColor(Color.RED);
                    break;
                }
                default: {
                    h.tvEditableLayerName.setTextColor(Color.BLACK);
                    break;
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public GIEditableLayer getItem(int position) {
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
        private EditableLayerHolderCallback callback;
        private List<GIEditableLayer> data;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(EditableLayerHolderCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(List<GIEditableLayer> data) {
            this.data = data;
            return this;
        }

        public ReEditableLayersAdapter build() {
            return new ReEditableLayersAdapter(this);
        }

    }


}
