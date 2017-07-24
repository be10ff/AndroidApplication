package ru.tcgeo.application.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.views.adapter.ReMarkersAdapter;
import ru.tcgeo.application.views.callback.MarkerCallback;
import ru.tcgeo.application.views.callback.MarkerHolderCallback;
import ru.tcgeo.application.views.viewholder.MarkerHolder;

/**
 * Created by a_belov on 23.07.15.
 */
public class ReMarkersDialog extends Dialog {

    @Bind(R.id.rvMarkers)
    RecyclerView rvMarkers;

    ReMarkersAdapter adapter;

    private MarkerCallback callback;
    private List<Marker> data;
    private Context context;


    public ReMarkersDialog(Builder builder) {
        super(builder.context, true, null);
        this.callback = builder.callback;
        this.context = builder.context;
        this.data = builder.data;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.re_markers_dialog);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        adapter = new ReMarkersAdapter.Builder(context)
                .callback(new MarkerHolderCallback() {
                    @Override
                    public void onGoToClick(MarkerHolder holder) {
                        callback.onGoToClick(adapter.getItem(holder.getAdapterPosition()));
                    }

                    @Override
                    public void onShowDirectiponClick(MarkerHolder holder) {
                        callback.onShowDirectiponClick(adapter.getItem(holder.getAdapterPosition()), !adapter.getItem(holder.getAdapterPosition()).selected);
                        adapter.setSelected(holder.getAdapterPosition(), !adapter.getItem(holder.getAdapterPosition()).selected);
                        dismiss();
                    }

                    @Override
                    public void onClose() {
                        dismiss();
                    }
                })
                .data(data)
                .build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);

        rvMarkers.setLayoutManager(layoutManager);
        rvMarkers.addItemDecoration(dividerItemDecoration);
        rvMarkers.setAdapter(adapter);
    }


    public static class Builder {

        private MarkerCallback callback;
        private List<Marker> data;
        private Context context;

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

        public ReMarkersDialog build() {
            return new ReMarkersDialog(this);
        }
    }

}
