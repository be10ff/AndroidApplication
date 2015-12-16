package ru.tcgeo.application.gilib.gps;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import ru.tcgeo.application.App;
import ru.tcgeo.application.R;

public class GICompassDrawThread extends Thread 
{
	private boolean running = false;
	private SurfaceHolder  surfaceHolder;
	Context mContext;
	Bitmap arrow;
	public GICompassDrawThread(SurfaceHolder surfaceHolder)
	{
		this.surfaceHolder = surfaceHolder;

		arrow = BitmapFactory.decodeResource(App.getInstance().getResources(), R.drawable.arrow);
	}

	public GICompassDrawThread(Context context, SurfaceHolder surfaceHolder)
	{
		this.surfaceHolder = surfaceHolder;
		mContext = context;
		arrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
	}
	
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	@Override
	public void run()
	{
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
				float[] orientation =  GISensors.Instance(mContext).getOrientation();
				canvas.rotate(- orientation[0] , canvas.getWidth()/2,canvas.getHeight()/2);
				canvas.drawBitmap(arrow, new Rect(0, 0, (int)arrow_width, (int)arrow_height), new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null); 
			}
			catch(Exception e) {
				String res = e.toString();
			}
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
