package ru.tcgeo.application.gilib.gps;
/**
 * Локатор - указывает направление на точку на местности
 */

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import ru.tcgeo.application.wkt.GI_WktGeometry;

public class GILocatorView extends SurfaceView implements SurfaceHolder.Callback
{
	private GILocatorDrawThread drawThread;
	public GI_WktGeometry m_POI;
	public GILocatorView(Context context) 
	{
		super(context);
		getHolder().addCallback(this);
		setZOrderOnTop(true);    // necessary
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}
	public GILocatorView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		getHolder().addCallback(this);
		if(!isInEditMode())
		{
		setZOrderOnTop(true);    // necessary
		}
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}
	public GILocatorView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		getHolder().addCallback(this);
		setZOrderOnTop(true);    // necessary
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}
	public void setTarget(GI_WktGeometry poi)
	{
		  m_POI = poi;
	      drawThread = new GILocatorDrawThread(getHolder(), poi, getContext());
	      drawThread.setRunning(true);
	      drawThread.start();
	}

	public void surfaceCreated(SurfaceHolder holder) 
	{
//	      drawThread = new GILocatorDrawThread(getHolder(), null);
//	      drawThread.setRunning(true);
//	      drawThread.start();
	      //setWillNotDraw(false);
	}

	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		boolean retry = true;
		drawThread.setRunning(false);
		while(retry)
		{
			try
			{
				drawThread.join();
				retry = false;
			}
			catch(InterruptedException e){}
		}
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
}
