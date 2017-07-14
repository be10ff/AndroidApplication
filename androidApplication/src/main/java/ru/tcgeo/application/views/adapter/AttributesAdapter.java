package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.AttributesCallback;
import ru.tcgeo.application.views.viewholder.AttributesHolder;

/**
 * Created by artem on 14.07.17.
 */

public class AttributesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private AttributesCallback callback;

    public AttributesAdapter(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_attriute, parent, false);
        return new AttributesHolder(context, v, callback);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class Builder {

        private Context context;
        private AttributesCallback callback;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(AttributesCallback callback) {
            this.callback = callback;
            return this;
        }

        public AttributesAdapter build() {
            return AttributesAdapter(this);
        }

    }
}
