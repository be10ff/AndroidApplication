package ru.tcgeo.application.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.gilib.layer.GILayer;
import ru.tcgeo.application.gilib.layer.GISQLLayer;
import ru.tcgeo.application.gilib.layer.renderer.GIEditableRenderer;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.utils.ScreenUtils;
import ru.tcgeo.application.view.IFolderItemListener;
import ru.tcgeo.application.views.adapter.ReLayersAdapter;
import ru.tcgeo.application.views.callback.LayerCallback;
import ru.tcgeo.application.views.callback.LayerHolderCallback;
import ru.tcgeo.application.views.viewholder.LayerHolder;
import ru.tcgeo.application.views.viewholder.SqliteLayerHolder;
import ru.tcgeo.application.views.viewholder.XmlLayerHolder;
import ru.tcgeo.application.views.viewholder.helper.OnStartDragListener;
import ru.tcgeo.application.views.viewholder.helper.SimpleItemTouchHelperCallback;
import ru.tcgeo.application.wkt.GIGPSPointsLayer;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by a_belov on 23.07.15.
 */
public class ReSettingsDialog extends Dialog implements IFolderItemListener, OnStartDragListener {

    @BindView(R.id.rvLayers)
    RecyclerView rvLayers;

    ReLayersAdapter adapter;

    private LayerCallback callback;

    private List<GILayer> data;

    private GIProjectProperties project;

    private Context context;

    private ItemTouchHelper mItemTouchHelper;


    public ReSettingsDialog(Builder builder) {
        super(builder.context);
        this.callback = builder.callback;
        this.context = builder.context;
        this.data = builder.data;
        this.project = builder.project;
    }

    @OnClick(R.id.fabAdd)
    public void onAddClick() {
        OpenFileDialog dlg = new OpenFileDialog();
        dlg.setIFolderItemListener(ReSettingsDialog.this);
        dlg.show(((FragmentActivity) context).getSupportFragmentManager(), "tag");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_settings);
        ButterKnife.bind(this);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ReLayersAdapter.Builder(context)
                .callback(new LayerHolderCallback() {
                    @Override
                    public void onMarkersSourceCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        callback.onMarkersSourceCheckChanged(tuple, isChecked);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onVisibilityCheckChanged(RecyclerView.ViewHolder holder, boolean isChecked) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        LayerHolder h = (LayerHolder) holder;
                        GILayer.Builder builder = new GILayer.Builder(tuple);
                        builder.enabled(h.cbLayerVisibility.isChecked());
                        builder.build();
                        callback.onImmediatelyChange();
                    }

                    @Override
                    public void onLayerName(RecyclerView.ViewHolder holder) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        GILayer.Builder builder = new GILayer.Builder(tuple);
                        LayerHolder h = (LayerHolder) holder;
                        builder.name(h.etLayerName.getText().toString());
                        builder.build();
                    }

                    @Override
                    public void onScaleRange(RecyclerView.ViewHolder holder) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        GILayer.Builder builder = new GILayer.Builder(tuple);
                        LayerHolder h = (LayerHolder) holder;
//                        builder.sqldbMaxZ((int) h.rsbScaleRange.getSelectedMaxValue());
//                        builder.sqldbMinZ((int) h.rsbScaleRange.getSelectedMinValue());
//                        builder.rangeFrom(MapUtils.z2scale((int) h.rsbScaleRange.getSelectedMaxValue()));
//                        builder.rangeTo(MapUtils.z2scale((int) h.rsbScaleRange.getSelectedMinValue()));

                        builder.sqldbMaxZ((int) h.rsbScaleRange.getSelectedMinValue());
                        builder.sqldbMinZ((int) h.rsbScaleRange.getSelectedMaxValue());
                        builder.rangeFrom(MapUtils.z2scale((int) h.rsbScaleRange.getSelectedMinValue()));
                        builder.rangeTo(MapUtils.z2scale((int) h.rsbScaleRange.getSelectedMaxValue()));

