package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.views.callback.MarkerCallback;
import ru.tcgeo.application.views.viewholder.MarkerHolder;

/**
 * Created by a_belov on 06.07.15.
 */
public class ReMarkersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Geoinfo mActivity;
    private MarkerCallback callback;
    private Context context;
    private List<Marker> data;

    public ReMarkersAdapter(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
        this.data = builder.data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_attriutes_header, parent, false);
        return new MarkerHolder(v, callback);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MarkerHolder h = (MarkerHolder) holder;
        Marker item = data.get(position);
        h.tvDescription.setText(item.description);
        if (item.selected) {
            h.ivDirecton.setVisibility(View.VISIBLE);
        } else {
            h.ivDirecton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class Builder {
        private Context context;
        private MarkerCallback callback;
        private List<Marker> data;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(MarkerCallback callback) {
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
