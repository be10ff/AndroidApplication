package ru.tcgeo.application.gilib;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import ru.tcgeo.application.gilib.models.GIBitmap;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIScaleRange;
import ru.tcgeo.application.utils.MapUtils;


public class GIMap extends SurfaceView //implements SurfaceHolder.Callback//implements Runnable SurfaceView
{

	//new bitmap works
	GIBitmap m_smooth;
	GIBitmap m_draft;

	GIBounds m_bounds;	// current view extent & projection
	 public Rect m_view; 		// view size		
	Rect m_view_rect;	// viewable part of bitmap
	public final String LOG_TAG = "LOG_TAG";
	public ru.tcgeo.application.gilib.parser.GIProjectProperties ps;
	
	// view diagonal in inches
	static public double inches_per_pixel = 0.0066;  
	static public float offsetY;
		
	//TODO: make private
	public GIGroupLayer m_layers;
	
	
	Handler m_handler;
	SurfaceHolder m_holder;
	
	ThreadStack m_threadStack;
	ru.tcgeo.application.gilib.GIMap target = this;
	
	//GIControl's works
	
	private ArrayList<GIControl> m_listeners = new ArrayList<GIControl>();
	
	
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
	public GIMap (Context context, AttributeSet attrs, int defStyle)
    {
	    super(context, attrs, defStyle);
	    this.initialize();
    }

	public GIMap (Context context, AttributeSet attrs)
    {
	    super(context, attrs);
	    this.initialize();
    }

	public GIMap (Context context)
	{
		super(context);
		this.initialize();
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
		double latlt = m_bounds.top() - pixelHeight*LeftTop.y; 
		
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
		/*if(large_bitmap != null)
		{
			if(!large_bitmap.isRecycled() && large_bounds != null)
			{
				
				if(m_old_view_rect != null && m_new_view_rect != null )
				{
					Rect rect = new Rect(0, 0, large_bitmap.getWidth(), large_bitmap.getHeight());
					Rect dst = new Rect(rect);
					//Rect dst = new Rect(m_old_view_rect);
					//int dx = (m_new_view_rect.width() - m_old_view_rect.width())/2;
					//int dy = (m_new_view_rect.height() - m_old_view_rect.height())/2;
					//dst.inset(-m_old_view_rect.width(), -m_old_view_rect.height());
					int dx = (m_new_view_rect.width() - rect.width())/2;
					int dy = (m_new_view_rect.height() - rect.height())/2;
					dst.inset(-rect.width(), -rect.height());
					dst.offset(dx, dy);
					//holded_canvas.drawBitmap(large_bitmap, m_old_view_rect, dst, null);
					//holded_canvas.drawBitmap(large_bitmap, rect, dst, null);
					
					//Canvas tmp = new Canvas(large_bitmap);
					//Bitmap bmp = Bitmap.createBitmap(large_bitmap);
					//tmp.drawColor(Color.WHITE);
					//tmp.drawBitmap(bmp, m_old_view_rect, dst, null);

					//Log.d(LOG_TAG, "onDraw draft with new (" + m_new_view_rect.width() + " ," + m_new_view_rect.height() + ") and old  (" + m_old_view_rect.width() + " ," + m_old_view_rect.height() + ")");
					
				}
				else
				{

					//Log.d(LOG_TAG, "onDraw draft with large_bounds");
					RectF screen = MapToScreenDraw(large_bounds);
					//holded_canvas.drawBitmap(large_bitmap, m_view, screen, null);
				}
				//drawBitmap(bitmap, src, dst, paint);
			}
		}*/
		
		/*
		if(!m_bitmap.isRecycled())
		{
			if(m_old_view_rect != null && m_new_view_rect != null)
			{
				Rect rect = new Rect(0, 0, m_bitmap.getWidth(), m_bitmap.getHeight());
				int dx = (m_new_view_rect.width() - rect.width())/2;
				int dy = (m_new_view_rect.height() - rect.height())/2;
				Rect dst = new Rect(rect);
				dst.offset(dx, dy);
				//int dx = (m_new_view_rect.width() - m_old_view_rect.width())/2;
				//int dy = (m_new_view_rect.height() - m_old_view_rect.height())/2;
				//Rect dst = new Rect(m_old_view_rect);
				//dst.offset(dx, dy);
				//holded_canvas.drawBitmap(m_bitmap, m_old_view_rect, dst, null);
				
				//Canvas tmp = new Canvas(m_bitmap);
				//Bitmap bmp = Bitmap.createBitmap(m_bitmap);
				//tmp.drawColor(Color.WHITE);
				//tmp.drawBitmap(bmp, m_old_view_rect, dst, null);
				
				//holded_canvas.drawBitmap(m_bitmap, rect, dst, null);
			}
			else
			{
				//holded_canvas.drawBitmap(m_bitmap, m_view_rect, m_view, null);
			}
		}*/
		if(m_draft != null)
		{
			m_draft.Draw(holded_canvas, m_bounds);
		}
		m_smooth.Draw(holded_canvas, m_bounds);

		
		//m_old_view_rect = null;
		//m_new_view_rect = null;
		m_holder.unlockCanvasAndPost(holded_canvas);

    }
	public static double meters_per_inch = 0.0254f; 
	static double getScale (GIBounds bounds, Rect rect)
	{
		//final static double meters_per_inch = 0.0254f; 
		GIBounds metric = bounds.Reprojected(GIProjection.WorldMercator());
		double rect_diag_meters = Math.hypot(rect.width(), rect.height()) * inches_per_pixel * meters_per_inch;
		return rect_diag_meters / Math.hypot(metric.width(), metric.height()); 
	}
	
