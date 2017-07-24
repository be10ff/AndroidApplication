package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GITuple;
import ru.tcgeo.application.views.callback.LayerHolderCallback;
import ru.tcgeo.application.views.viewholder.LayerHolder;

/**
 * Created by a_belov on 06.07.15.
 */
public class ReLayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DEFAULT = 1;
    private static final int TYPE_ZERODATA = 2;

    private LayerHolderCallback callback;
    private Context context;
    private List<GITuple> data;

    public ReLayersAdapter(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
        this.data = builder.data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.re_item_layers_list, parent, false);
        return new LayerHolder(v, callback);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DEFAULT) {
            LayerHolder h = (LayerHolder) holder;
            GITuple item = data.get(position);
            h.tvLayerName.setText(item.layer.getName());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) == null) {
            return TYPE_ZERODATA;
        } else {
            return TYPE_DEFAULT;
        }
    }

    public GITuple getItem(int position) {
        return data.get(position);
    }


    public static class Builder {
        private Context context;
        private LayerHolderCallback callback;
        private List<GITuple> data;

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

        public ReLayersAdapter build() {
            return new ReLayersAdapter(this);
        }

    }


}
