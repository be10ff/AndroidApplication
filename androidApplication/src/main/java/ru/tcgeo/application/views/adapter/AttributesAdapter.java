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
import ru.tcgeo.application.views.viewholder.AttributesAddHolder;
import ru.tcgeo.application.views.viewholder.AttributesHeaderHolder;
import ru.tcgeo.application.views.viewholder.AttributesHolder;

/**
 * Created by artem on 14.07.17.
 */

public class AttributesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DEFAULT = 1;
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ADD_NEW = 3;

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

        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_edit_attriutes_header, parent, false);
            return new AttributesHeaderHolder(v);
        } else if (viewType == TYPE_ADD_NEW) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_add_attriute, parent, false);
            return new AttributesAddHolder(v, callback);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_edit_attriute, parent, false);
            return new AttributesHolder(context, v, callback);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_DEFAULT) {
            AttributesHolder h = (AttributesHolder) holder;
            h.bind(data.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == 0)) {
            return TYPE_HEADER;
        } else if (data.get(position) == null) {
            return TYPE_ADD_NEW;
        } else {
            return TYPE_DEFAULT;
        }
    }

    public void addAttribute() {
        data.add(data.size() - 1, new Attribute("", ""));
        notifyItemInserted(data.size() - 1);
    }

    public Attribute getItem(int position) {
        return data.get(position);
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
