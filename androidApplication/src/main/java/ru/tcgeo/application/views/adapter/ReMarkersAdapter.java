package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.views.callback.MarkerHolderCallback;
import ru.tcgeo.application.views.callback.ZeroDataHolderCallback;
import ru.tcgeo.application.views.viewholder.MarkerHolder;
import ru.tcgeo.application.views.viewholder.ZeroDataHolder;

/**
 * Created by a_belov on 06.07.15.
 */
public class ReMarkersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_DEFAULT = 1;
    private static final int TYPE_ZERODATA = 2;

    private MarkerHolderCallback callback;
    private Context context;
    private List<Marker> data;

    public ReMarkersAdapter(Builder builder) {
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
                    .inflate(R.layout.item_marker_list, parent, false);
            return new MarkerHolder(v, callback);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DEFAULT) {
            MarkerHolder h = (MarkerHolder) holder;
            Marker item = data.get(position);
            h.tvDescription.setText(item.name);
            if (item.selected) {
                h.ivDirecton.setVisibility(View.VISIBLE);
            } else {
                h.ivDirecton.setVisibility(View.INVISIBLE);
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
            return TYPE_ZERODATA;
        } else {
            return TYPE_DEFAULT;
        }
    }

    public Marker getItem(int position) {
        return data.get(position);
    }

    public void setSelected(int position, boolean selected) {
        for (Marker marker : data) {
            marker.selected = false;
        }
        data.get(position).selected = selected;
        notifyDataSetChanged();
    }

    public static class Builder {
        private Context context;
        private MarkerHolderCallback callback;
        private List<Marker> data;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(MarkerHolderCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(List<Marker> data) {
            this.data = data;
            return this;
        }

        public ReMarkersAdapter build() {
            return new ReMarkersAdapter(this);
        }

    }


}
