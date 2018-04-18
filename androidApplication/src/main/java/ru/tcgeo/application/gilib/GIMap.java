package ru.tcgeo.application.gilib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.tcgeo.application.App;
import ru.tcgeo.application.Geoinfo;
import ru.tcgeo.application.control.GIControl;
import ru.tcgeo.application.control.GIGeometryPointControl;
import ru.tcgeo.application.data.GIEditingStatus;
import ru.tcgeo.application.data.GITrackingStatus;
import ru.tcgeo.application.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.gilib.layer.GIGroupLayer;
import ru.tcgeo.application.gilib.layer.GILayer;
import ru.tcgeo.application.gilib.layer.GISQLLayer;
import ru.tcgeo.application.gilib.models.GIBitmap;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIPList;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.gilib.parser.GIPropertiesStyle;
import ru.tcgeo.application.gilib.parser.GIRange;
import ru.tcgeo.application.gilib.parser.GISQLDB;
import ru.tcgeo.application.gilib.parser.GISource;
import ru.tcgeo.application.gilib.requestor.GIDataRequestor;
import ru.tcgeo.application.utils.CommonUtils;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.wkt.GIDBaseField;
import ru.tcgeo.application.wkt.GIGPSPointsLayer;
import ru.tcgeo.application.wkt.GIGeometryControl;
import ru.tcgeo.application.wkt.GIXMLTrack;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktLinestring;
import ru.tcgeo.application.wkt.GI_WktPoint;
import ru.tcgeo.application.wkt.GI_WktPolygon;

import static ru.tcgeo.application.gilib.layer.GILayer.EditableType.POI;


public class GIMap extends SurfaceView //implements SurfaceHolder.Callback//implements Runnable SurfaceView
{
    // view diagonal in inches
    static public double inches_per_pixel = 0.0066;
    //    static public float offsetY;
    public static double meters_per_inch = 0.0254f;
    public final String LOG_TAG = "LOG_TAG";

    public Geoinfo activity;


    public Rect m_view;        // view size
    public ru.tcgeo.application.gilib.parser.GIProjectProperties ps;
    //TODO: make private

    public GIGroupLayer m_layers;
    public Rect m_view_rect;    // viewable part of bitmap
    GIBitmap m_smooth;
    GIBitmap m_draft;
    GIBounds m_bounds;    // current view extent & projection
    Handler m_handler;
    SurfaceHolder m_holder;
    ThreadStack m_threadStack;
    //GIControl's works
    ru.tcgeo.application.gilib.GIMap target = this;
    private GIEditableLayer currentLayer;
    private GIEditableLayer trackLayer;
    private GIEditableLayer poiLayer;
    private GI_WktGeometry currentTrack;
    private GI_WktGeometry currentGeometry;
    private GIGeometryControl currentEditingControl;
    private GIGeometryControl currentTrackControl;
    private ArrayList<GIGeometryControl> controls;
    private ArrayList<GIEditableLayer> editableLayers;
    private ArrayList<GIControl> m_listeners = new ArrayList<GIControl>();

