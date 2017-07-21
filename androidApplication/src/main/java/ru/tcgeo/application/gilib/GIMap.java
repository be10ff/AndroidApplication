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
import java.util.ArrayList;
import java.util.List;

import ru.tcgeo.application.gilib.gps.GIXMLTrack;
import ru.tcgeo.application.gilib.models.GIBitmap;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIColor;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIPList;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIScaleRange;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
import ru.tcgeo.application.gilib.parser.GIPropertiesGroup;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayer;
import ru.tcgeo.application.gilib.parser.GIPropertiesLayerRef;
import ru.tcgeo.application.gilib.parser.GISQLDB;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktLinestring;
import ru.tcgeo.application.wkt.GI_WktPoint;


public class GIMap extends SurfaceView //implements SurfaceHolder.Callback//implements Runnable SurfaceView
{
	// view diagonal in inches
    static public double inches_per_pixel = 0.0066;
    static public float offsetY;
    public static double meters_per_inch = 0.0254f;
    public final String LOG_TAG = "LOG_TAG";
    public Rect m_view;        // view size
    public ru.tcgeo.application.gilib.parser.GIProjectProperties ps;
    //TODO: make private
	public GIGroupLayer m_layers;
    GIBitmap m_smooth;
    GIBitmap m_draft;
    GIBounds m_bounds;    // current view extent & projection
    Rect m_view_rect;    // viewable part of bitmap
    Handler m_handler;
	SurfaceHolder m_holder;
	ThreadStack m_threadStack;
	
	//GIControl's works
    ru.tcgeo.application.gilib.GIMap target = this;
    private ArrayList<GIControl> m_listeners = new ArrayList<GIControl>();

