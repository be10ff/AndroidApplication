package ru.tcgeo.application.gilib;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.utils.MapUtils;

public class GIRuleToolControl extends View implements OnClickListener, GIControl
{

	Context m_context;
	GIMap m_map;
	ArrayList<GILonLat> m_curve;
	int[] map_location = { 0, 0 };
	GIControlFloating m_last;
	Paint m_paint;

	private static GIRuleToolControl instance;
	public static GIRuleToolControl Instance(Context context, GIMap map)
	{
		if(instance == null)
		{
			instance = new GIRuleToolControl(context, map);
		}
		return instance ;
	}


	protected GIRuleToolControl (Context context, GIMap map)
	{
		super(context);
		m_context = context;
		m_map = map;
    	m_map.getLocationOnScreen(map_location);
		this.setX(map_location[0]);
		this.setY(map_location[1]);
		m_curve = new ArrayList<GILonLat>();
        m_paint = new Paint();
        m_paint.setARGB(255, 15, 177, 41);
        m_paint.setStrokeWidth(2);
        m_map.registerGIControl(this);
		RelativeLayout rl = (RelativeLayout)m_map.getParent();//
    	rl.addView(this);
	}
	public void Disable()
	{
		m_curve.clear();
		RelativeLayout rl = (RelativeLayout)m_map.getParent();
    	rl.removeView(this);
    	rl.removeView(m_last);
    	m_last = null;
    	instance = null;
	}

	public void RemoveLastPoint()
	{
		if(m_curve.size() == 1)
		{
			m_curve.remove(m_curve.size() - 1);
			RelativeLayout rl = (RelativeLayout)m_map.getParent();
	    	rl.removeView(m_last);
	    	m_last = null;
		}
		if(m_curve.size() > 1)
		{
			m_curve.remove(m_curve.size() - 1);
			m_last.setLonLat(m_curve.get(m_curve.size() - 1));
		}
		this.invalidate();
	}

	public void AddPoint(GILonLat point)
	{
		m_curve.add(point);
		if(m_last == null)
		{
			m_last = new GIControlFloating(m_context);
			int[] offset = {8, 9};
			m_last = new GIControlFloating(m_context, R.layout.rule_last_point_control, R.id.rule_last_point_text, offset);
			m_last.setMap(m_map);
			RelativeLayout rl = (RelativeLayout)m_map.getParent();
	    	rl.addView(m_last);
			ImageView pointer = (ImageView)rl.findViewById(R.id.rule_last_point_image);
			pointer.setOnClickListener(this);
		}
		m_last.setLonLat(point);
		invalidate();
	}

	@Override
    protected void onDraw(Canvas canvas)
    {
		double result = 0;
        for(int i = 0; i < m_curve.size() - 1; i++)
        {
        	Point current = m_map.MapToScreenTempo( m_curve.get(i));
        	Point next = m_map.MapToScreenTempo( m_curve.get(i+1));
        	canvas.drawLine(current.x, current.y, next.x, next.y, m_paint);
        	result += MapUtils.GetDistanceBetween(m_curve.get(i), m_curve.get(i + 1));
        }

        if(m_last != null)
        {
        	m_last.setText(GetLengthText(result));
        }
    }
	public static String GetLengthText(double length)
	{
		double len = length;
		String form = "%.2f m";
		if(len < 10)
		{
			form = "%.2f m";
		}
		else if(len < 100)
		{
			form = "%.1f m";
		}
		else if(len < 1000)
		{
			form = "%.0f m";
		}
		else if(len < 10000)
		{
			form = "%.3f km";
			len = len/1000;
		}
		else if(len < 100000)
		{
			form = "%.2f km";
			len = len/1000;
		}
		else if(len < 1000000)
		{
			form = "%.1f km";
			len = len/1000;
		}
		else if(len >= 1000000)
		{
			form = "%.0f km";
			len = len/1000;
		}
		return String.format(form, len);


	}

	public void onClick(View v)
	{
		if(v.getId() == R.id.rule_last_point_image)
		{
			RemoveLastPoint();
		}

	}

	public GIMap Map() {
		return m_map;
	}

	public void setMap(GIMap map)
	{
		m_map = map;
		map.registerGIControl(this);
	}

	public void onMapMove() 
	{

		
	}


	public void onViewMove() {
		invalidate();
	}


	public void afterMapFullRedraw(GIBounds bounds, Rect view_rect) {
	}


	public void afterMapImageRedraw(GIBounds bounds, Rect view_rect) {
	}


	public void onMarkerLayerRedraw(Rect view_rect) {
	}


	public void afterViewRedraw() {
	}
	

}