                        builder.build();
                        callback.onImmediatelyChange();
                    }

                    @Override
                    public void onRemove(RecyclerView.ViewHolder holder) {
                        callback.onRemoveLayer(data.get(holder.getAdapterPosition()));
                        adapter.onItemDismiss(holder.getAdapterPosition());
                    }

                    @Override
                    public void onMove(GILayer fromPosition, GILayer toPosition) {
                        callback.onMoveLayer(fromPosition, toPosition);
                    }

                    @Override
                    public void onZoomType(RecyclerView.ViewHolder holder, GISQLLayer.GISQLiteZoomingType type) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        GILayer.Builder builder = new GILayer.Builder(tuple);
                        builder.sqldbZoomType(type);
                        builder.build();
                        callback.onImmediatelyChange();
                    }

                    @Override
                    public void onProjection(RecyclerView.ViewHolder holder, GISQLLayer.GILayerType type) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        GILayer.Builder builder = new GILayer.Builder(tuple);
                        builder.type(type);
                        builder.build();
                        callback.onImmediatelyChange();
                    }

                    @Override
                    public void onRatio(RecyclerView.ViewHolder holder) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        GILayer.Builder builder = new GILayer.Builder(tuple);
                        SqliteLayerHolder h = (SqliteLayerHolder) holder;
                        builder.sqldbRatio((int) h.rsbRatio.getSelectedMaxValue());
                        builder.build();
                        callback.onImmediatelyChange();
                    }

                    @Override
                    public void onEditable(RecyclerView.ViewHolder holder, GISQLLayer.EditableType type) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        XmlLayerHolder h = (XmlLayerHolder) holder;
                        GIEditableLayer layer = (GIEditableLayer) tuple;
                        if (layer != null) {
                            GILayer.Builder builder = new GILayer.Builder(tuple);
                            builder.editable(type);
                            builder.build();
                        }
                    }

                    @Override
                    public void onSetPoiLayer(RecyclerView.ViewHolder holder, boolean active) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        XmlLayerHolder h = (XmlLayerHolder) holder;
                        GIEditableLayer layer = (GIEditableLayer) tuple;
                        if (layer != null) {
                            callback.onPOILayer(layer);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFillColor(final RecyclerView.ViewHolder holder) {
                        GILayer tuple = adapter.getItem(holder.getAdapterPosition());
                        XmlLayerHolder h = (XmlLayerHolder) holder;
                        final GIGPSPointsLayer layer = (GIGPSPointsLayer) tuple;

                        if (layer.m_layer_properties.m_style != null && layer.m_layer_properties.m_style.m_colors != null) {
                            for (final GIColor color : layer.m_layer_properties.m_style.m_colors) {
                                if (color.m_description.equalsIgnoreCase("fill")) {
                                    new AmbilWarnaDialog(getContext(), color.Get(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                        @Override
                                        public void onOk(AmbilWarnaDialog dialog, int new_color) {
                                            color.set(new_color);
                                            ((GIEditableRenderer) layer.renderer()).m_style.m_paint_brush.setColor(new_color);
                                            adapter.notifyItemChanged(holder.getAdapterPosition());
                                        }

                                        @Override
                                        public void onCancel(AmbilWarnaDialog dialog) {
                                        }
                                    }).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onStrokeColor(final RecyclerView.ViewHolder holder) {
                        GILayer l = adapter.getItem(holder.getAdapterPosition());
                        XmlLayerHolder h = (XmlLayerHolder) holder;
                        final GIGPSPointsLayer layer = (GIGPSPointsLayer) l;

                        if (layer.m_layer_properties.m_style != null && layer.m_layer_properties.m_style.m_colors != null) {
                            for (final GIColor color : layer.m_layer_properties.m_style.m_colors) {
                                if (color.m_description.equalsIgnoreCase("line")) {
                                    new AmbilWarnaDialog(getContext(), color.Get(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                        @Override
                                        public void onOk(AmbilWarnaDialog dialog, int new_color) {
                                            color.set(new_color);
                                            ((GIEditableRenderer) layer.renderer()).m_style.m_paint_pen.setColor(new_color);
                                            adapter.notifyItemChanged(holder.getAdapterPosition());
                                        }

                                        @Override
                                        public void onCancel(AmbilWarnaDialog dialog) {
                                        }
                                    }).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onWidth(RecyclerView.ViewHolder holder) {
                        GILayer l = adapter.getItem(holder.getAdapterPosition());
                        XmlLayerHolder h = (XmlLayerHolder) holder;
                        final GIGPSPointsLayer layer = (GIGPSPointsLayer) l;
                        layer.m_layer_properties.m_style.m_lineWidth = (int) h.rsbStrokeWidth.getSelectedMaxValue();
                        ((GIEditableRenderer) layer.renderer()).m_style.m_paint_pen.setStrokeWidth((int) h.rsbStrokeWidth.getSelectedMaxValue());
                        callback.onImmediatelyChange();
                    }

                })
                .dragListener(this)
                .data(data)
                .header(true)
                .project(project)
                .build();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);

        rvLayers.setLayoutManager(layoutManager);
        rvLayers.addItemDecoration(dividerItemDecoration);
        rvLayers.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvLayers);
    }


    @Override
    public void onStart() {
        super.onStart();

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
        int dialogWidth = (int) (ScreenUtils.getScreenWidth(context) * 0.9f);
        int dialogHeight = (int) (ScreenUtils.getScreenHeight(context) * 0.9f);
        getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @Override
    protected void onStop() {
        super.onStop();
        callback.onImmediatelyChange();
    }

    @Override
    public void OnCannotFileRead(File file) {
        Toast.makeText(context, R.string.file_error,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void OnFileClicked(File file) {
        GILayer result = callback.onAddLayer(file);
        if (result != null) {
            adapter.addItemAt(result);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public static class Builder {

        private LayerCallback callback;
        private List<GILayer> data;
        private GIProjectProperties project;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(LayerCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder data(List<GILayer> data) {
            this.data = data;
            return this;
        }

        public Builder project(GIProjectProperties project) {
            this.project = project;
            return this;
        }

        public ReSettingsDialog build() {
            return new ReSettingsDialog(this);
        }
    }
}
