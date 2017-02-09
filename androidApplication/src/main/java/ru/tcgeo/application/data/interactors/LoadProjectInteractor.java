package ru.tcgeo.application.data.interactors;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;

import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIEditableLayer;
import ru.tcgeo.application.gilib.GIEditableSQLiteLayer;
import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GISQLLayer;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIScaleRange;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.gilib.parser.GIPropertiesGroup;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayerRef;
import ru.tcgeo.application.gilib.parser.GISQLDB;
import ru.tcgeo.application.view.MapView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by abelov on 28.04.16.
 */
public class LoadProjectInteractor {
    private MapView view;
//    private GIMap map;

    public void setView(MapView view) {
        this.view = view;
    }

    public void loadProject(final String path){
        
        Observable.just(path)
            .observeOn(Schedulers.newThread())
                .map(new Func1<String, GIProjectProperties>() {
                    @Override
                    public GIProjectProperties call(String s) {
                        GIProjectProperties ps = new GIProjectProperties(s);
                        view.onProject(ps);
                        return ps;
                    }
                })
                .flatMap(new Func1<GIProjectProperties, Observable<Layer>>() {
                    @Override
                    public Observable<Layer> call(final GIProjectProperties giProjectProperties) {
                        return Observable.create(
                                new Observable.OnSubscribe<Layer>() {
                                    @Override
                                    public void call(Subscriber<? super Layer> sub) {
                                        loadGroup(giProjectProperties,  giProjectProperties.m_Group, sub );
                                        sub.onCompleted();
                                    }
                                }
                        );

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Layer>() {
                    @Override
                    public void onCompleted() {
                        view.onComplited();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onError();
                    }

                    @Override
                    public void onNext(Layer layer) {
                        view.onLayer(layer);
                    }
                });

    }

    public class Layer{
        public GILayer giLayer;
        public GIScaleRange giRange;
        public boolean enabled;

        public Layer(GILayer giLayer, GIScaleRange giRange, boolean enabled){
            this.giLayer = giLayer;
            this.giRange = giRange;
            this.enabled = enabled;

        }
    }



    private void loadGroup(GIProjectProperties ps, GIPropertiesGroup current_layer2, Subscriber<? super Layer>  subscriber)
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
                            new GIScaleRange(current_layer.m_range),
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
                            new GIScaleRange(current_layer.m_range),
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
                        builder.zoomType(current_layer.m_sqldb.m_zoom_type);
                        if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE"))
                        {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer,
                            new GIScaleRange(current_layer.m_range),
                            current_layer.m_enabled));
                }
                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
                {
                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.SQL_LAYER);

                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zoom_type);

                        if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE")) {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled));
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
                        builder.zoomType(current_layer.m_sqldb.m_zoom_type);
                        if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE"))
                        {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled));
                }
                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
                {
                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.SQL_YANDEX_LAYER);
                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zoom_type);
                        if (current_layer.m_sqldb.m_zoom_type
                                .equalsIgnoreCase("ADAPTIVE")) {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled));
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
                        builder.zoomType(current_layer.m_sqldb.m_zoom_type);
                        if (current_layer.m_sqldb.m_zoom_type.equalsIgnoreCase("ADAPTIVE"))
                        {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled));
                }
                else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
                {
                    layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.FOLDER);
                    layer.setName(current_layer.m_name);
                    if (current_layer.m_sqldb != null) {
                        GISQLDB.Builder builder = new GISQLDB.Builder(current_layer.m_sqldb);
                        builder.zoomType(current_layer.m_sqldb.m_zoom_type);
                        if (current_layer.m_sqldb.m_zoom_type
                                .equalsIgnoreCase("ADAPTIVE")) {
                            ((GISQLLayer) layer).getAvalibleLevels();
                        }
                        current_layer.m_sqldb = builder.build();
                    }
                    layer.m_layer_properties = current_layer;
                    subscriber.onNext(new Layer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled));
                }
                else
                {
                    continue;
                }

            }

            if (current_layer.m_type == GILayer.GILayerType.DBASE) {
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
                        (int) current_layer2.m_opacity);

                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
                    GIVectorStyle vstyle = new GIVectorStyle(line, fill,
                            (int) current_layer2.m_opacity);
                    layer = GILayer
                            .CreateLayer(current_layer.m_source.GetLocalPath(),
                                    GILayer.GILayerType.DBASE, vstyle,
                                    current_layer.m_encoding);

                    layer.setName(current_layer.m_name);

                    layer.m_layer_properties = current_layer;
                    layer.AddStyle(vstyle_editing);
					/**/
                    for (GIPropertiesLayerRef ref : ps.m_Edit.m_Entries) {
                        if (ref.m_name.equalsIgnoreCase(current_layer.m_name)) {
                            GIEditableSQLiteLayer l = (GIEditableSQLiteLayer) layer;
                            if (ref.m_type.equalsIgnoreCase("POINT")) {
                                l.setType(GIEditableLayer.GIEditableLayerType.POINT);
                                continue;
                            }
                            if (ref.m_type.equalsIgnoreCase("LINE")) {
                                l.setType(GIEditableLayer.GIEditableLayerType.LINE);
                                continue;
                            }
                            if (ref.m_type.equalsIgnoreCase("POLYGON")) {
                                l.setType(GIEditableLayer.GIEditableLayerType.POLYGON);
                                continue;
                            }
                            if (ref.m_type.equalsIgnoreCase("TRACK")) {
                                l.setType(GIEditableLayer.GIEditableLayerType.TRACK);
                                continue;
                            }
                        }
                    }
                    subscriber.onNext(new Layer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled));
                    GIEditLayersKeeper.Instance().AddLayer(
                            (GIEditableSQLiteLayer) layer);
                }

                else {
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
                        (int) current_layer2.m_opacity);

                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("local") || current_layer.m_source.m_location.equalsIgnoreCase("absolute")) {
                    GIVectorStyle vstyle = new GIVectorStyle(line, fill,
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
					/**/
                    for (GIPropertiesLayerRef ref : ps.m_Edit.m_Entries) {
                        if (ref.m_name.equalsIgnoreCase(current_layer.m_name)) {
                            GIEditableLayer l = (GIEditableLayer) layer;
                            if (ref.m_type.equalsIgnoreCase("POINT")) {
                                l.setType(GIEditableLayer.GIEditableLayerType.POINT);
                                GIEditLayersKeeper.Instance().m_POILayer = l;
                                continue;
                            }
                            if (ref.m_type.equalsIgnoreCase("LINE")) {
                                l.setType(GIEditableLayer.GIEditableLayerType.LINE);
                                continue;
                            }
                            if (ref.m_type.equalsIgnoreCase("POLYGON")) {
                                l.setType(GIEditableLayer.GIEditableLayerType.POLYGON);
                                continue;
                            }
                            if (ref.m_type.equalsIgnoreCase("TRACK")) {
                                GIEditLayersKeeper.Instance().m_TrackLayer = l;
                                l.setType(GIEditableLayer.GIEditableLayerType.TRACK);
                                continue;
                            }
                        }
                    }
                    subscriber.onNext(new Layer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled));

                    GIEditLayersKeeper.Instance().AddLayer(
                            (GIEditableLayer) layer);
                }

                else {
                    continue;
                }
            }

            if (current_layer.m_type == GILayer.GILayerType.PLIST)
            {
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


                GILayer layer;
                if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
                    GIVectorStyle vstyle = new GIVectorStyle(line, fill,
                            (int) current_layer2.m_opacity);
                    layer = GILayer.CreateLayer(
                            current_layer.m_source.GetLocalPath(),
                            GILayer.GILayerType.PLIST, vstyle, current_layer.m_encoding);

                    layer.setName(current_layer.m_name);
                    layer.m_layer_properties = current_layer;

                    subscriber.onNext(new Layer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled));
                    GIEditLayersKeeper.Instance().AddLayer(
                            (GIEditableLayer) layer);
                }
            }

        }
    }
}