    public GIMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize();
    }

    public GIMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public GIMap(Context context) {
        super(context);
        this.initialize();
    }

    public static double getScale(GIBounds bounds, Rect rect) {
        //final static double meters_per_inch = 0.0254f;
        GIBounds metric = bounds.Reprojected(GIProjection.WorldMercator());
        double rect_diag_meters = Math.hypot(rect.width(), rect.height()) * inches_per_pixel * meters_per_inch;
        return rect_diag_meters / Math.hypot(metric.width(), metric.height());
    }

    public void registerGIControl(GIControl control)
	{
		m_listeners.add(control);
	}

    public void unRegisterGIControl(GIControl control)
	{
		m_listeners.remove(control);
		RelativeLayout rl = (RelativeLayout)getParent();
		rl.removeView((View)control);
	}

	protected void fire_afterMapFullRedraw()
	{
		for(GIControl control: m_listeners)
		{
			control.afterMapFullRedraw(m_bounds, m_view);
		}
	}

	protected void fire_afterImageFullRedraw()
	{
		for(GIControl control: m_listeners)
		{
			control.afterMapImageRedraw(m_bounds, m_view);
		}
	}

	protected void fire_onMarkerLayerlRedraw()
	{
		for(GIControl control: m_listeners)
		{
			control.onMarkerLayerRedraw(m_view);
		}
	}

	protected void fire_onViewMove()
	{
		invalidate();
		for(GIControl control: m_listeners)
		{
			control.onViewMove();
		}
	}
	
	protected void fire_onMapMove()
	{
		for(GIControl control: m_listeners)
		{
			control.onMapMove();
		}
	}

	protected void fire_afterViewRedraw()
	{
		for(GIControl control: m_listeners)
		{
			control.afterViewRedraw();
		}
	}

	@Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
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
	    Log.d(LOG_TAG, "onSize from (" + oldw + " ," + oldh + ") to (" + w + " ," + h+ ")");

	    if(oldw != 0 && oldh !=0)
	    {
            BoundsChanging(w, h, oldw, oldh);
        }
	    else
	    {
	    	AdjustBoundsRatio();
	    }

	    if(m_smooth == null)
	    {
	    	m_smooth = new GIBitmap(m_bounds, m_view.width(), m_view.height());
	    }
	    UpdateMap();
    }

	protected void BoundsChanging(int w, int h, int oldw, int oldh)
	{

		int dx = (w - oldw)/2;
		int dy = (h - oldh)/2;
		Point LeftTop = new Point(-dx, -dy);
		Point RightBottom = new Point(w - dx, h - dy);

        double pixelWidth = m_bounds.width() / oldw;
        double pixelHeight = m_bounds.height() / oldh;

		double lonlt = m_bounds.left() + pixelWidth*LeftTop.x;
        double latlt = m_bounds.top() - pixelHeight * LeftTop.y;

		double lon = m_bounds.left() + pixelWidth*RightBottom.x;
		double lat = m_bounds.top() - pixelHeight*RightBottom.y;

		m_bounds = new GIBounds(m_bounds.projection(), lonlt , latlt, lon, lat);
		fire_onViewMove();
	}

	@Override
    protected void onDraw (Canvas canvas)
    {
		if(m_holder == null)
			return;
		if(m_holder.getSurface() == null)
			return;
		int i = 0;
		while(!m_holder.getSurface().isValid())
		{
            i++;
        }
		Canvas holded_canvas = m_holder.lockCanvas();
		holded_canvas.drawColor(Color.WHITE);
		if(m_draft != null)
		{
			m_draft.Draw(holded_canvas, m_bounds);
		}
		m_smooth.Draw(holded_canvas, m_bounds);
		m_holder.unlockCanvasAndPost(holded_canvas);

    }
	
	private void initialize ()
	{
		setWillNotDraw(false);
		m_layers = new GIGroupLayer();
		m_holder = getHolder();
		m_handler = new Handler();
		m_threadStack = new ThreadStack();
        m_bounds = new GIBounds(GIProjection.WGS84(), 0, 90, 90, 0);
        InitBounds(m_bounds.Reprojected(GIProjection.WorldMercator()));
	}
	
	public void Clear()
	{
		m_layers.RemoveAll();
		initialize();
	}
	
	public void InitBounds (GIBounds initial_extent)
	{
		m_bounds = initial_extent;
		AdjustBoundsRatio();
	}
	
	private void AdjustBoundsRatio ()
	{
		if(m_view == null)
			return;
		if(m_bounds == null)
			return;
		
		double ratio = (double)m_view.width() / (double)m_view.height();
		
		
		if (m_bounds.width() / m_bounds.height() == ratio)
		{
			return; // we're good
		}
		else if (m_bounds.width() / m_bounds.height() > ratio)
		{
			// height should be expanded
			double diff = (m_bounds.width() / (double)m_view.width()) * (double)m_view.height() - m_bounds.height();
			m_bounds = new GIBounds(m_bounds.projection(), 
									m_bounds.left(), 
									m_bounds.top() + diff/2,
									m_bounds.right(), 
									m_bounds.bottom() - diff/2);
			fire_onViewMove();
		}
		else
		{
			// width should be expanded
			double diff = (m_bounds.height() / (double)m_view.height()) * (double)m_view.width() - m_bounds.width();
			m_bounds = new GIBounds(m_bounds.projection(), 
									m_bounds.left() - diff/2, 
									m_bounds.top(), 
									m_bounds.right() + diff/2, 
									m_bounds.bottom());
			fire_onViewMove();
		}
		
	}

	
	public void AddLayer (GILayer layer)
	{
		m_layers.AddLayer(layer);
	}

	public void AddLayer (GILayer layer, GIScaleRange range, boolean enabled)
	{
		m_layers.AddLayer(layer, range, enabled);
	}
	public void InsertLayerAt (GILayer layer, int position)
	{
		m_layers.InsertLayerAt(layer, position);
	}
	
	public GIProjection Projection ()
	{
		return m_bounds.projection();
	}
	

	public GILonLat Center ()
	{
		return new GILonLat((m_bounds.left() + m_bounds.right())/2,
							(m_bounds.top() + m_bounds.bottom())/2);
	}

	
	public void SetCenter (GILonLat point)
	{
		m_bounds = new GIBounds(m_bounds.projection(), point, m_bounds.width(), m_bounds.height());
		fire_onViewMove();
		UpdateMap();
	}
	public double GetTg()
	{
		return ((double)m_view.height() / (double)m_view.width());
	}
	public double GetCos()
	{
		double alpha = Math.atan(GetTg());
		return Math.cos(alpha);
	}	
	public double GetSin()
	{
		double alpha = Math.atan(GetTg());
		return Math.sin(alpha);
	}		
	public double getPixelWidth()
	{
		return m_bounds.width() / m_view_rect.width(); 
	}
	public double getPixelHeight()
	{
		return m_bounds.height() / m_view_rect.height(); 
	}
	
	public double getDistance(Point distance)
	{
		double pixelWidth = m_bounds.width() / m_view.width(); 
		double pixelHeight = m_bounds.height() / m_view.height();
		double lon = pixelWidth*distance.x;
		double lat = pixelHeight*distance.y;
		double res = Math.hypot(lon, lat);
		
		return res;
	}
	public void SetCenter (GILonLat point, double diagonal)
	{
		GILonLat center = GIProjection.ReprojectLonLat(point, GIProjection.WGS84(), this.Projection());
		GIBounds new_bounds = new GIBounds(this.Projection(), center, diagonal*GetCos(), diagonal*GetSin());
		SetBounds(new_bounds);
	}

	public void MoveMapBy (double x, double y)
	{
		m_bounds = new GIBounds(m_bounds.projection(), 
								new GILonLat(Center().lon() + x, Center().lat() + y), 
								m_bounds.width(), 
								m_bounds.height());
		fire_onViewMove();
		UpdateMap();
	}
	
	public void MoveViewBy (int x, int y)
	{
		
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
	
	public GIBounds Bounds()
	{
		return m_bounds;
	}
	
	public void SetBounds (GIBounds bounds)
	{
		m_bounds = bounds;
		AdjustBoundsRatio();
		fire_onViewMove();
		UpdateMap();
	}
	
	public double Width ()
	{
		return m_bounds.width();
	}
	
	public double Height ()
	{
		return m_bounds.height();
	}
	public double getScaleFactor()
	{
			return GIMap.getScale(m_bounds, m_view_rect);
	}
	
	// Factor < 1 is Zoom in, > 1 is Zoom out.
	// from TougchControl
	public void ScaleViewBy (Point focus, double factor)
	{
		double ratio = (double)m_view.width() / (double)m_view.height();
		
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
		if (pixW / pixH == ratio)
		{
            // we're good
        }
		else if (pixW / pixH > ratio)
		{
			// height should be expanded
			double diff = (pixW / m_view.width()) * m_view.height() - pixH;
			new_top -= diff/2; 
			new_bottom += diff/2;
		}
		else
		{
			// width should be expanded
			double diff = (pixH / m_view.height()) * m_view.width() - pixW;
			new_left -= diff/2; 
			new_right += diff/2; 
		}
		
		m_view_rect.set((int)new_left, (int)new_top, (int)new_right, (int)new_bottom);
		
		m_bounds = new GIBounds(m_bounds.projection(),
								b_focus_x - (focus.x - (int)new_left)*pixelWidth, 
								b_focus_y + (focus.y - (int)new_top)*pixelHeight, 
								b_focus_x - (focus.x - (int)new_right)*pixelWidth, 
								b_focus_y + (focus.y - (int)new_bottom)*pixelHeight);
		fire_onViewMove();
	}
	
	// Factor < 1 is Zoom in, > 1 is Zoom out.
	// from buttons
	public void ScaleMapBy (GILonLat focus, double factor)
	{
		//ARAB
		//cant see a reason
		//Point _focus = MercatorMapToScreen(focus);
		Point _focus = new Point( m_view.centerX(), m_view.centerY());
		ScaleViewBy(_focus, factor);
		/*m_bounds = new GIBounds(m_bounds.projection(),
                				focus.lon() - (focus.lon() - m_bounds.left()) / factor,
                				focus.lat() - (focus.lat() - m_bounds.top()) / factor,
                				focus.lon() - (focus.lon() - m_bounds.right()) / factor,
                				focus.lat() - (focus.lat() - m_bounds.bottom()) / factor);*/
		this.invalidate();
		UpdateMap();		
	}

	public void UpdateMap ()
	{
		m_view_rect = new Rect(m_view);
		m_threadStack.addTask();
		fire_afterMapFullRedraw();
	}

	public void setToDraft(boolean needed)
	{
		m_threadStack.setToDraft(needed);
	}

    public void RenewBitmap(Bitmap bitmap, GIBounds bounds)
	{
		if(bitmap != null)
		{
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

	public void RenewBitmapLarge(Bitmap bitmap, GIBounds bounds)
	{

		if(bitmap != null)
		{
			/*if(large_bitmap != null)
			{
				large_bitmap.recycle();
				//System.gc();
			}*/
			if(m_draft != null)
			{
				m_draft.Set(bounds, bitmap);
			}
			else
			{
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
	public GIBounds getrequestArea(Point point)
	{
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();

		double area_width = pixelWidth * 30;
		double area_height = pixelHeight * 30;

		double lon = m_bounds.left() + pixelWidth*point.x;
        double lat = m_bounds.top() - pixelHeight * point.y;

        GIBounds requestArea = new GIBounds(m_bounds.projection(), new GILonLat(lon, lat), area_width, area_height);
        return requestArea;
	}

	GIDataRequestor RequestDataInPoint(Point point, GIDataRequestor requestor)
	{
		synchronized(m_layers)
		{
		double scale_ = GIMap.getScale(m_bounds, m_view);

            double pixelWidth = m_bounds.width() / m_view_rect.width();
            double pixelHeight = m_bounds.height() / m_view_rect.height();

            double area_width = pixelWidth * 30;
		double area_height = pixelHeight * 30;

            double lon = m_bounds.left() + pixelWidth*point.x;
            double lat = m_bounds.top() - pixelHeight * point.y;

        GIBounds requestArea = new GIBounds(m_bounds.projection(), new GILonLat(lon, lat), area_width, area_height);
		requestor.StartGatheringData(new GILonLat(lon, lat));
		m_layers.RequestDataIn(requestArea, requestor, scale_);
		requestor.EndGatheringData(new GILonLat(lon, lat));
		}
		return requestor;
	}

    public GILonLat ScreenToMap(Point point)
	{
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();
		double lon = m_bounds.left() + pixelWidth*point.x;
        double lat = m_bounds.top() - pixelHeight * point.y;
        GILonLat lonlat = new GILonLat(lon, lat);
		GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, this.Projection(), GIProjection.WGS84());
		return new_lonlat;
	}

    public GILonLat ScreenToMercatorMap(Point point)
	{
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();
		double lon = m_bounds.left() + pixelWidth*point.x;
        double lat = m_bounds.top() - pixelHeight * point.y;
        GILonLat lonlat = new GILonLat(lon, lat);
		return lonlat;
	}

    public Point MercatorMapToScreen(GILonLat lonlat)
	{
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();
		int point_x = (int)((lonlat.lon() - m_bounds.left())/pixelWidth);
		int point_y = (int)((m_bounds.top() - lonlat.lat())/pixelHeight);
		return new Point(point_x, point_y);
	}

    public Point MapToScreen(GILonLat lonlat)
	{
        double pixelWidth = m_bounds.width() / m_view_rect.width();
        double pixelHeight = m_bounds.height() / m_view_rect.height();
		GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, GIProjection.WGS84(), this.Projection());
		int point_x = (int)((new_lonlat.lon() - m_bounds.left())/pixelWidth);
		int point_y = (int)((m_bounds.top() - new_lonlat.lat())/pixelHeight);
		return new Point(point_x, point_y);
	}

    public Point MapToScreenTempo(GILonLat lonlat)
	{
        double pixelWidth = m_bounds.width() / m_view.width();
        double pixelHeight = m_bounds.height() / m_view.height();
		GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, GIProjection.WGS84(), this.Projection());
		int point_x = (int)((new_lonlat.lon() - m_bounds.left())/pixelWidth);
		int point_y = (int)((m_bounds.top() - new_lonlat.lat())/pixelHeight);
		return new Point(point_x, point_y);
	}

	public Rect MapToScreen(GIBounds bounds)
	{
        double pixelWidth = m_bounds.width() / m_view.width();
        double pixelHeight = m_bounds.height() / m_view.height();

        int left = (int)((bounds.left() - m_bounds.left())/pixelWidth);
		//int right = (int)((bounds.right() - m_bounds.left())/pixelWidth);
		int top = (int)((m_bounds.top() - bounds.top())/pixelHeight);
		//int bottom = (int)((m_bounds.top() - bounds.bottom())/pixelHeight);

        Rect test = new Rect(m_view);
		test.offset(-left, -top);
		//Rect res = new Rect(left, top, right, bottom);
		return test;
	}

    public RectF MapToScreenDraw(GIBounds bounds)
	{
        double pixelWidth = m_bounds.width() / m_view.width();
        double pixelHeight = m_bounds.height() / m_view.height();
		float left = (float)((bounds.left() - m_bounds.left())/pixelWidth);
		float right = (float)((bounds.right() - m_bounds.left())/pixelWidth);
		float top = (float)((m_bounds.top() - bounds.top())/pixelHeight);
		float bottom = (float)((m_bounds.top() - bounds.bottom())/pixelHeight);
		RectF res = new RectF(left, top, right, bottom);
		return res;
	}

    public GILonLat MetersToDegrees(GILonLat lonlat)
	{
		GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, this.Projection(), GIProjection.WGS84());
		return new_lonlat;
	}

	/*			actual_bounds = new GIBounds(m_bounds.m_projection, m_bounds.m_left, m_bounds.m_top, m_bounds.m_right, m_bounds.m_bottom);
			System.gc();
			final Bitmap tmp_bitmap = Bitmap.createBitmap(m_view.width(), m_view.height(), Bitmap.Config.ARGB_8888);*/

	public double MetersInPixel()
	{

        GIBounds wgs_bounds = m_bounds.Reprojected(GIProjection.WGS84());
		double dist = MapUtils.GetDistanceBetween(wgs_bounds.TopLeft(), wgs_bounds.BottomRight());
		double px_dist = Math.hypot(m_view.width(), m_view.height());

        double meters_in_px = dist/px_dist;
		return meters_in_px;

	}

    public void Synhronize()
	{
		GIBounds wgs_bounds = m_bounds.Reprojected(GIProjection.WGS84());
		ps.m_left = wgs_bounds.left();
		ps.m_top = wgs_bounds.top();
		ps.m_right = wgs_bounds.right();
		ps.m_bottom = wgs_bounds.bottom();

        for(GITuple tuple : m_layers.m_list)
		{
			tuple.layer.m_layer_properties.m_enabled = tuple.visible;
		}
	}

    public int getOffsetY()
	{
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		return displayMetrics.heightPixels - getMeasuredHeight();
	}

	public void LoadProject(String path) {
		ps = new GIProjectProperties(path);
		GIBounds temp = new GIBounds(ps.m_projection, ps.m_left,
				ps.m_top, ps.m_right, ps.m_bottom);
		InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
		GIPropertiesGroup current_group = ps.m_Group;
		GIEditLayersKeeper.Instance().ClearLayers();
		loadGroup(current_group);
	}

	private void loadGroup(GIPropertiesGroup current_layer2)
	{
		for (GIPropertiesLayer current_layer : current_layer2.m_Entries)
		{
			if (current_layer.m_type == GILayer.GILayerType.LAYER_GROUP) {
				loadGroup((GIPropertiesGroup) current_layer);
			}
			if (current_layer.m_type == GILayer.GILayerType.TILE_LAYER) {
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("local")) {
					layer = GILayer.CreateLayer(
							current_layer.m_source.GetLocalPath(),
							GILayer.GILayerType.TILE_LAYER);
					layer.setName(current_layer.m_name);
					layer.m_layer_properties = current_layer;
					AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
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
					AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
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
					AddLayer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
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
					AddLayer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
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
					AddLayer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
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
					AddLayer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				}
				else
				{
					continue;
				}

			}
			if (current_layer.m_type == GILayer.GILayerType.FOLDER)
			{
				GILayer layer;
				if (current_layer.m_source.m_location.equalsIgnoreCase("text"))
				{
					layer = GILayer.CreateLayer(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + current_layer.m_source.GetRemotePath(),	GILayer.GILayerType.FOLDER);
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
					AddLayer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
				}
				else if(current_layer.m_source.m_location.equalsIgnoreCase("absolute"))
				{
					layer = GILayer.CreateLayer(current_layer.m_source.GetAbsolutePath(),	GILayer.GILayerType.FOLDER);

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
					AddLayer(layer, new GIScaleRange(current_layer.m_range), current_layer.m_enabled);
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
					AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
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
					AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
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

					AddLayer(layer,
							new GIScaleRange(current_layer.m_range),
							current_layer.m_enabled);
					GIEditLayersKeeper.Instance().AddLayer(
							(GIEditableLayer) layer);
				}
			}

		}
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
                for (GITuple tuple : m_layers.m_list) {
                    if (tuple.layer.getName()
                            .equalsIgnoreCase(ps.m_markers)) {
                        layer = (GIEditableLayer) tuple.layer;
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
                                        String data = GIEditLayersKeeper.getTime(geom.m_attributes.get("Description").m_value.toString());
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
                                        String data = GIEditLayersKeeper.getTime(geom.m_attributes.get("Description").m_value.toString());
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


//	public void onMapLoaded(GIProjectProperties ps) {
//		this.ps = ps;
//		GIBounds temp = new GIBounds(ps.m_projection, ps.m_left,
//				ps.m_top, ps.m_right, ps.m_bottom);
//		InitBounds(temp.Reprojected(GIProjection.WorldMercator()));
//		GIPropertiesGroup current_group = ps.m_Group;
//		GIEditLayersKeeper.Instance().ClearLayers();
//		loadGroup(current_group);
//	}

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
