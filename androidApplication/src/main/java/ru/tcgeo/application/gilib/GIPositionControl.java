package ru.tcgeo.application.gilib;

/**
 * текущее положение и направление
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.widget.RelativeLayout;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;

public class GIPositionControl extends View implements GIControl //View
{

	private GIMap m_map;
	private RelativeLayout m_root;
	boolean hasClosed;
	private GILonLat m_CurrentPosition;
	private GILonLat m_OriginPosition;
	private Context m_context;
	int[] map_location = { 0, 0 };
	Bitmap image;
	Matrix matrix;

	Point current_pos_on_screen;
	//todo ????
	//View m_LayoutView;
	public GIPositionControl()
	{
		super(GIEditLayersKeeper.Instance().getMap().getContext());
		m_context = GIEditLayersKeeper.Instance().getMap().getContext();
		//LayoutInflater m_LayoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//m_LayoutView = m_LayoutInflater.inflate(R.layout.position_marker, null);
		m_root = (RelativeLayout) GIEditLayersKeeper.Instance().getMap().getParent();
		m_root.addView(this);
    	bringToFront();
		image = BitmapFactory.decodeResource(getResources(), R.drawable.position_arrow);
		setMap(GIEditLayersKeeper.Instance().getMap());
		matrix = new Matrix();

	}
	public GIPositionControl(Context context, GIMap map)
	{
		super(context);
		m_context = context;
		//LayoutInflater m_LayoutInflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//m_LayoutView = m_LayoutInflater.inflate(R.layout.position_marker, null);
		m_root = (RelativeLayout)map.getParent();
		m_root.addView(this);
    	bringToFront();
		image = BitmapFactory.decodeResource(getResources(), R.drawable.position_arrow);
		setMap(map);
		matrix = new Matrix();

	}

	public GIMap Map() {
		return m_map;
	}

	public void setMap(GIMap map) {
		m_map = map;
		map.registerGIControl(this);
		int[] screen_location = { 0, 0 };
		m_map.getLocationInWindow(screen_location);

    	m_map.getLocationOnScreen(map_location);
		map_location[0] -= image.getHeight()/2;
		map_location[1] -= image.getWidth()/2 + m_map.getOffsetY();
	}

	public void onMapMove()
	{
		if(m_CurrentPosition != null)
		{
			MoveTo(m_map.MapToScreenTempo(m_CurrentPosition));
		}

	}

	public void onViewMove()
	{
		if(m_CurrentPosition != null)
		{
			MoveTo(m_map.MapToScreenTempo(m_CurrentPosition));
		}
	}

	public void afterMapFullRedraw(GIBounds bounds, Rect view_rect) {}
	public void afterMapImageRedraw(GIBounds bounds, Rect view_rect) {}
	public void onMarkerLayerRedraw(Rect view_rect) {}
	public void afterViewRedraw() {}

	public void MoveTo(Point point)
	{
		current_pos_on_screen = point;
		setX(point.x + map_location[0]);
        setY(point.y + map_location[1]);
        invalidate();
	}
	
	public void setLonLat(GILonLat lonlat)
	{
		if(m_CurrentPosition != null)// && m_CurrentPosition != m_OriginPosition)
		{
			m_OriginPosition = m_CurrentPosition;
		}
		m_CurrentPosition = lonlat;
		onViewMove();
	}
	
	@Override
    protected void onDraw(Canvas canvas) 
	{
		double direction =  -Math.PI/2;

		if(m_OriginPosition != null)
		{
			double hypot = Math.hypot(m_CurrentPosition.lon() - m_OriginPosition.lon(), m_CurrentPosition.lat() - m_OriginPosition.lat());
			if(hypot != 0)
			{
				double dir_cos = (m_CurrentPosition.lon() - m_OriginPosition.lon())/hypot;
				double dir_sin = (m_CurrentPosition.lat() - m_OriginPosition.lat());
				direction = Math.acos(dir_cos);
				if(dir_sin > 0)
				{
					direction = -direction;
				}
			}
		}
		/*
		Paint paint = new Paint();
		paint.setARGB(128, 128, 128, 128);
		paint.setStyle(Style.FILL);
		canvas.drawCircle(current_pos_on_screen.x, current_pos_on_screen.y, 100, paint);
		
		*/
		direction = Math.toDegrees(direction);
		matrix.reset();
		//matrix.setTranslate(m_accurancy/2, m_accurancy/2);
		matrix.setRotate((float)direction, image.getWidth()/2, image.getHeight()/2);
		canvas.drawBitmap(image, matrix, null);
	}
}
