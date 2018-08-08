package ru.tcgeo.application.views.adapter;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.Collections;
import java.util.List;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.gilib.layer.GILayer;
import ru.tcgeo.application.gilib.layer.GISQLLayer;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.views.callback.LayerHolderCallback;
import ru.tcgeo.application.views.viewholder.LayerHolder;
import ru.tcgeo.application.views.viewholder.ProjectLayerHeaderHolder;
import ru.tcgeo.application.views.viewholder.SqliteLayerHolder;
import ru.tcgeo.application.views.viewholder.XmlLayerHolder;
import ru.tcgeo.application.views.viewholder.helper.ItemTouchHelperAdapter;
import ru.tcgeo.application.views.viewholder.helper.OnStartDragListener;
import ru.tcgeo.application.wkt.GIGPSPointsLayer;


/**
 * Created by a_belov on 06.07.15.
 */
public class ReLayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_ZERODATA = 2;
    public static final int TYPE_XML = 3;
    public static final int TYPE_GROUP = 4;
    public static final int TYPE_SQL = 5;

    private LayerHolderCallback callback;
    private OnStartDragListener listener;
    private Context context;
    private List<GILayer> data;
    private GIProjectProperties project;
    private boolean header;

    public ReLayersAdapter(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
        this.listener = builder.listener;
        this.data = builder.data;
        this.header = builder.header;
        if (header) {
            data.add(0, null);
        }
        this.project = builder.project;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_GROUP) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layers_project_holder, parent, false);
            return new ProjectLayerHeaderHolder(v, callback);
        } else if (viewType == TYPE_XML) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layers_xml_list, parent, false);
            return new XmlLayerHolder(v, callback);
        } else if (viewType == TYPE_SQL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layers_sqlite_list, parent, false);
            return new SqliteLayerHolder(v, callback);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layers_list, parent, false);
            return new LayerHolder(v, callback);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_GROUP) {
            ProjectLayerHeaderHolder headerHolder = (ProjectLayerHeaderHolder) holder;
            headerHolder.etProjectName.setText(project.m_name);
            headerHolder.tvFilePath.setText(project.m_path);
            headerHolder.etDescription.setText(project.m_decription);

        } else {
            LayerHolder h = (LayerHolder) holder;
            h.removeListeners();
            GILayer item = data.get(position);

            h.flReOrder.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        listener.onStartDrag(holder);
                    }
                    return false;
                }
            });


            h.etLayerName.setText(item.getName());
            h.cbLayerVisibility.setChecked(item.m_layer_properties.m_enabled);
            h.tvFilePath.setText(item.m_layer_properties.m_source.GetAbsolutePath());
            if (new File(item.m_layer_properties.m_source.GetAbsolutePath()).exists()) {
                h.ivFileExsist.setImageResource(R.drawable.project_mark);
            } else {
                h.ivFileExsist.setImageResource(R.drawable.project_mark_fail);
            }
            h.rsbScaleRange.setSelectedMaxValue(MapUtils.scale2Z(item.m_layer_properties.m_range.m_to));
            h.rsbScaleRange.setSelectedMinValue(MapUtils.scale2Z(item.m_layer_properties.m_range.m_from));

