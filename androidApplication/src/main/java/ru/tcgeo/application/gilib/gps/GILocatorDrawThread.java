package ru.tcgeo.application.gilib.gps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import ru.tcgeo.application.App;
import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.utils.MapUtils;
import ru.tcgeo.application.wkt.GI_WktGeometry;
import ru.tcgeo.application.wkt.GI_WktPoint;

public class GILocatorDrawThread extends Thread 
{
	public GI_WktGeometry m_POI;
    Matrix matrix;
    //Context context;
	GIMap m_map;
	GILonLat m_lon_lat_poi;
	Bitmap arrow;
	int size = 50;
	int length = 2;
	Path path;
	Paint paint_fill;

	GIBounds bounds;
	Context mContext;
    private boolean running = false;
    private SurfaceHolder surfaceHolder;


	public GILocatorDrawThread(SurfaceHolder surfaceHolder, GI_WktGeometry poi, Context context)
	{
		mContext = context;
		this.bounds = bounds;
		this.surfaceHolder = surfaceHolder;
        arrow = BitmapFactory.decodeResource(App.Instance().getResources(), R.drawable.locator_big);
        m_POI = poi;
        m_map = App.Instance().getMap();
        matrix = new Matrix();
    	m_lon_lat_poi = new GILonLat(((GI_WktPoint)m_POI).m_lon, ((GI_WktPoint)m_POI).m_lat);
		paint_fill = new Paint();
		paint_fill.setColor(Color.argb(255, 255, 0, 0));
		paint_fill.setStyle(Style.FILL);
		path = new Path();
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	@Override
	public void run()
	{
		if(m_POI == null)
		{
			return;
		}
		Canvas canvas;
		while(running)
		{
			canvas = null;
			try
			{
				sleep(300);
				float arrow_width = arrow.getWidth();
				float arrow_height = arrow.getHeight();
				canvas  = surfaceHolder.lockCanvas(null);
				if(canvas == null) continue;
				canvas.drawColor(0, PorterDuff.Mode.CLEAR);
//				GILonLat c = GIProjection.ReprojectLonLat(bounds.Center(), bounds.projection(), GIProjection.WGS84());
				GILonLat center = GIProjection.ReprojectLonLat(m_map.Center(), m_map.Projection(), GIProjection.WGS84());
				float[] orientation =  GISensors.Instance(mContext).getOrientation();
				double azimuth = - orientation[0] + MapUtils.GetAzimuth(center, m_lon_lat_poi);
				canvas.rotate((float) azimuth , canvas.getWidth()/2,canvas.getHeight()/2);
				canvas.drawBitmap(arrow, new Rect(0, 0, (int)arrow_width, (int)arrow_height), new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null); 
			}
			catch(Exception e) {}
			finally
			{
				if(canvas != null)
				{
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
