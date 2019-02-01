package ru.tcgeo.application.views.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.tcgeo.application.R;
import ru.tcgeo.application.views.callback.DirCallback;
import ru.tcgeo.application.views.viewholder.DirHolder;

public class DirAdapter extends RecyclerView.Adapter {
    private static final int TYPE_PARENT = 1;
    private static final int TYPE_DIR = 2;
    private static final int TYPE_FILE = 3;

    List<File> data;
    DirCallback callback;

    public DirAdapter(DirCallback callback) {
        this.callback = callback;
        data = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dir, parent, false);
        return new DirHolder(v, callback);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DirHolder h = (DirHolder) holder;
        h.bind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else if (data.get(position).isDirectory()) {
            return 2;
        } else {
            return 3;
        }
    }

    public File getItem(int position) {
        return data.get(position);
    }

    public void setData(List<File> files) {
        data.clear();
//        data.add(parent);
        data.addAll(files);
        notifyDataSetChanged();
    }


}
