package ru.tcgeo.application.data.interactors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.gilib.layer.GILayer;
import ru.tcgeo.application.gilib.layer.GISQLLayer;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.gilib.parser.GIPropertiesGroup;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.gilib.parser.GIRange;
import ru.tcgeo.application.gilib.parser.GISQLDB;
import ru.tcgeo.application.view.MapView;

/**
 * Created by abelov on 28.04.16.
 */
public class LoadProjectInteractor {
    private Bitmap wktPointBitmap;
    private Disposable subscription;

    private MapView view;

    public LoadProjectInteractor(Context context) {

        wktPointBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.measure_point);
    }

    public void setView(MapView view) {
        this.view = view;
    }

    public void loadProject(final String path){

        subscription = Flowable.just(path)
                .observeOn(Schedulers.io())
                .map(new Function<String, GIProjectProperties>() {
                    @Override
                    public GIProjectProperties apply(String s) {
                        GIProjectProperties ps = new GIProjectProperties(s);
//                        view.onProject(ps);
                        return ps;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<GIProjectProperties>() {
                    @Override
                    public void accept(GIProjectProperties giProjectProperties) {
                        view.onProject(giProjectProperties);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<GIProjectProperties, Flowable<Layer>>() {
                    @Override
                    public Flowable<Layer> apply(final GIProjectProperties giProjectProperties) {
                        return Flowable.create(new FlowableOnSubscribe<Layer>() {
                            @Override
                            public void subscribe(FlowableEmitter<Layer> emitter) {
                                loadGroup(giProjectProperties, giProjectProperties.m_Group, emitter);
                            }
                        }, BackpressureStrategy.BUFFER);

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Layer>() {
                    @Override
                    public void accept(Layer layer) {
                        view.onLayer(layer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("GEOINFO", throwable.getMessage());
                        view.onError();
                    }
                }, new Action() {
                    @Override
                    public void run() {
                        view.onComplited();
                    }
                });
//                .subscribe(new Subscriber<Layer>() {
//                    @Override
//                    public void onCompleted() {
//                        view.onComplited();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("GEOINFO", e.getMessage());
//                        view.onError();
//                    }
//
//                    @Override
//                    public void onNext(Layer layer) {
//                        view.onLayer(layer);
//                    }
//                });

    }

//    private void loadGroup(GIProjectProperties ps, GIPropertiesGroup current_layer2, Subscriber<? super Layer>  subscriber)
//    {
//        for (GIPropertiesLayer current_layer : current_layer2.m_Entries)
//        {
//            if (current_layer.m_type == GILayer.GILayerType.LAYER_GROUP) {
//                loadGroup(ps, (GIPropertiesGroup) current_layer, subscriber);
//            }
//            if (current_layer.m_type == GILayer.GILayerType.TILE_LAYER) {
//                GILayer layer;
//                if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
//                    layer = GILayer.CreateLayer(
//                            current_layer.m_source.GetLocalPath(),
//                            GILayer.GILayerType.TILE_LAYER);
//                    layer.setName(current_layer.m_name);
//                    layer.m_layer_properties = current_layer;
//                    subscriber.onNext(new Layer(layer,
//                            current_layer.m_range,
//                            current_layer.m_enabled));
//                } else {
//                    continue;
//                }
//
//            }
//            if (current_layer.m_type == GILayer.GILayerType.ON_LINE) {
//                GILayer layer;
//                if (current_layer.m_source.m_location.equalsIgnoreCase("text")) {
//                    layer = GILayer.CreateLayer(
//                            current_layer.m_source.GetRemotePath(),
//                            GILayer.GILayerType.ON_LINE);
//                    layer.setName(current_layer.m_name);
//                    layer.m_layer_properties = current_layer;
//                    subscriber.onNext(new Layer(layer,
//                            current_layer.m_range,
//                            current_layer.m_enabled));
//                } else {
//                    continue;
//                }
//
//            }
//            if (current_layer.m_type == GILayer.GILayerType.SQL_LAYER)
//            {
//                GILayer layer;
//                if (current_layer.m_source.m_location.equalsIgnoreCase("text"))
//                {
//                    layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayer.GILayerType.SQL_LAYER);
//                    layer.setName(current_layer.m_name);
//                    if (current_layer.m_sqldb != null) {
//                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
//                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
//                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
//                        {
//                            ((GISQLLayer) layer).getAvalibleLevels();
//                        }
//                        current_layer.m_sqldb = builder.build();
//                    }
//                    layer.m_layer_properties = current_layer;
//                    subscriber.onNext(new Layer(layer,
//                            current_layer.m_range,
//                            current_layer.m_enabled));
//                }
//                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
//                {
//                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.SQL_LAYER);
//
//                    layer.setName(current_layer.m_name);
//                    if (current_layer.m_sqldb != null) {
//                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
//                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
//
//                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE) {
//                            ((GISQLLayer) layer).getAvalibleLevels();
//                        }
//                        current_layer.m_sqldb = builder.build();
//                    }
//                    layer.m_layer_properties = current_layer;
//                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
//                }
//                else
//                {
//                    continue;
//                }
//
//            }
//            if (current_layer.m_type == GILayer.GILayerType.SQL_YANDEX_LAYER) {
//                GILayer layer;
//                if (current_layer.m_source.m_location.equalsIgnoreCase("text"))
//                {
//                    layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayer.GILayerType.SQL_YANDEX_LAYER);
//                    layer.setName(current_layer.m_name);
//                    if (current_layer.m_sqldb != null) {
//                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
//                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
//                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
//                        {
//                            ((GISQLLayer) layer).getAvalibleLevels();
//                        }
//                        current_layer.m_sqldb = builder.build();
//                    }
//                    layer.m_layer_properties = current_layer;
//                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
//                }
//                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
//                {
//                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.SQL_YANDEX_LAYER);
//                    layer.setName(current_layer.m_name);
//                    if (current_layer.m_sqldb != null) {
//                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
//                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
//                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE) {
//                            ((GISQLLayer) layer).getAvalibleLevels();
//                        }
//                        current_layer.m_sqldb = builder.build();
//                    }
//                    layer.m_layer_properties = current_layer;
//                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
//                }
//                else
//                {
//                    continue;
//                }
//
//            }
//            if (current_layer.m_type == GILayer.GILayerType.FOLDER) {
//                GILayer layer;
//                if (current_layer.m_source.m_location.equalsIgnoreCase("text"))
//                {
//                    layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayer.GILayerType.SQL_YANDEX_LAYER);
//                    layer.setName(current_layer.m_name);
//                    if (current_layer.m_sqldb != null) {
//                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
//                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
//                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
//                        {
//                            ((GISQLLayer) layer).getAvalibleLevels();
//                        }
//                        current_layer.m_sqldb = builder.build();
//                    }
//                    layer.m_layer_properties = current_layer;
//                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
//                }
//                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
//                {
//                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.FOLDER);
//                    layer.setName(current_layer.m_name);
//                    if (current_layer.m_sqldb != null) {
//                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
//                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
//                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE) {
//                            ((GISQLLayer) layer).getAvalibleLevels();
//                        }
//                        current_layer.m_sqldb = builder.build();
//                    }
//                    layer.m_layer_properties = current_layer;
//                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
//                }
//                else
//                {
//                    continue;
//                }
//
//            }
//
//            //
//            if (current_layer.m_type == GILayer.GILayerType.XML) {
//                Paint fill = new Paint();
//                Paint line = new Paint();
//                for (GIColor color : current_layer.m_style.m_colors) {
//                    if (color.m_description.equalsIgnoreCase("line")) {
//                        if (color.m_name.equalsIgnoreCase("custom")) {
//                            line.setARGB(color.m_alpha, color.m_red,
//                                    color.m_green, color.m_blue);
//                        } else {
//                            color.setFromName();
//                            line.setARGB(color.m_alpha, color.m_red,
//                                    color.m_green, color.m_blue);
//                        }
//                        line.setStyle(Paint.Style.STROKE);
//                        line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
//                    } else if (color.m_description.equalsIgnoreCase("fill")) {
//                        if (color.m_name.equalsIgnoreCase("custom")) {
//                            fill.setARGB(color.m_alpha, color.m_red,
//                                    color.m_green, color.m_blue);
//                        } else {
//                            color.setFromName();
//                            fill.setARGB(color.m_alpha, color.m_red,
//                                    color.m_green, color.m_blue);
//                        }
//                        fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
//                        fill.setStyle(Paint.Style.FILL);
//                    }
//                }
//
//                Paint editing_fill = new Paint();
//                editing_fill.setColor(Color.CYAN);
//                editing_fill.setAlpha(96);
//                editing_fill.setStyle(Paint.Style.FILL);
//
//                Paint editing_stroke = new Paint();
//                editing_stroke.setColor(Color.CYAN);
//                editing_stroke.setStrokeWidth(2);
//                editing_fill.setAlpha(128);
//                editing_stroke.setStyle(Paint.Style.STROKE);
//                GIVectorStyle vstyle_editing = new GIVectorStyle(
//                        editing_stroke, editing_fill,
//                        wktPointBitmap,
//                        (int) current_layer2.m_opacity);
//
//                GILayer layer;
//                if (current_layer.m_source.m_location.equalsIgnoreCase("local") || current_layer.m_source.m_location.equalsIgnoreCase("absolute")) {
//                    GIVectorStyle vstyle = new GIVectorStyle(line, fill, wktPointBitmap,
//                            (int) current_layer2.m_opacity);
//
//                    String path = current_layer.m_source.GetLocalPath();
//                    if(current_layer.m_source.m_location.equalsIgnoreCase("absolute")){
//                        path = current_layer.m_source.GetAbsolutePath();
//                    }
//                    layer = GILayer.CreateLayer(
//                            path,
//                            GILayer.GILayerType.XML, vstyle, current_layer.m_encoding);
//
//                    layer.setName(current_layer.m_name);
//                    layer.m_layer_properties = current_layer;
//
//                    layer.AddStyle(vstyle_editing);
//                    if (layer instanceof GIEditableLayer && current_layer.editable != null) {
//                        GIEditableLayer editableLayer = (GIEditableLayer) layer;
//                        editableLayer.m_Type = current_layer.editable.enumType;
//                    }
//
//					/**/
////					if(ps.m_Edit != null && ps.m_Edit.m_Entries != null) {
////                        for (GIPropertiesLayerRef ref : ps.m_Edit.m_Entries) {
////                            if (ref.m_name.equalsIgnoreCase(current_layer.m_name)) {
////                                GIEditableLayer l = (GIEditableLayer) layer;
////                                if (ref.m_type.equalsIgnoreCase("POINT")) {
////                                    l.setType(GIEditableLayer.GIEditableLayerType.POINT);
////                                    GIEditLayersKeeper.Instance().poiLayer = l;
////                                    continue;
////                                }
////                                if (ref.m_type.equalsIgnoreCase("LINE")) {
////                                    l.setType(GIEditableLayer.GIEditableLayerType.LINE);
////                                    continue;
////                                }
////                                if (ref.m_type.equalsIgnoreCase("POLYGON")) {
////                                    l.setType(GIEditableLayer.GIEditableLayerType.POLYGON);
////                                    continue;
////                                }
////                                if (ref.m_type.equalsIgnoreCase("TRACK")) {
////                                    GIEditLayersKeeper.Instance().trackLayer = l;
////                                    l.setType(GIEditableLayer.GIEditableLayerType.TRACK);
////                                    continue;
////                                }
////                            }
////                        }
////                    }
//                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
////
////                    //todo remove with EditLayersKeeper
////                    GIEditableLayer l = (GIEditableLayer) layer;
////                    if (l != null && l.m_layer_properties.editable != null
////                            && l.m_layer_properties.editable.enumType != GILayer.EditableType.TRACK) {
////                        l.setType(l.m_layer_properties.editable.enumType);
////                        if (l.m_Type == GILayer.EditableType.TRACK && l.m_layer_properties.editable.active) {
////                            GIEditLayersKeeper.Instance().trackLayer = l;
////                        } else if (l.m_Type == GILayer.EditableType.POI && l.m_layer_properties.editable.active) {
////                            GIEditLayersKeeper.Instance().poiLayer = l;
////                        }
////                        GIEditLayersKeeper.Instance().AddEditableLayer(l);
////                    }
//                }
//
//                else {
//                    continue;
//                }
//            }
//
//        }
//    }

    private void loadGroup(GIProjectProperties ps, GIPropertiesGroup current_layer2, FlowableEmitter<Layer> subscriber)
    {
        for (GIPropertiesLayer current_layer : current_layer2.m_Entries)
        {
            if (current_layer.m_type == GILayer.GILayerType.LAYER_GROUP) {
                loadGroup(ps, (GIPropertiesGroup) current_layer, subscriber);
            }
            if (current_layer.m_type == GILayer.GILayerType.TILE_LAYER) {
                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
                    layer = GILayer.CreateLayer(
                            current_layer.m_source.GetLocalPath(),
                            GILayer.GILayerType.TILE_LAYER);
                    layer.setName(current_layer.m_name);
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer,
                            current_layer.m_range,
                            current_layer.m_enabled));
                } else {
                    continue;
                }

            }
            if (current_layer.m_type == GILayer.GILayerType.ON_LINE) {
                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("text")) {
                    layer = GILayer.CreateLayer(
                            current_layer.m_source.GetRemotePath(),
                            GILayer.GILayerType.ON_LINE);
                    layer.setName(current_layer.m_name);
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer,
                            current_layer.m_range,
                            current_layer.m_enabled));
                } else {
                    continue;
                }

            }
            if (current_layer.m_type == GILayer.GILayerType.SQL_LAYER)
            {
                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("text"))
                {
                    layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayer.GILayerType.SQL_LAYER);
                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
                        {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer,
                            current_layer.m_range,
                            current_layer.m_enabled));
                }
                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
                {
                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.SQL_LAYER);

                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);

                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE) {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
                }
                else
                {
                    continue;
                }

            }
            if (current_layer.m_type == GILayer.GILayerType.SQL_YANDEX_LAYER) {
                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("text"))
                {
                    layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayer.GILayerType.SQL_YANDEX_LAYER);
                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
                        {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
                }
                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
                {
                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.SQL_YANDEX_LAYER);
                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE) {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
                }
                else
                {
                    continue;
                }

            }
            if (current_layer.m_type == GILayer.GILayerType.FOLDER) {
                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("text"))
                {
                    layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayer.GILayerType.SQL_YANDEX_LAYER);
                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE)
                        {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
                }
                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
                {
                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.FOLDER);
                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zooming_type);
                        if (current_layer.m_sqldb.m_zooming_type == GISQLLayer.GISQLiteZoomingType.ADAPTIVE) {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
                }
                else
                {
                    continue;
                }

            }

            //
            if (current_layer.m_type == GILayer.GILayerType.XML) {
                Paint fill = new Paint();
                Paint line = new Paint();
                for (GIColor color : current_layer.m_style.m_colors) {
                    if (color.m_description.equalsIgnoreCase("line")) {
                        if (color.m_name.equalsIgnoreCase("custom")) {
                            line.setARGB(color.m_alpha, color.m_red,
                                    color.m_green, color.m_blue);
                        } else {
                            color.setFromName();
                            line.setARGB(color.m_alpha, color.m_red,
                                    color.m_green, color.m_blue);
                        }
                        line.setStyle(Paint.Style.STROKE);
                        line.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
                    } else if (color.m_description.equalsIgnoreCase("fill")) {
                        if (color.m_name.equalsIgnoreCase("custom")) {
                            fill.setARGB(color.m_alpha, color.m_red,
                                    color.m_green, color.m_blue);
                        } else {
                            color.setFromName();
                            fill.setARGB(color.m_alpha, color.m_red,
                                    color.m_green, color.m_blue);
                        }
                        fill.setStrokeWidth((float) current_layer.m_style.m_lineWidth);
                        fill.setStyle(Paint.Style.FILL);
                    }
                }

                Paint editing_fill = new Paint();
                editing_fill.setColor(Color.CYAN);
                editing_fill.setAlpha(96);
                editing_fill.setStyle(Paint.Style.FILL);

                Paint editing_stroke = new Paint();
                editing_stroke.setColor(Color.CYAN);
                editing_stroke.setStrokeWidth(2);
                editing_fill.setAlpha(128);
                editing_stroke.setStyle(Paint.Style.STROKE);
                GIVectorStyle vstyle_editing = new GIVectorStyle(
                        editing_stroke, editing_fill,
                        wktPointBitmap,
                        (int) current_layer2.m_opacity);

                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("local") || current_layer.m_source.m_location.equalsIgnoreCase("absolute")) {
                    GIVectorStyle vstyle = new GIVectorStyle(line, fill, wktPointBitmap,
                            (int) current_layer2.m_opacity);

                    String path = current_layer.m_source.GetLocalPath();
                    if(current_layer.m_source.m_location.equalsIgnoreCase("absolute")){
                        path = current_layer.m_source.GetAbsolutePath();
                    }
                    layer = GILayer.CreateLayer(
                            path,
                            GILayer.GILayerType.XML, vstyle, current_layer.m_encoding);

                    layer.setName(current_layer.m_name);
                    layer.m_layer_properties = current_layer;

                    layer.AddStyle(vstyle_editing);
                    if (layer instanceof GIEditableLayer && current_layer.editable != null) {
                        GIEditableLayer editableLayer = (GIEditableLayer) layer;
                        editableLayer.m_Type = current_layer.editable.enumType;
                    }

                    /**/
//					if(ps.m_Edit != null && ps.m_Edit.m_Entries != null) {
//                        for (GIPropertiesLayerRef ref : ps.m_Edit.m_Entries) {
//                            if (ref.m_name.equalsIgnoreCase(current_layer.m_name)) {
//                                GIEditableLayer l = (GIEditableLayer) layer;
//                                if (ref.m_type.equalsIgnoreCase("POINT")) {
//                                    l.setType(GIEditableLayer.GIEditableLayerType.POINT);
//                                    GIEditLayersKeeper.Instance().poiLayer = l;
//                                    continue;
//                                }
//                                if (ref.m_type.equalsIgnoreCase("LINE")) {
//                                    l.setType(GIEditableLayer.GIEditableLayerType.LINE);
//                                    continue;
//                                }
//                                if (ref.m_type.equalsIgnoreCase("POLYGON")) {
//                                    l.setType(GIEditableLayer.GIEditableLayerType.POLYGON);
//                                    continue;
//                                }
//                                if (ref.m_type.equalsIgnoreCase("TRACK")) {
//                                    GIEditLayersKeeper.Instance().trackLayer = l;
//                                    l.setType(GIEditableLayer.GIEditableLayerType.TRACK);
//                                    continue;
//                                }
//                            }
//                        }
//                    }
                    subscriber.onNext(new Layer(layer, current_layer.m_range, current_layer.m_enabled));
                }

                else {
                    continue;
                }
            }

        }
        subscriber.onComplete();
    }

    public class Layer {
        public GILayer giLayer;
        public GIRange giRange;
        public boolean enabled;

        public Layer(GILayer giLayer, GIRange giRange, boolean enabled) {
            this.giLayer = giLayer;
            this.giRange = giRange;
            this.enabled = enabled;

        }
    }
}
