package ru.tcgeo.application.gilib.gps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class GICompassView extends SurfaceView implements SurfaceHolder.Callback
{
	private GICompassDrawThread drawThread;
	private Context mContext;
	public GICompassView(Context context)
	{
		super(context);
		mContext = context;
		getHolder().addCallback(this);
		setZOrderOnTop(true);    // necessary
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}
	public GICompassView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		getHolder().addCallback(this);
		if(!isInEditMode())
		{
		setZOrderOnTop(true);    // necessary
		}
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}
	public GICompassView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
		getHolder().addCallback(this);
		setZOrderOnTop(true);    // necessary
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height)
	{
		int whats = 0;

	}

	public void surfaceCreated(SurfaceHolder holder)
	{
	      drawThread = new GICompassDrawThread(mContext, getHolder());
	      drawThread.setRunning(true);
	      drawThread.start();
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
    @Override
    protected void onDraw(Canvas canvas)
    {
    	//canvas.drawColor(Color.TRANSPARENT);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onWindowVisibilityChanged(int visibility)
    {
    	super.onWindowVisibilityChanged(visibility);
    }
    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom)
    {
    	super.onLayout(changed, left, top, right, bottom);
    }

}