//            tvScaleRange.setText(tvScaleRange.getContext().getString(R.string.scale_range_format, MapUtils.z2scale((Integer) minValue), MapUtils.z2scale((Integer)maxValue)));

            h.tvScaleRange.setText(context.getString(R.string.scale_range_format, Math.round(item.m_layer_properties.m_range.m_from), Math.round(item.m_layer_properties.m_range.m_to)));

            h.ivRemove.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) ==
                            MotionEvent.ACTION_DOWN) {
                        listener.onStartSwipe(holder);
                    }
                    return false;
                }
            });

            if (getItemViewType(position) == TYPE_SQL) {
                SqliteLayerHolder sqlHolder = (SqliteLayerHolder) holder;
                h.flMore.setVisibility(View.VISIBLE);
                h.flMarkers.setVisibility(View.GONE);

                if (item.m_layer_properties.m_type == GILayer.GILayerType.SQL_YANDEX_LAYER) {
                    sqlHolder.rbYandex.toggle();
                } else if (item.m_layer_properties.m_type == GILayer.GILayerType.SQL_LAYER) {
                    sqlHolder.rbGoogle.toggle();
                }


                if (item.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.AUTO) {
                    sqlHolder.rbAuto.toggle();
                } else if (item.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.SMART) {
                    sqlHolder.rbSmart.toggle();
                } else if (item.m_layer_properties.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE) {
                    sqlHolder.rbAdaptive.toggle();
                }

                h.rsbScaleRange.setSelectedMaxValue(item.m_layer_properties.m_sqldb.m_max_z);
                h.rsbScaleRange.setSelectedMinValue(item.m_layer_properties.m_sqldb.m_min_z);

                sqlHolder.rsbRatio.setSelectedMaxValue(item.m_layer_properties.m_sqldb.mRatio);
            } else if (getItemViewType(position) == TYPE_XML) {
                h.flMore.setVisibility(View.VISIBLE);
                h.flMarkers.setVisibility(View.VISIBLE);


                XmlLayerHolder xmlHolder = (XmlLayerHolder) holder;
                if (item instanceof GIGPSPointsLayer) {
                    xmlHolder.isMarkersSource = ((GIGPSPointsLayer) item).isMarkersSource();
                    if (xmlHolder.isMarkersSource) {
                        xmlHolder.ivMarkersSource.setImageResource(R.drawable.ic_markers_enable);

                    } else {
                        xmlHolder.ivMarkersSource.setImageResource(R.drawable.ic_markers_disable);
                    }
                }
                xmlHolder.rsbStrokeWidth.setSelectedMaxValue(item.m_layer_properties.m_style.m_lineWidth);

                if (item.m_layer_properties.m_style != null && item.m_layer_properties.m_style.m_colors != null) {
                    for (GIColor color : item.m_layer_properties.m_style.m_colors) {
                        if (color.m_description.equalsIgnoreCase("line")) {
                            xmlHolder.vStrokeColor.setBackgroundColor(color.Get());
                        } else if (color.m_description.equalsIgnoreCase("fill")) {
                            xmlHolder.vFillColor.setBackgroundColor(color.Get());
                        }
                    }
                }

                if (item instanceof GIEditableLayer) {
                    if (item.m_layer_properties.editable != null) {
                        xmlHolder.cbPoiLayer.setChecked(item.m_layer_properties.editable.active);
                        if (item.m_layer_properties.editable.enumType == GISQLLayer.EditableType.POI) {
                            xmlHolder.rbPoint.toggle();
                        } else if (item.m_layer_properties.editable.enumType == GISQLLayer.EditableType.LINE) {
                            xmlHolder.rbLine.toggle();
                        } else if (item.m_layer_properties.editable.enumType == GISQLLayer.EditableType.POLYGON) {
                            xmlHolder.rbPolygon.toggle();
                        } else {
                            xmlHolder.rgEditableType.clearCheck();
                        }
                    }
                }


            } else {
                h.flMore.setVisibility(View.GONE);
                h.flMarkers.setVisibility(View.GONE);
            }

            h.initListeners();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) == null) {
            return TYPE_GROUP;
        } else if (data.get(position).type == GILayer.GILayerType.XML) {
            return TYPE_XML;
        } else if (data.get(position).type == GILayer.GILayerType.SQL_LAYER
                || data.get(position).type == GILayer.GILayerType.SQL_YANDEX_LAYER
                || data.get(position).type == GILayer.GILayerType.FOLDER) {
            return TYPE_SQL;
        } else {
            return TYPE_DEFAULT;
        }
    }

    public GILayer getItem(int position) {
        return data.get(position);
    }

    public void addItem(GILayer tuple) {
        data.add(tuple);
        notifyItemInserted(data.size() - 1);
    }

    public void addItemAt(GILayer tuple) {
        int position = tuple.position;
        if (getItemViewType(0) == TYPE_GROUP) {
            position++;
        }
        data.add(position, tuple);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(data, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        callback.onMove(data.get(fromPosition), data.get(toPosition));
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        callback.onRemove(data.get(position));
        data.remove(position);
        notifyItemRemoved(position);


    }


    public static class Builder {
        OnStartDragListener listener;
        private Context context;
        private LayerHolderCallback callback;
        private List<GILayer> data;
        private GIProjectProperties project;
        private boolean header;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder callback(LayerHolderCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder dragListener(OnStartDragListener listener) {
            this.listener = listener;
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

        public Builder header(boolean header) {
            this.header = header;
            return this;
        }

        public ReLayersAdapter build() {
            return new ReLayersAdapter(this);
        }

    }


}
