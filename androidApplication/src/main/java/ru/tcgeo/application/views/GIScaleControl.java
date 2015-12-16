package ru.tcgeo.application.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.GIControl;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.GIMap;

public class GIScaleControl extends RelativeLayout implements GIControl
{
	int[] m_nominals = {20, 50, 100, 250, 500, 1000, 2000, 3000, 5000, 10000, 20000, 50000, 100000, 500000, 1000000, 5000000, 10000000};
	GIMap m_map;
	public TextView m_scale_info_text;
	public TextView m_lon_info_text;
	public TextView m_lat_info_text;

	public GIScaleControl(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.scale_control, this);
		initControl();
	}
	
	public GIScaleControl(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.scale_control, this);
		initControl();
	}	
	
	public GIScaleControl(Context context) 
	{
		super(context);
		((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.scale_control, this);
		initControl();
	}	
    
    public String getCoordString(double coord)
    {
    	int degrees = (int)Math.floor(coord);//º   ° ctrl+shift+u +code +space
    	int mins = (int)Math.floor((coord - degrees)*60); //
    	double secs = ((coord - degrees)*60-mins)*60;
    	return String.format("%d° %d\' %.4f\"", degrees, mins, secs);
    }
    
	private void initControl()
	{
		m_scale_info_text = (TextView)findViewById(R.id.scale_info_);
		m_lon_info_text = (TextView)findViewById(R.id.lontitude_info_text);
		m_lat_info_text = (TextView)findViewById(R.id.latitude_info_text);		
	}
	public void SetScale()
	{
		//double res = m_map.GetGeometryLength();
		double pixelWidth = m_map.MetersInPixel(); 
		int nearest = 10000;
		for(int i = 0; i < m_nominals.length; i++)
		{
			if((m_nominals[i]>=180*pixelWidth)&& i > 0)
			{
				nearest =m_nominals[i-1];
				break;
			}
		}
		long lenght =  Math.round(nearest/pixelWidth);
		
		String out;
		if(nearest <= 1000)
		{
			out = String.format("% d m", nearest);
		}
		else
		{
			out = String.format("% d km", nearest/1000);
		}
		m_scale_info_text.setText(out);
		
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.scale_control_root);
		ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)rl.getLayoutParams();
		params.width = (int) lenght;
		rl.setLayoutParams(params);
		/**/
		//double dist = m_map.getDistance(new Point((int)lenght, 0));
		GILonLat center = (m_map.Center());
		m_lon_info_text.setText(getCoordString(m_map.MetersToDegrees(center).lon()));
		m_lat_info_text.setText(getCoordString(m_map.MetersToDegrees(center).lat()));
	}
	
	public GIMap Map ()
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
		SetScale();
	}
	public void onViewMove()
	{
		SetScale();
	}
	public void afterMapFullRedraw(GIBounds bounds, Rect view_rect)
	{
		SetScale();
	}
	public void afterMapImageRedraw(GIBounds bounds, Rect view_rect)
	{
		SetScale();
	}
	public void onMarkerLayerRedraw(Rect view_rect){}
	public void afterViewRedraw(){}

}
