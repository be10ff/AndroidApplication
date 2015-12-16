package ru.tcgeo.application.gilib;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.wkt.GI_WktPoint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class GIGeometryPointControl extends LinearLayout implements GIControl, OnClickListener, OnLongClickListener {

	private GIMap m_map;
	private RelativeLayout m_root;
	boolean hasClosed;
	private GILonLat m_PointOriginMap;
	public GI_WktPoint m_WKTPoint;
	private Context m_context;
	int[] map_location = { 0, 0 };
	ToggleButton m_button;
	View m_LayoutView;
	int[] m_offset;

	public boolean getChecked()
	{
		if(m_button != null)
		{
			return m_button.isChecked();
		}
		return false;
	}

	public void setChecked(boolean checked)
	{
		if(m_button != null)
		{
			m_button.setChecked(checked);
		}
	}


	public GIGeometryPointControl (Context context, GIMap map)
	{
		super(context);
		//setMap(map);
		m_context = context;
		Bitmap m_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.edit_point); //measure_point
		LayoutInflater m_LayoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_LayoutView = m_LayoutInflater.inflate(R.layout.geometry_editing_point_control, this);
		m_button = (ToggleButton)m_LayoutView.findViewById(R.id.point_image);
		m_button.setBackgroundDrawable(m_context.getResources().getDrawable(R.drawable.edit_point)); //measure_point
		m_offset = new int[] {m_bitmap.getWidth()/2, m_bitmap.getHeight()/2};
		m_button.setEnabled(false);
		m_button.setClickable(false);
		setEnabled(false);
		setClickable(false);
		//m_button.setEnabled(false);
		setMap(map);
		//m_button.setOnClickListener(this);
	}

	public void setActiveStatus(boolean active)
	{
		//TODO something wrong with removing listeners... may be
		if(active)
		{
//			Bitmap m_bitmap = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.unselected_point_large);
//			m_offset = new int[] {m_bitmap.getWidth()/2, m_bitmap.getHeight()/2};
			m_button.setBackgroundDrawable(m_context.getResources().getDrawable(R.drawable.point_selection_status));
			m_button.setOnClickListener(this);
			m_button.setOnLongClickListener(this);
			m_button.setEnabled(true);
			m_button.setClickable(true);
			setEnabled(true);
			setClickable(true);
//			m_map.getLocationOnScreen(map_location);
//			map_location[0] -= m_offset[0];
//			map_location[1] -= m_offset[1];
			MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
			invalidate();
		}
		else
		{
//			Bitmap m_bitmap = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.edit_point);
//			m_offset = new int[] {m_bitmap.getWidth()/2, m_bitmap.getHeight()/2};
			m_button.setBackgroundDrawable(m_context.getResources().getDrawable(R.drawable.edit_point)); //measure_point
			m_button.setOnClickListener(null);
			//m_button.setOnLongClickListener(null);
			m_button.setEnabled(false);
			m_button.setClickable(false);
			setEnabled(false);
			setClickable(false);
//			m_map.getLocationOnScreen(map_location);
//			map_location[0] -= m_offset[0];
//			map_location[1] -= m_offset[1];
			MoveTo(m_map.MapToScreenTempo(m_PointOriginMap));
			invalidate();
		}
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
    	//m_map.getLocationOnScreen(map_location);
//		map_location[0] -= m_offset[0];
//		map_location[1] -= m_offset[1];
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
		setX(point.x + map_location[0] - m_offset[0]);
        setY(point.y + map_location[1] - m_offset[1]);
        invalidate();
	}
	public void setLonLat(GILonLat lonlat)
	{
		m_PointOriginMap = lonlat;
		onViewMove();
	}

	public void setWKTPoint(GI_WktPoint point)
	{
		m_WKTPoint = point;
		m_PointOriginMap = new GILonLat(point.m_lon, point.m_lat);
		m_WKTPoint.Set(m_PointOriginMap);
		onViewMove();
	}

	public void onClick(View v)
	{
		GIEditLayersKeeper.Instance().onSelectPoint(this);
	}

	public boolean onLongClick(View v) 
	{
		GIEditLayersKeeper.Instance().onLongClickPoint(this);
		return true;
	}

}
