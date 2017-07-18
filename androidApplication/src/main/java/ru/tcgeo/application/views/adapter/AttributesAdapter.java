package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.Attribute;
import ru.tcgeo.application.views.callback.AttributesCallback;
import ru.tcgeo.application.views.viewholder.AttributesHolder;

/**
 * Created by artem on 14.07.17.
 */

public class AttributesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private AttributesCallback callback;
    private List<Attribute> data;

    public AttributesAdapter(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
        this.data = builder.data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_attriute, parent, false);
        return new AttributesHolder(context, v, callback);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AttributesHolder h = (AttributesHolder) holder;
        Attribute item = data.get(position);
        h.etName.setText(item.name);
        h.etValue.setText(item.value);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class Builder {

        private Context context;
        private AttributesCallback callback;
        private List<Attribute> data;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(AttributesCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(List<Attribute> data) {
            this.data = data;
            return this;
        }

        public AttributesAdapter build() {
            return new AttributesAdapter(this);
        }

    }
}
