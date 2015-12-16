package ru.tcgeo.application.gilib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.wkt.GI_WktPoint;

public class GIControlFloating extends LinearLayout implements GIControl
{
	private GIMap m_map;
	private RelativeLayout m_root;
	boolean hasClosed;
	private GILonLat m_PointOriginMap;
	public GI_WktPoint m_WKTPoint;
	int[] map_location = { 0, 0 };
	TextView tv;
	ToggleButton button;

	int [] m_offset= { 0, 0 };

	public GIControlFloating (Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public GIControlFloating(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public boolean getChecked()
	{
		if(button != null)
		{
			return button.isChecked();
		}
		return false;
	}

	public void setChecked(boolean checked)
	{
		if(button != null)
		{
			button.setChecked(checked);
		}
	}

	public GIControlFloating (Context context)
	{
		super(context);
		//m_context = context;
		//LayoutInflater m_LayoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, this);
		//View m_LayoutView = m_LayoutInflater.inflate(R.layout.marker, this);
		tv = (TextView)findViewById(R.id.text_marker);
		button = (ToggleButton)findViewById(R.id.point_image);
		Bitmap m_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon);
		//int [] m_offset_ = new int[] {m_bitmap.getWidth()/2, m_bitmap.getHeight()/2};

		m_offset[0] = 15*m_bitmap.getWidth()/58 ;
		m_offset[1] = 69*m_bitmap.getHeight()/89;
	}

	public GIControlFloating (Context context, int layout, int text_view, int[] offset)
	{
		super(context);
		//m_context = context;
		//LayoutInflater m_LayoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//View m_LayoutView = m_LayoutInflater.inflate(layout, this);
		//   ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, this);
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, this);
		tv = (TextView)findViewById(text_view);
		m_offset = offset;
	}
	public void setRoot(RelativeLayout root)
	{
		m_root = root;
		m_root.addView(this);
	}
	public void Remove()
	{
		if(m_root != null)
		{
			m_root.removeView(this);
			this.setVisibility(View.GONE);
		}
	}

	public GIMap Map()
	{
		return m_map;
	}
	public void setMap(GIMap map)
	{
		m_map = map;
		map.registerGIControl(this);
//    	m_map.getLocationOnScreen(map_location);
//		map_location[0] -= m_offset[0];
//		map_location[1] -= m_offset[1];		

//		map_location[0] = -m_offset[0];
//		map_location[1] = -m_offset[1];	
	}
	public void onMapMove()
	{
		//m_PointOriginScreen = m_map.MapToScreen(m_PointOriginMap);
		MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
	}
	public void onViewMove()
	{
		MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
	}
	public void afterMapFullRedraw(GIBounds bounds, Rect view_rect)
	{
		//MoveTo(m_map.MapToScreen(m_PointOriginMap));		
	}
	public void afterMapImageRedraw(GIBounds bounds, Rect view_rect)
	{

	}
	public void onMarkerLayerRedraw(Rect view_rect){}
	public void afterViewRedraw(){}
	private void Visibility()
	{
		if(hasClosed)
		{return;}
        if(m_map.m_view.contains(m_map.MapToScreenTempo(m_PointOriginMap).x, m_map.MapToScreenTempo(m_PointOriginMap).y))
        {
        	this.setVisibility(View.VISIBLE);
        }
        else
        {
        	this.setVisibility(View.GONE);
        }
	}

	public void MoveTo(Point point)
	{
		Visibility();
		if(getVisibility() != View.VISIBLE)
		{return;}
		
//    	m_map.getLocationOnScreen(map_location);
//		map_location[0] -= m_offset[0];
//		map_location[1] -= m_offset[1];	

		setX(point.x + map_location[0] -  m_offset[0]);
        setY(point.y + map_location[1] -  m_offset[1]);
        invalidate();
	}
	public void setLonLat(GILonLat lonlat)
	{
		m_PointOriginMap = lonlat;
		onViewMove();
	}
	public void setText(String text)
	{
		tv.setText(text);
	}
//	public void setWKTPoint(GI_WktPoint point)
//	{
//		m_WKTPoint = point;
//		m_PointOriginMap = new GILonLat(point.m_lon, point.m_lat);
//		onViewMove();
//	}
}
