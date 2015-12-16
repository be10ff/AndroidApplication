package ru.tcgeo.application.gilib.gps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.GIControl;
import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.GIMap;

public class GILocatorRange extends View implements GIControl
{
	Context m_context;
	GIMap m_map;
	Bitmap image;
	int[] map_location = { 0, 0 };
	Rect m_source;
	Rect m_dest;
	private float m_accurancy;

	public final String tag = "LOCATOR_RANGE_TAG";
	
	public GILocatorRange() 
	{
		super(GIEditLayersKeeper.Instance().getMap().getContext());
		android.view.ViewGroup.LayoutParams params = new LayoutParams(100, 100);
		setLayoutParams(params);
		m_context = GIEditLayersKeeper.Instance().getMap().getContext();	
		m_map = GIEditLayersKeeper.Instance().getMap();
    	m_map.getLocationOnScreen(map_location);
		this.setX(map_location[0]);
		this.setY(map_location[1]);
        m_map.registerGIControl(this);
		RelativeLayout rl = (RelativeLayout)m_map.getParent();//
		setTag(tag);
		Disable();
    	rl.addView(this);
    	image = BitmapFactory.decodeResource(getResources(), R.drawable.range);
    	m_source = new Rect(0, 0, image.getWidth(), image.getHeight());
    	m_dest = new Rect();
    	m_accurancy = 100;
	}
	public void setAccurancy(float a)
	{
		m_accurancy = a;
		android.view.ViewGroup.LayoutParams params = getLayoutParams();
		params.height = (int) (2*m_accurancy);
		params.width = (int) (2*m_accurancy);
		setLayoutParams(params);
		invalidate();
	}
	
	@Override
    protected void onDraw(Canvas canvas)
	{
		m_dest.set(0, 0, canvas.getWidth(), canvas.getHeight());
		canvas.drawBitmap(image, m_source, m_dest, null );
	}

	public GIMap Map() 
	{
		return m_map;
	}

	public void setMap(GIMap map) 
	{
		m_map = map;
		map.registerGIControl(this);
	}

	public void onMapMove() {
		// TODO Auto-generated method stub
		
	}

	public void onViewMove() 
	{
		invalidate();
	}

	public void afterMapFullRedraw(GIBounds bounds, Rect view_rect) {
		// TODO Auto-generated method stub
		
	}

	public void afterMapImageRedraw(GIBounds bounds, Rect view_rect) {
		// TODO Auto-generated method stub
		
	}

	public void onMarkerLayerRedraw(Rect view_rect) {
		// TODO Auto-generated method stub
		
	}

	public void afterViewRedraw() {
		// TODO Auto-generated method stub
		
	}

	public void Disable()
	{
		RelativeLayout rl = (RelativeLayout)m_map.getParent();
		View v = rl.findViewWithTag(tag);
		if(v != null)
		{
			rl.removeView(v);
		}
	}

}