    public GIMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context);
    }

    public GIMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context);
    }

    public GIMap(Context context) {
        super(context);
        this.initialize(context);
    }

    public static double getScale(GIBounds bounds, Rect rect) {
        //final static double meters_per_inch = 0.0254f;
        GIBounds metric = bounds.Reprojected(GIProjection.WorldMercator());
        double rect_diag_meters = Math.hypot(rect.width(), rect.height()) * inches_per_pixel * meters_per_inch;
        double metricDiagonal = Math.hypot(metric.width(), metric.height());
        return rect_diag_meters / metricDiagonal;
    }

    private void initialize(Context context) {
        activity = (Geoinfo) context;
        setWillNotDraw(false);
        m_layers = new GIGroupLayer();
        m_holder = getHolder();
        m_handler = new Handler();
        m_threadStack = new ThreadStack();
        editableLayers = new ArrayList<GIEditableLayer>();
        m_bounds = new GIBounds(GIProjection.WGS84(), 0, 90, 90, 0);
        InitBounds(m_bounds.Reprojected(GIProjection.WorldMercator()));
    }

    public void registerGIControl(GIControl control) {
        m_listeners.add(control);
    }

    public void unRegisterGIControl(GIControl control) {
        m_listeners.remove(control);
        RelativeLayout rl = (RelativeLayout) getParent();
        rl.removeView((View) control);
    }

    protected void fire_afterMapFullRedraw() {
        for (GIControl control : m_listeners) {
            control.afterMapFullRedraw(m_bounds, m_view);
        }
    }

    protected void fire_afterImageFullRedraw() {
        for (GIControl control : m_listeners) {
            control.afterMapImageRedraw(m_bounds, m_view);
        }
    }

    protected void fire_onMarkerLayerlRedraw() {
        for (GIControl control : m_listeners) {
            control.onMarkerLayerRedraw(m_view);
        }
    }

    protected void fire_onViewMove() {
        invalidate();
        for (GIControl control : m_listeners) {
            control.onViewMove();
        }
    }

    protected void fire_onMapMove() {
        for (GIControl control : m_listeners) {
            control.onMapMove();
        }
    }

    protected void fire_afterViewRedraw() {
        for (GIControl control : m_listeners) {
            control.afterViewRedraw();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        m_view = new Rect(0, 0, w, h);
        /*if(m_bitmap != null)
        {
	    	m_bitmap.recycle();
	    }

	    if(m_bitmap == null)
	    {
	    	System.gc();
	    	m_bitmap = Bitmap.createBitmap(m_view.width(), m_view.height(), Bitmap.Config.ARGB_8888);
	    }

	    m_old_view_rect = new Rect(0, 0, oldw, oldh);
	    m_new_view_rect = new Rect(0, 0, w, h);*/
        m_view_rect = new Rect(0, 0, w, h);
        Log.d(LOG_TAG, "onSize from (" + oldw + " ," + oldh + ") to (" + w + " ," + h + ")");

        if (oldw != 0 && oldh != 0) {
            BoundsChanging(w, h, oldw, oldh);
        } else {
            AdjustBoundsRatio();
        }

        if (m_smooth == null) {
            m_smooth = new GIBitmap(m_bounds, m_view.width(), m_view.height());
        }
        UpdateMap();
    }

    protected void BoundsChanging(int w, int h, int oldw, int oldh) {

        int dx = (w - oldw) / 2;
        int dy = (h - oldh) / 2;
        Point LeftTop = new Point(-dx, -dy);
        Point RightBottom = new Point(w - dx, h - dy);

        double pixelWidth = m_bounds.width() / oldw;
        double pixelHeight = m_bounds.height() / oldh;

        double lonlt = m_bounds.left() + pixelWidth * LeftTop.x;
        double latlt = m_bounds.top() - pixelHeight * LeftTop.y;

        double lon = m_bounds.left() + pixelWidth * RightBottom.x;
        double lat = m_bounds.top() - pixelHeight * RightBottom.y;

        m_bounds = new GIBounds(m_bounds.projection(), lonlt, latlt, lon, lat);
        fire_onViewMove();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (m_holder == null)
            return;
        if (m_holder.getSurface() == null)
            return;
        int i = 0;
        while (!m_holder.getSurface().isValid()) {
            i++;
        }
        Canvas holded_canvas = m_holder.lockCanvas();
        holded_canvas.drawColor(Color.WHITE);
        if (m_draft != null) {
            m_draft.Draw(holded_canvas, m_bounds);
        }
        m_smooth.Draw(holded_canvas, m_bounds);
        m_holder.unlockCanvasAndPost(holded_canvas);

    }

    public void Clear() {
        m_layers.RemoveAll();
        initialize(activity);
    }

    public void InitBounds(GIBounds initial_extent) {
        m_bounds = initial_extent;
        AdjustBoundsRatio();
    }

    private void AdjustBoundsRatio() {
        if (m_view == null)
            return;
        if (m_bounds == null)
            return;

        double ratio = (double) m_view.width() / (double) m_view.height();


        if (m_bounds.width() / m_bounds.height() == ratio) {
            return; // we're good
        } else if (m_bounds.width() / m_bounds.height() > ratio) {
            // height should be expanded
            double diff = (m_bounds.width() / (double) m_view.width()) * (double) m_view.height() - m_bounds.height();
            m_bounds = new GIBounds(m_bounds.projection(),
                    m_bounds.left(),
                    m_bounds.top() + diff / 2,
                    m_bounds.right(),
                    m_bounds.bottom() - diff / 2);
            fire_onViewMove();
        } else {
            // width should be expanded
            double diff = (m_bounds.height() / (double) m_view.height()) * (double) m_view.width() - m_bounds.width();
            m_bounds = new GIBounds(m_bounds.projection(),
                    m_bounds.left() - diff / 2,
                    m_bounds.top(),
                    m_bounds.right() + diff / 2,
                    m_bounds.bottom());
            fire_onViewMove();
        }

    }


    public GILayer AddLayer(GILayer layer) {
        return m_layers.AddLayer(layer);
    }

    public GILayer AddLayer(GILayer layer, GIRange range, boolean enabled) {
        return m_layers.AddLayer(layer, range, enabled);
    }

    public GILayer InsertLayerAt(GILayer layer, int position) {
        return m_layers.InsertLayerAt(layer, position);
    }

    public GILayer addLayer(File file) {

        GILayer result = null;
        String filenameArray[] = file.getName().split("\\.");
        String extention = filenameArray[filenameArray.length - 1];
        if (extention.equalsIgnoreCase("sqlitedb")) {
            result = addSQLLayer(file);
        } else if (extention.equalsIgnoreCase("xml")) {
            result = addXMLLayer(file);
        } else if (extention.equalsIgnoreCase("yandex") || extention.equalsIgnoreCase("traffic")) {
            result = addYandexTraffic(file);
        }
        UpdateMap();
        return result;
    }

    public GILayer addSQLLayer(File file) {
        GIPropertiesLayer properties_layer = new GIPropertiesLayer();
        properties_layer.m_enabled = true;
        properties_layer.m_name = file.getName();
        properties_layer.m_range = new GIRange();
        properties_layer.m_source = new GISource("absolute", file.getAbsolutePath()); //getName()
        properties_layer.m_type = GILayer.GILayerType.SQL_YANDEX_LAYER;
        properties_layer.m_strType = "SQL_YANDEX_LAYER";
        GILayer layer;
        //TODO
        layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayer.GILayerType.SQL_YANDEX_LAYER);
        properties_layer.m_sqldb = new GISQLDB();//"auto";
        properties_layer.m_sqldb.m_zooming_type = GISQLLayer.GISQLiteZoomingType.AUTO;

        properties_layer.m_sqldb.m_min_z = 1;
        properties_layer.m_sqldb.m_max_z = 19;

        int min = 1;
        int max = 19;

        properties_layer.m_range = new GIRange();
        double con = 0.0254 * 0.0066 * 256 / (0.5 * 40000000);
        properties_layer.m_range.m_from = (int) (1 / (Math.pow(2, min) * con));
        properties_layer.m_range.m_to = (int) (1 / (Math.pow(2, max) * con));

        ps.m_Group.addEntry(properties_layer);
        layer.setName(file.getName());
        layer.m_layer_properties = properties_layer;
//        mMap.InsertLayerAt(layer, 0);
        return AddLayer(layer);
    }

    public GILayer addYandexTraffic(File file) {
        GIPropertiesLayer properties_layer = new GIPropertiesLayer();
        properties_layer.m_enabled = true;
        properties_layer.m_name = file.getName();
        properties_layer.m_range = new GIRange();
        properties_layer.m_source = new GISource("text", "yandex"); //getName()
        properties_layer.m_type = GILayer.GILayerType.ON_LINE;
        properties_layer.m_strType = "ON_LINE";
        GILayer layer;
        layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayer.GILayerType.ON_LINE);
        ps.m_Group.addEntry(properties_layer);
        layer.setName(file.getName());
        layer.m_layer_properties = properties_layer;
        return AddLayer(layer);
    }

    public GILayer addXMLLayer(File file) {
        GIPropertiesLayer properties_layer = new GIPropertiesLayer();
        properties_layer.m_enabled = true;
        properties_layer.m_name = file.getName();
        properties_layer.m_range = new GIRange();
        properties_layer.m_source = new GISource("absolute", file.getAbsolutePath());
        properties_layer.m_type = GILayer.GILayerType.XML;
        properties_layer.m_strType = "XML";
        GILayer layer;
        //
        Paint fill = new Paint();
        Paint line = new Paint();

        GIColor color_fill = new GIColor.Builder().description("fill").name("gray").build();
        GIColor color_line = new GIColor.Builder().description("line").name("gray").build();

        line.setColor(color_line.Get());
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(2);

        fill.setColor(color_fill.Get());
        fill.setStrokeWidth(2);
        fill.setStyle(Paint.Style.FILL);

        GIVectorStyle vstyle = new GIVectorStyle(line, fill, 1);

        properties_layer.m_style = new GIPropertiesStyle.Builder()
                .type("vector")
                .lineWidth(2)
                .opacity(1)
                .color(color_line)
                .color(color_fill)
                .build();

        layer = GILayer.CreateLayer(properties_layer.m_source.GetAbsolutePath(), GILayer.GILayerType.XML, vstyle);
        ps.m_Group.addEntry(properties_layer);
        layer.setName(file.getName());
        layer.m_layer_properties = properties_layer;

        return AddLayer(layer);
    }

    public List<GILayer> getLayers() {
        List<GILayer> result = new ArrayList<>();
//        result.add(null);
        result.addAll(getLayers(m_layers));
        return result;
    }

    private List<GILayer> getLayers(GIGroupLayer layer) {
        List<GILayer> result = new ArrayList<>();
        for (GILayer l : layer.m_list) {
            if (GILayer.GILayerType.LAYER_GROUP == l.type) {
                result.addAll(getLayers((GIGroupLayer) l));
            } else {
                result.add(l);
            }
        }
        return result;
    }

    public void setMarkersSource(GILayer giLayer, boolean set) {
        if (giLayer instanceof GIGPSPointsLayer) {
            GIGPSPointsLayer layer = (GIGPSPointsLayer) giLayer;
            ps.m_markers = null;
            for (GILayer l : getLayers()) {
                if (l != null && l instanceof GIGPSPointsLayer) {
                    if (((GIGPSPointsLayer) l).getPath().equalsIgnoreCase(layer.getPath()) && set) {
                        ((GIGPSPointsLayer) l).setMarkersSource(true);
                        ps.m_markers = giLayer.getName();
                        ps.m_markers_source = "layer";
                    } else {
                        ((GIGPSPointsLayer) l).setMarkersSource(false);
                    }
                }
            }
        }
    }

    public GIProjection Projection() {
        return m_bounds.projection();
    }


    public GILonLat Center() {
        return new GILonLat((m_bounds.left() + m_bounds.right()) / 2,
                (m_bounds.top() + m_bounds.bottom()) / 2);
    }


    public void SetCenter(GILonLat point) {
        m_bounds = new GIBounds(m_bounds.projection(), point, m_bounds.width(), m_bounds.height());
        fire_onViewMove();
        UpdateMap();
    }

    public double GetTg() {
        return ((double) m_view.height() / (double) m_view.width());
    }

    public double GetCos() {
        double alpha = Math.atan(GetTg());
        return Math.cos(alpha);
    }

    public double GetSin() {
        double alpha = Math.atan(GetTg());
        return Math.sin(alpha);
    }

    public double getPixelWidth() {
        return m_bounds.width() / m_view_rect.width();
    }

    public double getPixelHeight() {
        return m_bounds.height() / m_view_rect.height();
    }

    public double getDistance(Point distance) {
        double pixelWidth = m_bounds.width() / m_view.width();
        double pixelHeight = m_bounds.height() / m_view.height();
        double lon = pixelWidth * distance.x;
        double lat = pixelHeight * distance.y;
        double res = Math.hypot(lon, lat);

        return res;
    }

    public void SetCenter(GILonLat point, double diagonal) {
        GILonLat center = GIProjection.ReprojectLonLat(point, GIProjection.WGS84(), this.Projection());
        GIBounds new_bounds = new GIBounds(this.Projection(), center, diagonal * GetCos(), diagonal * GetSin());
        SetBounds(new_bounds);
    }

    public void MoveMapBy(double x, double y) {
        m_bounds = new GIBounds(m_bounds.projection(),
                new GILonLat(Center().lon() + x, Center().lat() + y),
                m_bounds.width(),
                m_bounds.height());
        fire_onViewMove();
        UpdateMap();
    }

    public void MoveViewBy(int x, int y) {

        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();

        m_view_rect.offset(x, y);

        m_bounds = new GIBounds(m_bounds.projection(),
                m_bounds.left() + x * pixelWidth,
                m_bounds.top() - y * pixelHeight,
                m_bounds.right() + x * pixelWidth,
                m_bounds.bottom() - y * pixelHeight);
        //invalidate();
        fire_onViewMove();

    }

    public GIBounds Bounds() {
        return m_bounds;
    }

    public void SetBounds(GIBounds bounds) {
        m_bounds = bounds;
        AdjustBoundsRatio();
        fire_onViewMove();
        UpdateMap();
    }

    public double Width() {
        return m_bounds.width();
    }

    public double Height() {
        return m_bounds.height();
    }

    public double getScaleFactor() {
        return GIMap.getScale(m_bounds, m_view_rect);
    }

    // Factor < 1 is Zoom in, > 1 is Zoom out.
    // from TougchControl
    public void ScaleViewBy(Point focus, double factor) {
        double ratio = (double) m_view.width() / (double) m_view.height();

        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();

        double b_focus_x = m_bounds.left() + pixelWidth * (focus.x - m_view_rect.left);
        double b_focus_y = m_bounds.top() - pixelHeight * (focus.y - m_view_rect.top);

        double new_left = (focus.x - ((double) focus.x - m_view_rect.left) / factor);
        double new_top = (focus.y - ((double) focus.y - m_view_rect.top) / factor);
        double new_right = (focus.x - ((double) focus.x - m_view_rect.right) / factor);
        double new_bottom = (focus.y - ((double) focus.y - m_view_rect.bottom) / factor);


        double pixW = new_right - new_left;
        double pixH = new_bottom - new_top;

        // Adjust ratio
        if (pixW / pixH == ratio) {
            // we're good
        } else if (pixW / pixH > ratio) {
            // height should be expanded
            double diff = (pixW / m_view.width()) * m_view.height() - pixH;
            new_top -= diff / 2;
            new_bottom += diff / 2;
        } else {
            // width should be expanded
            double diff = (pixH / m_view.height()) * m_view.width() - pixW;
            new_left -= diff / 2;
            new_right += diff / 2;
        }

        m_view_rect.set((int) new_left, (int) new_top, (int) new_right, (int) new_bottom);

        m_bounds = new GIBounds(m_bounds.projection(),
                b_focus_x - (focus.x - (int) new_left) * pixelWidth,
                b_focus_y + (focus.y - (int) new_top) * pixelHeight,
                b_focus_x - (focus.x - (int) new_right) * pixelWidth,
                b_focus_y + (focus.y - (int) new_bottom) * pixelHeight);
        fire_onViewMove();
    }

    // Factor < 1 is Zoom in, > 1 is Zoom out.
    // from buttons
    public void ScaleMapBy(GILonLat focus, double factor) {
        //ARAB
        //cant see a reason
        //Point _focus = MercatorMapToScreen(focus);
        Point _focus = new Point(m_view.centerX(), m_view.centerY());
        ScaleViewBy(_focus, factor);
        /*m_bounds = new GIBounds(m_bounds.projection(),
                				focus.lon() - (focus.lon() - m_bounds.left()) / factor,
                				focus.lat() - (focus.lat() - m_bounds.top()) / factor,
                				focus.lon() - (focus.lon() - m_bounds.right()) / factor,
                				focus.lat() - (focus.lat() - m_bounds.bottom()) / factor);*/
        this.invalidate();
        UpdateMap();
    }

    public void UpdateMap() {
        m_view_rect = new Rect(m_view);
        m_threadStack.addTask();
        fire_afterMapFullRedraw();
    }

    public void setToDraft(boolean needed) {
        m_threadStack.setToDraft(needed);
    }

    public void RenewBitmap(Bitmap bitmap, GIBounds bounds) {
        if (bitmap != null) {
            //m_bitmap.recycle();
            //m_bitmap = bitmap;
            m_smooth.Set(bounds, bitmap);
            //System.gc();
        }
        //Rect screen = MapToScreen(bounds);
        //TODO это здесь сбивается scaling после перерисовки?????

        //m_view_rect.set(screen);
        target.invalidate();
        //m_current_working = false;
        fire_afterMapFullRedraw();
    }

    public void RenewBitmapLarge(Bitmap bitmap, GIBounds bounds) {

        if (bitmap != null) {
            /*if(large_bitmap != null)
			{
				large_bitmap.recycle();
				//System.gc();
			}*/
            if (m_draft != null) {
                m_draft.Set(bounds, bitmap);
            } else {
                m_draft = new GIBitmap(bounds, bitmap);
            }
        }
        //m_draft_working = false;
        //large_bitmap = bitmap;
        //large_bounds = bounds;
        target.invalidate();
    }

    /*public void run()
    {
        m_view_rect.set(m_view);
        m_bitmap.eraseColor(Color.WHITE);
        double scale_ = GIMap.getScale(m_bounds, m_view);
        m_layers.Redraw(m_bounds, m_bitmap, 255, scale_);
        this.invalidate();
        fire_afterMapFullRedraw();
    }*/
    public GIBounds getrequestArea(Point point) {
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();

        double area_width = pixelWidth * 30;
        double area_height = pixelHeight * 30;

        double lon = m_bounds.left() + pixelWidth * point.x;
        double lat = m_bounds.top() - pixelHeight * point.y;

        GIBounds requestArea = new GIBounds(m_bounds.projection(), new GILonLat(lon, lat), area_width, area_height);
        return requestArea;
    }

    public GIDataRequestor RequestDataInPoint(Point point, GIDataRequestor requestor) {
        synchronized (m_layers) {
            double scale_ = GIMap.getScale(m_bounds, m_view);

            double pixelWidth = m_bounds.width() / m_view_rect.width();
            double pixelHeight = m_bounds.height() / m_view_rect.height();

            double area_width = pixelWidth * 30;
            double area_height = pixelHeight * 30;

            double lon = m_bounds.left() + pixelWidth * point.x;
            double lat = m_bounds.top() - pixelHeight * point.y;

            GIBounds requestArea = new GIBounds(m_bounds.projection(), new GILonLat(lon, lat), area_width, area_height);
            requestor.StartGatheringData(new GILonLat(lon, lat));
            m_layers.RequestDataIn(requestArea, requestor, scale_);
            requestor.EndGatheringData(new GILonLat(lon, lat));
        }
        return requestor;
    }

    public GILonLat ScreenToMap(Point point) {
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();
        double lon = m_bounds.left() + pixelWidth * point.x;
        double lat = m_bounds.top() - pixelHeight * point.y;
        GILonLat lonlat = new GILonLat(lon, lat);
        GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, this.Projection(), GIProjection.WGS84());
        return new_lonlat;
    }

    public GILonLat ScreenToMercatorMap(Point point) {
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();
        double lon = m_bounds.left() + pixelWidth * point.x;
        double lat = m_bounds.top() - pixelHeight * point.y;
        GILonLat lonlat = new GILonLat(lon, lat);
        return lonlat;
    }

    public Point MercatorMapToScreen(GILonLat lonlat) {
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();
        int point_x = (int) ((lonlat.lon() - m_bounds.left()) / pixelWidth);
        int point_y = (int) ((m_bounds.top() - lonlat.lat()) / pixelHeight);
        return new Point(point_x, point_y);
    }

    public Point MapToScreen(GILonLat lonlat) {
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();
        GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, GIProjection.WGS84(), this.Projection());
        int point_x = (int) ((new_lonlat.lon() - m_bounds.left()) / pixelWidth);
        int point_y = (int) ((m_bounds.top() - new_lonlat.lat()) / pixelHeight);
        return new Point(point_x, point_y);
    }

    public Point MapToScreenTempo(GILonLat lonlat) {
        double pixelWidth = m_bounds.width() / m_view.width();
        double pixelHeight = m_bounds.height() / m_view.height();
        GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, GIProjection.WGS84(), this.Projection());
        int point_x = (int) ((new_lonlat.lon() - m_bounds.left()) / pixelWidth);
        int point_y = (int) ((m_bounds.top() - new_lonlat.lat()) / pixelHeight);
        return new Point(point_x, point_y);
    }

    public Rect MapToScreen(GIBounds bounds) {
        double pixelWidth = m_bounds.width() / m_view.width();
        double pixelHeight = m_bounds.height() / m_view.height();

        int left = (int) ((bounds.left() - m_bounds.left()) / pixelWidth);
        //int right = (int)((bounds.right() - m_bounds.left())/pixelWidth);
        int top = (int) ((m_bounds.top() - bounds.top()) / pixelHeight);
        //int bottom = (int)((m_bounds.top() - bounds.bottom())/pixelHeight);

        Rect test = new Rect(m_view);
        test.offset(-left, -top);
        //Rect res = new Rect(left, top, right, bottom);
        return test;
    }

    public RectF MapToScreenDraw(GIBounds bounds) {
        double pixelWidth = m_bounds.width() / m_view.width();
        double pixelHeight = m_bounds.height() / m_view.height();
        float left = (float) ((bounds.left() - m_bounds.left()) / pixelWidth);
        float right = (float) ((bounds.right() - m_bounds.left()) / pixelWidth);
        float top = (float) ((m_bounds.top() - bounds.top()) / pixelHeight);
        float bottom = (float) ((m_bounds.top() - bounds.bottom()) / pixelHeight);
        RectF res = new RectF(left, top, right, bottom);
        return res;
    }

    public GILonLat MetersToDegrees(GILonLat lonlat) {
        GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, this.Projection(), GIProjection.WGS84());
        return new_lonlat;
    }

	/*			actual_bounds = new GIBounds(m_bounds.m_projection, m_bounds.m_left, m_bounds.m_top, m_bounds.m_right, m_bounds.m_bottom);
			System.gc();
			final Bitmap tmp_bitmap = Bitmap.createBitmap(m_view.width(), m_view.height(), Bitmap.Config.ARGB_8888);*/

    public double MetersInPixel() {

        GIBounds wgs_bounds = m_bounds.Reprojected(GIProjection.WGS84());
        double dist = MapUtils.GetDistanceBetween(wgs_bounds.TopLeft(), wgs_bounds.BottomRight());
        double px_dist = Math.hypot(m_view.width(), m_view.height());

        double meters_in_px = dist / px_dist;
        return meters_in_px;

    }

    public void Synhronize() {
        GIBounds wgs_bounds = m_bounds.Reprojected(GIProjection.WGS84());
        ps.m_left = wgs_bounds.left();
        ps.m_top = wgs_bounds.top();
        ps.m_right = wgs_bounds.right();
        ps.m_bottom = wgs_bounds.bottom();

//        for(GITuple tuple : m_layers.m_list)
//		{
//			tuple.layer.m_layer_properties.m_enabled = tuple.visible;
//		}
    }

    public int getOffsetY() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return displayMetrics.heightPixels - getMeasuredHeight();
    }

    public void AddEditableLayer(GIEditableLayer layer) {
        editableLayers.add(layer);
    }

    public void ClearEditableLayers() {
        editableLayers.clear();
    }

    public List<GIEditableLayer> getEditableLayers() {
        return editableLayers;
    }

    public GIGeometryControl getCurrentTrackControl() {
        return currentTrackControl;
    }

    public void setCurrentTrackControl(GIGeometryControl currentTrackControl) {
        this.currentTrackControl = currentTrackControl;
    }

    public GIEditableLayer getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(GIEditableLayer currentLayer) {
        this.currentLayer = currentLayer;
    }

    public GIEditableLayer getTrackLayer() {
        return trackLayer;
    }

    public void setTrackLayer(GIEditableLayer trackLayer) {
        this.trackLayer = trackLayer;
    }

    public GIEditableLayer getPoiLayer() {
        return poiLayer;
    }

    public void setPoiLayer(GIEditableLayer poiLayer) {
        this.poiLayer = poiLayer;
    }

    public GI_WktGeometry getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(GI_WktGeometry currentTrack) {
        this.currentTrack = currentTrack;
    }

    public GI_WktGeometry getCurrentGeometry() {
        return currentGeometry;
    }

    public ArrayList<GIGeometryControl> getControls() {
        return controls;
    }

    public GIGeometryControl getCurrentEditingControl() {
        return currentEditingControl;
    }

    public boolean CreateNewObject() {
        boolean res = false;

        if (currentLayer.m_Type == null) {
            currentLayer.m_Type = POI;
        }

        switch (currentLayer.m_Type) {
            case POI: {
                currentGeometry = new GI_WktPoint();
                res = true;
                break;
            }
            case LINE: {
                currentGeometry = new GI_WktLinestring();
                res = true;
                break;
            }
            case POLYGON: {
                currentGeometry = new GI_WktPolygon();
                GI_WktLinestring outer_ring = new GI_WktLinestring();
                ((GI_WktPolygon) currentGeometry).AddRing(outer_ring);
                res = true;
                break;
            }
            case TRACK: {
                return false;
            }
            default:
                return false;
        }
        if (!res) {
            return false;
        }
        currentGeometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.NEW;
        currentGeometry.m_attributes = new HashMap<String, GIDBaseField>();
        for (String key : currentLayer.getAttributes().keySet()) {
            currentGeometry.m_attributes.put(key, new GIDBaseField(currentLayer.getAttributes().get(key)));
        }
        currentLayer.m_shapes.add(currentGeometry);
        currentEditingControl = new GIGeometryControl(this, currentLayer, currentGeometry);
        controls.add(currentEditingControl);

        return res;
    }

    public void StartEditing(GIEditableLayer layer) {
        if (activity.getState() == GIEditingStatus.EDITING_POI) {
            return;
        }
        boolean toRedraw = false;
        activity.setState(GIEditingStatus.RUNNING);
        currentLayer = layer;
        for (GIEditableLayer old : getEditableLayers()) {
            if (old.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED) {
                toRedraw = true;
                old.Save();
                old.m_Status = GIEditableLayer.GIEditableLayerStatus.UNEDITED;
            }
        }
        for (GIGeometryControl control : controls) {
            control.Disable();
        }
        controls.clear();

        if (layer.m_Status == GIEditableLayer.GIEditableLayerStatus.UNEDITED) {
            layer.m_Status = GIEditableLayer.GIEditableLayerStatus.EDITED;

            activity.fbEditButton.setVisibility(View.VISIBLE);
            activity.fbEditButton.setActivated(true);
            if (currentLayer == trackLayer) {

                activity.fbEditGeometry.setVisibility(View.GONE);
                activity.fbEditCreate.setVisibility(View.GONE);
            } else {
                activity.fbEditGeometry.setVisibility(View.VISIBLE);
                activity.fbEditCreate.setVisibility(View.VISIBLE);
            }

            for (GI_WktGeometry geom : layer.m_shapes) {
                GIGeometryControl geometry_control = new GIGeometryControl(this, currentLayer, geom);
                controls.add(geometry_control);
            }
            toRedraw = true;
        }
        if (toRedraw) {
            UpdateMap();
        }
    }

    public void StopEditing() {
        activity.setState(GIEditingStatus.STOPPED);
        boolean toRedraw = false;
        for (GIEditableLayer layer : getEditableLayers()) {
            if (layer != null && layer.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED) {
                toRedraw = true;
                layer.Save();
                layer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNEDITED;
            }
        }
        if (toRedraw) {
            UpdateMap();
        }

        activity.fbEditButton.setVisibility(View.GONE);
        activity.fbEditButton.setActivated(false);

        for (GIGeometryControl control : controls) {
            control.Disable();
        }
        controls.clear();
    }


    public List<Marker> getMarkers() {
        List<Marker> result = new ArrayList<>();
        if (ps.m_markers_source == null && ps.m_markers != null && !ps.m_markers.isEmpty()) {
            GIPList PList = new GIPList();
            PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ps.m_markers); // "/sdcard/"
            for (Marker marker : PList.m_list) {
                result.add(marker);
            }
        }
        if (ps.m_markers_source != null) {
            if (ps.m_markers_source.equalsIgnoreCase("file")) {
                GIPList PList = new GIPList();
                PList.Load(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ps.m_markers);// "/sdcard/"
                for (Marker marker : PList.m_list) {
                    result.add(marker);
                }
            }
            if (ps.m_markers_source.equalsIgnoreCase("layer")) {
                GIEditableLayer layer = null;
                for (GILayer l : m_layers.m_list) {
                    if (l.getName()
                            .equalsIgnoreCase(ps.m_markers)) {
                        layer = (GIEditableLayer) l;
                        break;
                    }
                }
                if (layer != null) {
                    GIPList list = new GIPList();
                    for (GI_WktGeometry geom : layer.m_shapes) {
                        if (geom instanceof GI_WktPoint) {
                            GI_WktPoint point = (GI_WktPoint) geom;
                            if (point != null) {
                                Marker marker = new Marker();
                                if (geom.m_attributes.containsKey("Name")) {
                                    marker.name = geom.m_attributes.get("Name").m_value.toString();
                                } else if (!geom.m_attributes.keySet().isEmpty()) {
                                    marker.name = (String) geom.m_attributes.get(geom.m_attributes.keySet().toArray()[0]).m_value;
                                } else {
                                    marker.name = String.valueOf(geom.m_ID);
                                }
                                marker.lon = point.m_lon;
                                marker.lat = point.m_lat;
                                marker.description = "";
                                marker.image = "";
                                marker.diag = 0;
                                result.add(marker);
                            }
                        } else if (geom instanceof GIXMLTrack) {
                            GIXMLTrack track = (GIXMLTrack) geom;
                            if (track != null && track.m_points != null && !track.m_points.isEmpty()) {
                                Marker marker = new Marker();
                                if (geom.m_attributes.containsKey("Project")) {
                                    marker.name = geom.m_attributes.get("Project").m_value.toString();
                                    if (geom.m_attributes.containsKey("Description")) {
                                        String data = CommonUtils.getTime(geom.m_attributes.get("Description").m_value.toString());
                                        if (!data.isEmpty()) {
                                            marker.name = marker.name + " " + data;
                                        } else {
                                            marker.name = marker.name + " " + geom.m_attributes.get("Description").m_value.toString();
                                        }

                                    }
                                } else if (!geom.m_attributes.keySet().isEmpty()) {
                                    marker.name = (String) geom.m_attributes.get(geom.m_attributes.keySet().toArray()[0]).m_value;
                                } else {
                                    marker.name = String.valueOf(geom.m_ID);
                                }
                                marker.lon = ((GI_WktPoint) track.m_points.get(0)).m_lon;
                                marker.lat = ((GI_WktPoint) track.m_points.get(0)).m_lat;
                                marker.description = "";
                                marker.image = "";
                                marker.diag = 0;
                                result.add(marker);
                            }
                        } else if (geom instanceof GI_WktLinestring) {
                            GI_WktLinestring line = (GI_WktLinestring) geom;
                            if (line != null && line.m_points != null && !line.m_points.isEmpty()) {
                                Marker marker = new Marker();
                                if (geom.m_attributes.containsKey("Project")) {
                                    marker.name = geom.m_attributes.get("Project").m_value.toString();
                                    if (geom.m_attributes.containsKey("Description")) {
                                        String data = CommonUtils.getTime(geom.m_attributes.get("Description").m_value.toString());
                                        if (!data.isEmpty()) {
                                            marker.name = marker.name + " " + data;
                                        } else {
                                            marker.name = marker.name + " " + geom.m_attributes.get("Description").m_value.toString();
                                        }

                                    }
                                } else if (!geom.m_attributes.keySet().isEmpty()) {
                                    marker.name = (String) geom.m_attributes.get(geom.m_attributes.keySet().toArray()[0]).m_value;
                                } else {
                                    marker.name = String.valueOf(geom.m_ID);
                                }
                                marker.lon = line.m_points.get(0).m_lon;
                                marker.lat = line.m_points.get(0).m_lat;
                                marker.description = "";
                                marker.image = "";
                                marker.diag = 0;
                                result.add(marker);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public void StartEditingPOI(GIEditableLayer layer, GI_WktGeometry geometry) {
        boolean toRedraw = false;
        activity.setState(GIEditingStatus.EDITING_POI);
        currentLayer = layer;
        currentGeometry = geometry;
        for (GIEditableLayer old : getEditableLayers()) {
            if (old.m_Status != GIEditableLayer.GIEditableLayerStatus.UNEDITED) {
                toRedraw = true;
                old.Save();
                old.m_Status = GIEditableLayer.GIEditableLayerStatus.UNEDITED;
            }
        }
        for (GIGeometryControl control : controls) {
            control.Disable();
        }
        controls.clear();

        if (layer.m_Status == GIEditableLayer.GIEditableLayerStatus.UNEDITED) {
            layer.m_Status = GIEditableLayer.GIEditableLayerStatus.EDITED;

            for (GI_WktGeometry geom : layer.m_shapes) {
                GIGeometryControl geometry_control = new GIGeometryControl(this, poiLayer, geom);
                if (geom == currentGeometry) {
                    currentEditingControl = geometry_control;
                    /**/
                    currentEditingControl.m_points.get(0).setActiveStatus(true);
                    currentEditingControl.m_points.get(0).setChecked(false);
                    currentEditingControl.m_points.get(0).invalidate();
                    activity.setState(GIEditingStatus.EDITING_GEOMETRY);
					/**/
                }
                controls.add(geometry_control);
            }
        }

        activity.showEditAttributesFragment(currentGeometry);
        currentLayer.m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
        currentLayer.Save();
        ((GI_WktPoint) currentGeometry).m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
        currentEditingControl.invalidate();
        if (toRedraw) {
            UpdateMap();
        }

    }

    public void StopEditingGeometry() {
        if (currentGeometry == null) {
            return;
        }
        currentGeometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;

        if (currentGeometry.m_type == GI_WktGeometry.GIWKTGeometryType.POLYGON) {
            GI_WktPolygon polygon = (GI_WktPolygon) currentGeometry;
            for (GI_WktLinestring ring : polygon.m_rings) {
                if (ring.m_points.size() > 1) {
                    ring.m_points.get(ring.m_points.size() - 1).m_lon = ring.m_points.get(0).m_lon;
                    ring.m_points.get(ring.m_points.size() - 1).m_lat = ring.m_points.get(0).m_lat;
                }
            }
        }
        for (GIGeometryPointControl c : currentEditingControl.m_points) {
            c.setActiveStatus(false);
            c.setChecked(false);
            c.invalidate();
        }
        currentEditingControl.invalidate();
    }

    public boolean CreateTrack() {
        boolean res = false;
        if (trackLayer == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(App.Instance().dateTimeFormat, Locale.ENGLISH);
            String date = dateFormat.format(new Date(Calendar.getInstance().getTimeInMillis()));

            trackLayer = GILayer.createTrack(ps.m_name, date);
            trackLayer.setType(GILayer.EditableType.TRACK);
            trackLayer.Save();

            ps.m_Group.addEntry(trackLayer.m_layer_properties);
            AddLayer(trackLayer);

        }

        if (trackLayer != null) {
            activity.setTrackingStatus(GITrackingStatus.WRITE);
            currentTrack = new GIXMLTrack();

            currentTrack.m_attributes = new HashMap<String, GIDBaseField>();
            for (String key : trackLayer.getAttributes().keySet()) {
                currentTrack.m_attributes.put(key, new GIDBaseField(trackLayer.getAttributes().get(key)));
            }
            String time = CommonUtils.getCurrentTime();
            GIDBaseField field = new GIDBaseField();
            field.m_name = "Description";
            field.m_value = time;
            currentTrack.m_attributes.put("Description", field);

            GIDBaseField proj_field = new GIDBaseField();
            proj_field.m_name = "Project";
            proj_field.m_value = ps.m_name;
            currentTrack.m_attributes.put("Project", proj_field);


            res = ((GIXMLTrack) currentTrack).Create(ps.m_name, ps.m_name + CommonUtils.getCurrentTimeShort(), trackLayer.getStyle(), trackLayer.getEncoding());
            currentTrack.m_status = GI_WktGeometry.GIWKTGeometryStatus.NEW;
            trackLayer.m_shapes.add(currentTrack);

            //todo
            currentTrackControl = new GIGeometryControl(this, trackLayer, currentTrack);
            currentTrackControl.setMap(this);

            trackLayer.Save();
        }
        return res;
    }

    public void AddPointToTrack(GILonLat lonlat, float accurancy) {
        GIXMLTrack track = (GIXMLTrack) currentTrack;
        if (track == null) {
            return;
        }
        GI_WktPoint point = new GI_WktPoint();
        point.Set(lonlat);
        point.m_attributes = new HashMap<String, GIDBaseField>();
        GIDBaseField field = new GIDBaseField();
        field.m_name = "Description";
        field.m_value = CommonUtils.getCurrentTime();
        point.m_attributes.put("Description", field);
        if (currentTrackControl != null) {
            ((GIXMLTrack) currentTrackControl.m_geometry).AddPoint(point, accurancy);
            currentTrackControl.invalidate();
        }
    }

    public void CreatePOI() {
        if (poiLayer != null) {
            GI_WktPoint point = new GI_WktPoint();
            GILonLat location = GIProjection.ReprojectLonLat(Center(), Projection(), GIProjection.WGS84());
            point.Set(location);
            point.m_attributes = new HashMap<String, GIDBaseField>();
            for (String key : poiLayer.getAttributes().keySet()) {
                point.m_attributes.put(key, new GIDBaseField(poiLayer.getAttributes().get(key)));
            }
            GIDBaseField field = new GIDBaseField();
            field.m_name = "DateTime";
            field.m_value = CommonUtils.getCurrentTime();
            point.m_attributes.put("DateTime", field);


            GIDBaseField proj_field = new GIDBaseField();
            proj_field.m_name = "Project";
            proj_field.m_value = ps.m_name;
            point.m_attributes.put("Project", proj_field);

            poiLayer.AddGeometry(point);
            StartEditingPOI(poiLayer, point);
        }
    }

    public void StopTrack() {
        if (currentTrack != null) {
            if (currentTrack.m_type == GI_WktGeometry.GIWKTGeometryType.TRACK) {
                GIXMLTrack track = (GIXMLTrack) currentTrack;
                track.StopTrack();
            }
        }
        if (currentTrackControl != null) {
            currentTrackControl.Disable();
        }
        currentTrack = null;
        UpdateMap();
    }

    public void FillAttributes() {
        if (currentGeometry != null) {
            if (!currentGeometry.IsEmpty()) {
                activity.showEditAttributesFragment(currentGeometry);
            }
            activity.setState(GIEditingStatus.RUNNING);
            StopEditingGeometry();
        }
    }

    public boolean ClickAt(GILonLat point, GIBounds area) {

        boolean res = false;
        switch (activity.getState()) {
            case WAITIN_FOR_SELECT_OBJECT: {
                for (GI_WktGeometry geometry : getCurrentLayer().m_shapes) {
                    if (geometry.isTouch(area)) {
                        currentGeometry = geometry;
                        activity.showEditAttributesFragment(geometry);
                        activity.setState(GIEditingStatus.RUNNING);
                        activity.btnEditAttributes.setChecked(false);
                        UpdateMap();
                        res = true;
                    }
                }
                break;
            }
            case WAITING_FOR_OBJECT_NEWLOCATION: {
                switch (currentGeometry.m_type) {
                    case POINT: {
                        ((GI_WktPoint) currentGeometry).Set(point);
                        activity.showEditAttributesFragment(currentGeometry);
                        activity.setState(GIEditingStatus.RUNNING);
                        getCurrentLayer().m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
                        getCurrentLayer().Save();
                        ((GI_WktPoint) currentGeometry).m_status = GI_WktGeometry.GIWKTGeometryStatus.MODIFIED;
                        currentEditingControl.addPoint((GI_WktPoint) currentGeometry);
                        currentEditingControl.invalidate();

                        res = true;
                        UpdateMap();

//						m_EditLayerDialog.m_btnNew.setChecked(false);
                        activity.btnEditCreate.setChecked(false);
                        break;
                    }
                    case LINE: {
                        GI_WktPoint p = new GI_WktPoint(point);
                        ((GI_WktLinestring) currentGeometry).AddPoint(p);

                        currentEditingControl.addPoint(p);
                        currentEditingControl.invalidate();
                        getCurrentLayer().m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
                        getCurrentLayer().Save();
                        res = true;
                        break;
                    }
                    case POLYGON: {
                        GI_WktPoint p = new GI_WktPoint(point);

                        ((GI_WktPolygon) currentGeometry).AddPoint(p);

                        currentEditingControl.addPoint(p);
                        currentEditingControl.invalidate();
                        getCurrentLayer().m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
                        getCurrentLayer().Save();
                        res = true;
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            case WAITING_FOR_TO_DELETE: {
                for (GI_WktGeometry geometry : getCurrentLayer().m_shapes) {
                    if (geometry.isTouch(area)) {
                        currentGeometry = geometry;
                        activity.setState(GIEditingStatus.RUNNING);
                        for (GIGeometryControl control : getControls()) {
                            if (control.m_geometry == currentGeometry) {
                                currentEditingControl = control;
                                continue;
                            }
                        }
                        res = true;
                    }
                }
                if (res) {
                    activity.setState(GIEditingStatus.RUNNING);
                    getCurrentLayer().m_shapes.remove(currentGeometry);
                    getCurrentLayer().DeleteObject(currentGeometry);
                    currentGeometry.Delete();
                    currentGeometry = null;
                    getCurrentLayer().m_Status = GIEditableLayer.GIEditableLayerStatus.UNSAVED;
                    getCurrentLayer().Save();
                    UpdateMap();
                    activity.btnEditDelete.setChecked(false);

                    for (GIGeometryPointControl c : currentEditingControl.m_points) {
                        c.Remove();
                    }
                    currentEditingControl.Disable();
                }

                break;

            }
            case WAITING_FOR_SELECT_GEOMETRY_TO_EDITING: {
                boolean reDraw = false;
                for (GI_WktGeometry geometry : getCurrentLayer().m_shapes) {
                    if (geometry.isTouch(area)) {
                        currentGeometry = geometry;
                        currentGeometry.m_status = GI_WktGeometry.GIWKTGeometryStatus.GEOMETRY_EDITING;
                        for (GIGeometryControl control : getControls()) {
                            if (control.m_geometry == currentGeometry) {
                                currentEditingControl = control;
                                for (GIGeometryPointControl c : currentEditingControl.m_points) {
                                    c.setActiveStatus(true);
                                    c.setChecked(false);
                                    c.invalidate();
                                }
                                continue;
                            }
                        }
                        currentEditingControl.invalidate();
                        activity.setState(GIEditingStatus.EDITING_GEOMETRY);
                        reDraw = true;
                    }
                }
                if (reDraw) {
                    UpdateMap();
                }
                break;
            }
            //case EDITING_GEOMETRY:
            case WAITING_FOR_NEW_POINT_LOCATION: {
                activity.setState(GIEditingStatus.EDITING_GEOMETRY);

                for (GIGeometryPointControl control : currentEditingControl.m_points) {
                    if (control.getChecked()) {
                        control.m_WKTPoint.m_lon = point.lon();
                        control.m_WKTPoint.m_lat = point.lat();

                        control.setWKTPoint(control.m_WKTPoint);

                        control.setChecked(false);
                    }

                }
                currentEditingControl.invalidate();

                activity.setState(GIEditingStatus.EDITING_GEOMETRY);

                break;
            }
            default:
                break;
        }
        return res;
    }

    class RenderTask implements Runnable {
        GIBounds actual_bounds;

        public void run() {
            actual_bounds = new GIBounds(m_bounds.projection(), m_bounds.left(), m_bounds.top(), m_bounds.right(), m_bounds.bottom());
            System.gc();
            final Bitmap tmp_bitmap = Bitmap.createBitmap(m_view.width(), m_view.height(), Bitmap.Config.ARGB_8888);
            tmp_bitmap.eraseColor(Color.WHITE);
            double scale_ = GIMap.getScale(m_bounds, m_view);
            synchronized (m_layers) {
                m_layers.Redraw(actual_bounds, tmp_bitmap, 255, scale_);
            }

            //if(!Thread.currentThread().isInterrupted())
            {
                //Log.d(LOG_TAG, "current " + Thread.currentThread().getId() + " proceed");
                m_handler.post(new Runnable() {
                    public void run() {
                        target.RenewBitmap(tmp_bitmap, actual_bounds);
                    }
                });
            }
            target.m_threadStack.kick(true);
            return;
        }
    }

    class DraftRenderTask implements Runnable {

        GIBounds actual_bounds;

        public void run() {

            actual_bounds = new GIBounds(m_bounds.projection(), m_bounds.left() - m_bounds.width(),
                    m_bounds.top() + m_bounds.height(), m_bounds.right() + m_bounds.width(), m_bounds.bottom() - m_bounds.height());
            System.gc();
            final Bitmap tmp_bitmap = Bitmap.createBitmap(m_view.width(), m_view.height(), Bitmap.Config.ARGB_8888);
            double scale_ = GIMap.getScale(actual_bounds, m_view);
            synchronized (m_layers) {
                m_layers.Redraw(actual_bounds, tmp_bitmap, 255, scale_ / 3);
            }
            m_handler.post(new Runnable() {
                public void run() {
                    target.RenewBitmapLarge(tmp_bitmap, actual_bounds);
                }
            });

            target.m_threadStack.kick(true);

            return;

        }
    }

    class ThreadStack {

        Thread current;
        Thread next;
        boolean ToDoDraft;
        boolean m_is_draft_nesessary;

        ThreadStack() {
            current = null;
            next = null;
            ToDoDraft = false;
            m_is_draft_nesessary = true;
        }

        public void setToDraft(boolean needed) {
            ToDoDraft = needed;
        }

        public boolean IsAlive() {
            if (current != null) {
                if (current.isAlive()) {
                    return true;
                }
            }
            return false;
        }

        public void addTask() {
            if (next != null) {
//				Thread dummy = next;
//				next = null;
//				dummy.interrupt();
                next.interrupt();
            }
            next = new Thread(new RenderTask());
            ToDoDraft = true;
            kick(false);
        }

        public void kick(boolean suppress) {
            //Log.d(LOG_TAG_THREAD, "kick");
            if (current != null && (current.getState() == Thread.State.RUNNABLE) && !suppress)//current.isAlive() !current.isInterrupted()
            {
//				Thread dummy = current;
//				current = null;
//				dummy.interrupt();
                current.interrupt();
                //Log.d(LOG_TAG_THREAD, "current " + current.getId() + " interrupting");
                return;
            } else {
                if (next != null) {
                    current = next;
                    next = null;
                    //Log.d(LOG_TAG, "Next " + current.getId() + " starting as current");
                    // TODO MAX_PRIORITY
                    current.setPriority(Thread.MIN_PRIORITY);
                    current.start();
                } else {
                    if (ToDoDraft) {
                        ToDoDraft = false;
                        current = new Thread(new DraftRenderTask());
                        // TODO MAX_PRIORITY
                        current.setPriority(Thread.MIN_PRIORITY);
                        current.start();
                    }
                }
            }
        }

        public void go_next() {
            if (next != null) {
                current = next;
                next = null;
                current.start();
            } else {
                current = new Thread(new DraftRenderTask());
                current.start();
            }
        }

    }

}
