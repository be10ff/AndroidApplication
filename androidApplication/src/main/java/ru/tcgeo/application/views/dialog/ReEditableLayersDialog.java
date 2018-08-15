package ru.tcgeo.application.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.views.adapter.ReEditableLayersAdapter;
import ru.tcgeo.application.views.callback.EditableLayerCallback;
import ru.tcgeo.application.views.callback.EditableLayerHolderCallback;
import ru.tcgeo.application.views.viewholder.EdiableLayerHolder;

import static ru.tcgeo.application.gilib.layer.GIEditableLayer.GIEditableLayerStatus.EDITED;
import static ru.tcgeo.application.gilib.layer.GIEditableLayer.GIEditableLayerStatus.UNSAVED;

/**
 * Created by a_belov on 23.07.15.
 */
public class ReEditableLayersDialog extends Dialog {

    @BindView(R.id.rvEditableLayers)
    RecyclerView rvEditableLayers;
    @BindView(R.id.fabStopEdit)
    ImageView fabStopEdit;
    private Context context;
    private List<GIEditableLayer> data;
    private EditableLayerCallback callback;
    private boolean inEditing;
    private ReEditableLayersAdapter adapter;

    public ReEditableLayersDialog(Builder builder) {
        super(builder.context, true, null);
        this.context = builder.context;
        this.callback = builder.callback;
        this.data = builder.data;

    }

    @OnClick(R.id.fabStopEdit)
    public void stopEdit() {
        inEditing = false;
        callback.onStopEdit();
        dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_editable_layers);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFab();

        adapter = new ReEditableLayersAdapter.Builder(getContext())
                .callback(new EditableLayerHolderCallback() {

                    @Override
                    public void onStartEdit(EdiableLayerHolder holder) {
                        if (!inEditing) {
                            callback.onStartEdit(adapter.getItem(holder.getAdapterPosition()));
                            dismiss();
                        }
                    }

                    @Override
                    public void onClose() {
                        dismiss();
                    }
                })
                .data(data)
                .build();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);

        rvEditableLayers.setLayoutManager(layoutManager);
//        rvEditableLayers.addItemDecoration(dividerItemDecoration);
        rvEditableLayers.setAdapter(adapter);

    }

    private void setFab() {
        inEditing = false;
        if (data != null) {
            for (GIEditableLayer layer : data) {
                if (layer != null && (layer.m_Status == EDITED || layer.m_Status == UNSAVED)) {
                    inEditing = true;
                    break;
                }
            }
        }
        if (inEditing) {
            fabStopEdit.setVisibility(View.VISIBLE);
        } else {
            fabStopEdit.setVisibility(View.GONE);
        }
    }


    public static class Builder {

        private Context context;
        private List<GIEditableLayer> data;
        private EditableLayerCallback callback;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder data(List<GIEditableLayer> data) {
            this.data = data;
            return this;
        }

        public Builder callback(EditableLayerCallback callback) {
            this.callback = callback;
            return this;
        }

        public ReEditableLayersDialog build() {
            return new ReEditableLayersDialog(this);
        }
    }

}