	private void initialize ()
	{
		setWillNotDraw(false);
		m_layers = new GIGroupLayer();
		m_holder = getHolder();
		m_handler = new Handler();
		m_threadStack = new ThreadStack();
		//m_holder.addCallback(this);
		//m_current_working = false;
		//m_draft_working = false;
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
	
	public void SetProjection (GIProjection projection)
	{
		m_bounds = m_bounds.Reprojected(projection);
		fire_onViewMove();
		UpdateMap();
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
	
	public void SetCenter (GILonLat point, double diagonal, GIProjection proj)
	{
		// TODO
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
		
		double new_left = 	(focus.x - (double)((double)focus.x - m_view_rect.left)/factor);
		double new_top = 	(focus.y - (double)((double)focus.y - m_view_rect.top)/factor);
		double new_right = 	(focus.x - (double)((double)focus.x - m_view_rect.right)/factor);
		double new_bottom = (focus.y - (double)((double)focus.y - m_view_rect.bottom)/factor);
		

		
		double pixW = new_right - new_left;
		double pixH = new_bottom - new_top;
		
		// Adjust ratio
		if (pixW / pixH == ratio)
		{
			; // we're good
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
	
	class RenderTask implements Runnable 
	{
		GIBounds actual_bounds;
		public void run() 
		{
			actual_bounds = new GIBounds(m_bounds.projection(), m_bounds.left(), m_bounds.top(), m_bounds.right(), m_bounds.bottom());
			System.gc();
			final Bitmap tmp_bitmap = Bitmap.createBitmap(m_view.width(), m_view.height(), Bitmap.Config.ARGB_8888);
			tmp_bitmap.eraseColor(Color.WHITE);
			double scale_ = GIMap.getScale(m_bounds, m_view);
			synchronized(m_layers)
			{
				m_layers.Redraw(actual_bounds, tmp_bitmap, 255, scale_);
			}
			
			//if(!Thread.currentThread().isInterrupted())	
			{
				//Log.d(LOG_TAG, "current " + Thread.currentThread().getId() + " proceed");
				m_handler.post(new Runnable()
				{
					public void run() 
					{
						target.RenewBitmap(tmp_bitmap, actual_bounds);
					}
				});
			}
			target.m_threadStack.kick(true);
			return;
		}
	}
	class DraftRenderTask implements Runnable
	{
		
		GIBounds actual_bounds;
		public void run() 
		{
			
			actual_bounds = new GIBounds(m_bounds.projection(), m_bounds.left() - m_bounds.width(),
			m_bounds.top() + m_bounds.height(), m_bounds.right() + m_bounds.width(), m_bounds.bottom() - m_bounds.height());
			System.gc();
			final Bitmap tmp_bitmap = Bitmap.createBitmap(m_view.width(), m_view.height(), Bitmap.Config.ARGB_8888);
			double scale_ = GIMap.getScale(actual_bounds, m_view);
			synchronized(m_layers)
			{
				m_layers.Redraw(actual_bounds, tmp_bitmap, 255, scale_/3);
			}
			m_handler.post(new Runnable()
			{
				public void run() 
				{
					target.RenewBitmapLarge(tmp_bitmap, actual_bounds);
				}
			});
			
			target.m_threadStack.kick(true);
			
			return;
			
		}
	}
	
	public void UpdateMap ()
	{
		m_view_rect = new Rect(m_view);
		m_threadStack.addTask();
		//m_current_working = true;
		//m_draft_working = true;
		fire_afterMapFullRedraw(); 
	}
	public void setToDraft(boolean needed)
	{
		m_threadStack.setToDraft(needed);
	}

	class ThreadStack
	{
		
		Thread current;
		Thread next;
		boolean ToDoDraft;
		boolean m_is_draft_nesessary;
		ThreadStack()
		{
			current = null;
			next = null;
			ToDoDraft = false;
			m_is_draft_nesessary = true;
		}
		public void setToDraft(boolean needed)
		{
			ToDoDraft = needed;
		}
		public boolean IsAlive()
		{
			if(current != null)
			{
				if(current.isAlive())
				{
					return true;
				}
			}
			return false;
		}
		
		public void addTask()
		{
			if(next != null)
			{
//				Thread dummy = next;
//				next = null;
//				dummy.interrupt();
				next.interrupt();
			}
			next = new Thread(new RenderTask());
			ToDoDraft = true;
			kick(false);
		}
		
		public void kick(boolean suppress)
		{
			//Log.d(LOG_TAG_THREAD, "kick");
			if(current != null && (current.getState() == Thread.State.RUNNABLE) && !suppress)//current.isAlive() !current.isInterrupted()
			{
//				Thread dummy = current;
//				current = null;
//				dummy.interrupt();
				current.interrupt();
				//Log.d(LOG_TAG_THREAD, "current " + current.getId() + " interrupting");
				return;
			}
			else 
			{
				if(next != null)
				{
					current = next;
					next = null;
					//Log.d(LOG_TAG, "Next " + current.getId() + " starting as current");
					// TODO MAX_PRIORITY
					current.setPriority(Thread.MIN_PRIORITY);
					current.start();
				}
				else
				{
					if(ToDoDraft)
					{
						ToDoDraft = false;
						current = new Thread(new DraftRenderTask()); 
						// TODO MAX_PRIORITY
						current.setPriority(Thread.MIN_PRIORITY);
						current.start();
					}
				}
			}
		}
		public void go_next()
		{
			if(next != null)
			{
				current = next;
				next = null;
				current.start();
			}
			else
			{
				current = new Thread(new DraftRenderTask()); ;
				current.start();
			}			
		}		
		
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
		double lat = m_bounds.top() - pixelHeight*point.y; 

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
		double lat = m_bounds.top() - pixelHeight*point.y; 

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
		double lat = m_bounds.top() - pixelHeight*point.y; 
		GILonLat lonlat = new GILonLat(lon, lat);
		GILonLat new_lonlat = GIProjection.ReprojectLonLat(lonlat, this.Projection(), GIProjection.WGS84());
		return new_lonlat;
	}
	public GILonLat ScreenToMercatorMap(Point point)
	{
		double pixelWidth = m_bounds.width() / m_view_rect.width(); 
		double pixelHeight = m_bounds.height() / m_view_rect.height();
		double lon = m_bounds.left() + pixelWidth*point.x;
		double lat = m_bounds.top() - pixelHeight*point.y; 
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
	
	/*			actual_bounds = new GIBounds(m_bounds.m_projection, m_bounds.m_left, m_bounds.m_top, m_bounds.m_right, m_bounds.m_bottom);
			System.gc();
			final Bitmap tmp_bitmap = Bitmap.createBitmap(m_view.width(), m_view.height(), Bitmap.Config.ARGB_8888);*/

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

}
