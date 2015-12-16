package ru.tcgeo.application.gilib;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.widget.RelativeLayout;

import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.utils.MapUtils;

public class GISquareToolControl extends GIRuleToolControl {

	Paint paint_fill;
	Paint paint_stroke;
	Path path;
	protected GISquareToolControl(Context context, GIMap map)
	{
		super(context, map);
        paint_fill = new Paint();
        path = new Path();
        paint_fill.setARGB(50, 15, 177, 41);
        //paint_fill.setAntiAlias(true);
        paint_fill.setStyle(Paint.Style.FILL);

        paint_stroke = new Paint();
        paint_stroke.setARGB(150, 15, 177, 41);
        paint_stroke.setAntiAlias(true);
        paint_stroke.setStyle(Paint.Style.STROKE);
        paint_stroke.setStrokeWidth(2);
	}
	private static GISquareToolControl instance;
	public static GISquareToolControl Instance(Context context, GIMap map)
	{
		if(instance == null)
		{
			instance = new GISquareToolControl(context, map);
		}
		return instance ;
	}

	public void Disable()
	{
		super.Disable();
    	instance = null;
	}

	@Override
    protected void onDraw(Canvas canvas)
    {
        /*Paint paint_fill = new Paint();
        Path path = new Path();
        paint_fill.setARGB(50, 15, 177, 41);
        //paint_fill.setAntiAlias(true);
        paint_fill.setStyle(Paint.Style.FILL);

        Paint paint_stroke = new Paint();
        paint_stroke.setARGB(150, 15, 177, 41);
        paint_stroke.setAntiAlias(true);
        paint_stroke.setStyle(Paint.Style.STROKE);
        paint_stroke.setStrokeWidth(2);*/
		path.reset();
        double result = 0;
        if( m_curve.size() > 1)
        {
	        path.moveTo( m_map.MapToScreenTempo( m_curve.get(0)).x, m_map.MapToScreenTempo( m_curve.get(0)).y);
	        //canvas.drawLine(m_map.MapToScreenTempo( m_curve.get(0)).x, m_map.MapToScreenTempo( m_curve.get(0)).y, m_map.MapToScreenTempo( m_curve.get(1)).x, m_map.MapToScreenTempo( m_curve.get(1)).y);
        	for(int i = 1; i < m_curve.size(); i++)
	        {
	        	Point current = m_map.MapToScreenTempo( m_curve.get(i));
	        	Point prev = m_map.MapToScreenTempo( m_curve.get(i-1));
	        	canvas.drawLine(prev.x, prev.y, current.x, current.y, paint_stroke);
	        	path.lineTo(current.x, current.y);

	        	//result += m_map.GetDistanceBetween( m_curve.get(i-1),  m_curve.get(i));
	        }
	        canvas.drawLine(m_map.MapToScreenTempo( m_curve.get(0)).x, m_map.MapToScreenTempo( m_curve.get(0)).y, m_map.MapToScreenTempo(m_curve.get(m_curve.size()-1)).x, m_map.MapToScreenTempo(m_curve.get(m_curve.size()-1)).y, paint_stroke);
        	canvas.drawPath(path, paint_fill);

        }
        if(m_last != null)
        {
        	result = Math.abs(getSquareSing(m_curve));
        	m_last.setText(GetSquareText(result));
        }
    }
	public double getSquareSing(ArrayList<GILonLat> curve)
	{
		if(curve.size() > 2)
		{
			double square_geron = 0;
			//double square_merkator = 0;
			for(int i = 2; i < curve.size(); i++)
			{
				GILonLat point_a = GIProjection.ReprojectLonLat(curve.get(0), GIProjection.WGS84(), GIProjection.WorldMercator());
				GILonLat point_b = GIProjection.ReprojectLonLat(curve.get(i-1), GIProjection.WGS84(), GIProjection.WorldMercator());
				GILonLat point_c = GIProjection.ReprojectLonLat(curve.get(i), GIProjection.WGS84(), GIProjection.WorldMercator());

				double triangle = ((point_b.lon() - point_a.lon())*(point_c.lat() - point_a.lat())
						- (point_b.lat() - point_a.lat())*(point_c.lon() - point_a.lon()))/2;
				//square_merkator += triangle;
				int sign = 0;
				if(triangle > 0)
				{
					sign = 1;
				}
				else 
				{
					sign = -1;
				}
				double dist_a = MapUtils.GetDistanceBetween(curve.get(0), curve.get(i - 1));
				double dist_b = MapUtils.GetDistanceBetween(curve.get(0), curve.get(i));
				double dist_c = MapUtils.GetDistanceBetween(curve.get(i), curve.get(i-1));
				
				//--- for high ranges ---- 
				//- commented for velocity
				/*
				double angle_a = m_map.GetAngle_A_OfTriangle(curve.get(0), curve.get(i-1), curve.get(i));
				double angle_b = m_map.GetAngle_A_OfTriangle(curve.get(i-1), curve.get(i), curve.get(0));
				double angle_c = m_map.GetAngle_A_OfTriangle(curve.get(i), curve.get(0), curve.get(i-1));
				
				double summ = angle_a + angle_b + angle_c - Math.PI;
				
				double square_sphere = Math.pow(6372795.0, 2)*summ;
				
				double deg__a = Math.toDegrees(angle_a);
				*/
				double perimetr = (dist_a + dist_b + dist_c)/2;
				double triangle_geron = Math.sqrt(perimetr*(perimetr - dist_a)*(perimetr - dist_b)*(perimetr - dist_c));
				
				square_geron += triangle_geron*sign;
			}
			//square_merkator
			//double diff = Math.abs(square_merkator - square_geron);
			return square_geron;
				
		}
		return 0;
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
	}
	
	protected String GetSquareText(double length)
	{
		//º   ° ctrl+shift+u +code +space² ა ₂ ₂
		double len = length;
		String form = "%.2f m²";
		if(len < 10)
		{
			form = "%.2f m²";
		}
		else if(len < 100)
		{
			form = "%.1f m²";
		}
		else if(len < 1000)
		{
			form = "%.0f m²";
		}
		else if(len < 10000)
		{
			form = "%.0f m²";
			//len = len;
		}
		else if(len < 100000)
		{
			form = "%.0f m²";
			//len = len/1000000;
		}
		else if(len < 1000000)
		{
			form = "%.2f km²";
			len = len/1000000;
		}
		else if(len < 10000000)
		{
			form = "%.1f km²";
			len = len/1000000;
		}
		/*else if(len < 100000000)
		{
			form = "%.1f km²";
			len = len/1000000;
		}*/
		
		else if(len >= 10000000)
		{
			form = "%.0f km²";
			len = len/1000000;
		}
		return String.format(form, len);


	}

	

}
