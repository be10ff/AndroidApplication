package ru.tcgeo.application.gilib;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;

public class GIControlInfoInPoint extends LinearLayout implements GIControl, OnClickListener
{
	private GIMap m_map;
	private GILonLat m_PointOriginMap;
	private View m_LayoutView;
	public boolean hasClosed;
	private Context m_context;
	private String m_info_text;

	private static GIControlInfoInPoint instance;

	public static GIControlInfoInPoint Instance(Context context, GIMap map, GILonLat lonlat, String text, String caption)
	{
		if(instance == null)
		{
			instance = new GIControlInfoInPoint(context, map, lonlat, text, caption);
		}
		else
		{
			instance.setMap(map);
			instance.hasClosed = false;
			instance.setText(text);
			instance.setCaption(caption);
			instance.setLonLat(lonlat);
			instance.onMapMove();
		}
		return instance ;
	}

	private GIControlInfoInPoint (Context context, GIMap map, GILonLat lonlat, String text, String caption)
	{
		super(context);
		setMap(map);
		m_context = context;
		LayoutInflater m_LayoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_LayoutView = m_LayoutInflater.inflate(R.layout.info_control_nine_patch, this);
		Button btn = (Button)findViewById(R.id.button_close_info);
		btn.setOnClickListener(this);
		hasClosed = false;
		setText(text);
		setCaption(caption);
		setLonLat(lonlat);
		//TextView tv = (TextView)findViewById(R.id.control_info_text);
		//tv.setOnClickListener(this);
		onMapMove();
		RelativeLayout rl = (RelativeLayout)m_map.getParent();
    	rl.addView(this);
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
	public void onMapMove()
	{
		MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
	}
	public void onViewMove()
	{
		MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
	}
	public void afterMapFullRedraw(GIBounds bounds, Rect view_rect)
	{
		Visibility();
		MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));		
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
	private void MoveTo(Point point)
	{
		int[] offset = {13, 13};
		Visibility();
		if(getVisibility() != View.VISIBLE)
		{return;}
        DisplayMetrics dm = m_context.getResources().getDisplayMetrics();
        	Point show_point = point;
			Point corrected_point = new Point(0, 0);
			int width = getWidth();
			int height = getHeight();
			if(show_point.x > dm.widthPixels - width + 120)
			{
				if(show_point.y > dm.heightPixels - height)
				{
					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_bottom_right_small_nine);
					corrected_point.x = show_point.x - width + offset[0];
					corrected_point.y = show_point.y - height + offset[1];				
				}
				else
				{
					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_top_right_small_nine);
					corrected_point.x = show_point.x - width + offset[0];
					corrected_point.y = show_point.y - offset[1];				
				}
			}
			else
			{
				if(show_point.y > dm.heightPixels - height)
				{
					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_bottom_left_small_nine);
					corrected_point.x = show_point.x  - offset[0];
					corrected_point.y = show_point.y - height  + offset[1];				
				}
				else
				{
					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_top_left_small_nine);
					corrected_point.x = show_point.x - offset[0];
					corrected_point.y = show_point.y - offset[1];
				}
			}				
	
			setX(corrected_point.x);
	        setY(corrected_point.y);
	        invalidate();
	}
//	private void MoveTo(Point point)
//	{
//		Visibility();
//		if(getVisibility() != View.VISIBLE)
//		{return;}
//        DisplayMetrics dm = m_context.getResources().getDisplayMetrics();
//        	Point show_point = point;
//			Point corrected_point = new Point(0, 0);
//			int width = getWidth();
//			int height = getHeight();
//			if(show_point.x > dm.widthPixels - width)
//			{
//				if(show_point.y > dm.heightPixels - height)
//				{
//					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_bottom_right_nine);
//					corrected_point.x = show_point.x - width +16;
//					corrected_point.y = show_point.y - height + 28;				
//				}
//				else
//				{
//					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_top_right_nine);
//					corrected_point.x = show_point.x - width +16 ;
//					corrected_point.y = show_point.y -28;				
//				}
//			}
//			else
//			{
//				if(show_point.y > dm.heightPixels - height)
//				{
//					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_bottom_left_nine);
//					corrected_point.x = show_point.x - 16;
//					corrected_point.y = show_point.y - height + 28;				
//				}
//				else
//				{
//					m_LayoutView.setBackgroundResource(R.drawable.point_info_panel_top_left_nine);
//					corrected_point.x = show_point.x - 16;
//					corrected_point.y = show_point.y - 28;
//				}
//			}
//			corrected_point.y += 67 + 5;
//			corrected_point.x += 5;		
//			setX(corrected_point.x);
//	        setY(corrected_point.y);
//	        invalidate();
//	}
	public void setText(String info)
	{
		m_info_text = info;
       	TextView tv = (TextView)findViewById(R.id.control_info_text);
		tv.setText(m_info_text);
	}
	public void setCaption(String info)
	{
       	//View v = findViewById(R.id.control_info_coords);
       	//v.setT
		TextView tv = (TextView)findViewById(R.id.control_info_coords);
		tv.setText(info);
	}	
	public void setLonLat(GILonLat lonlat)
	{
		m_PointOriginMap = m_map.MetersToDegrees(lonlat);
	}
	public void Close()
	{
		setVisibility(View.GONE);
    	hasClosed = true;
    	m_map.unRegisterGIControl(this);
    	RelativeLayout rl = (RelativeLayout)m_map.getParent();
    	rl.removeView(this);
    	instance = null;
	}
	public void onClick(View v) 
	{
    	if(v.getId() == R.id.button_close_info)
    	{
    		Close();
    	}
    	/*if(v.getId() == R.id.control_info_text)
    	{
			this.bringToFront();
    	}*/
    		
	}
}
