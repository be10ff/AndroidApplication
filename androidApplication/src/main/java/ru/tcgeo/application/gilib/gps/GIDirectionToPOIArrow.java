package ru.tcgeo.application.gilib.gps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.GIControl;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.GIRuleToolControl;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.Marker;
import ru.tcgeo.application.utils.MapUtils;

/**
 * направление на точку на карте
 */

public class GIDirectionToPOIArrow  extends View implements GIControl
{
    public Marker marker;
    GIMap m_map;
	Bitmap image;
	Matrix matrix;
	int size = 50;
	int length = 6;
	int[] map_location = { 0, 0 };
	GILonLat m_lon_lat_poi;
	Path path;
	Paint paint_fill;
	Rect bounds;

    public GIDirectionToPOIArrow(ViewGroup root, GIMap map, Marker marker) {
        super(root.getContext());
        this.marker = marker;
        this.m_map = map;
        m_map.getLocationOnScreen(map_location);
		this.setX(map_location[0]);
        map_location[1] += m_map.getOffsetY();
        this.setY(map_location[1]);
        m_map.registerGIControl(this);
        setTag(marker);
        setId(R.id.direction_to_point_arrow);
        root.addView(this);

    	image = BitmapFactory.decodeResource(getResources(), R.drawable.direction_arrow_new);
    	matrix = new Matrix();
    	setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        m_lon_lat_poi = new GILonLat(marker.lon, marker.lat);
        paint_fill = new Paint();
		paint_fill.setColor(Color.argb(255, 63, 255, 63));//setColor(Color.argb(255, 255, 127, 0));
		paint_fill.setStyle(Style.FILL);
        paint_fill.setTextSize(getResources().getDimension(R.dimen.direction_to_point_text_size));
        paint_fill.setShadowLayer(5, 2, 2, Color.BLACK);

		bounds = new Rect();
		path = new Path();
	}
	
	
	@Override
    protected void onDraw(Canvas canvas)
	{
		GILonLat center = GIProjection.ReprojectLonLat(m_map.Center(), m_map.Projection(), GIProjection.WGS84());
		double azimuth = MapUtils.GetAzimuth(center, m_lon_lat_poi);

		path.reset();

		String text = GIRuleToolControl.GetLengthText(MapUtils.GetDistanceBetween(center, m_lon_lat_poi));
		paint_fill.getTextBounds(text, 0, text.length() - 1, bounds);
		int offset_x = 0;
		int offset_y = bounds.height()/2;
		canvas.drawCircle(m_map.m_view.centerX()- map_location[0], m_map.m_view.centerY()- map_location[1], 2, paint_fill);
		if((azimuth > 0 &&  azimuth < 180))
		{
			path.moveTo(((int)(m_map.m_view.centerX()  + size*Math.sin(Math.toRadians(azimuth))- map_location[0])), (int)(m_map.m_view.centerY() - size*Math.cos(Math.toRadians(azimuth)))- map_location[1]);
			path.lineTo((int)(m_map.m_view.centerX() - map_location[0] + (1+length)*size*Math.sin(Math.toRadians(azimuth))), (int)(m_map.m_view.centerY() - map_location[1]- (1+length)*size*Math.cos(Math.toRadians(azimuth))));
			offset_x = (length)*size - image.getWidth() - bounds.width();
		}
		else
		{
			path.moveTo((int)(m_map.m_view.centerX() - map_location[0] + (1+length)*size*Math.sin(Math.toRadians(azimuth))), (int)(m_map.m_view.centerY() - map_location[1] - (1+length)*size*Math.cos(Math.toRadians(azimuth))));
			path.lineTo(((int)(m_map.m_view.centerX() - map_location[0] + size*Math.sin(Math.toRadians(azimuth)))), (int)(m_map.m_view.centerY() - map_location[1]- size*Math.cos(Math.toRadians(azimuth))));
			offset_x = image.getWidth();
		}
		canvas.drawTextOnPath(text, path, offset_x, offset_y, paint_fill);


        matrix.reset();
		// 90 потому как стрелка на битмапе уже повернута
		matrix.setRotate((float)(azimuth - 90), image.getWidth()/2, image.getHeight()/2);
		matrix.postTranslate((int)(m_map.m_view.centerX() + (1+length)*size*Math.sin(Math.toRadians(azimuth)) - image.getWidth()/2 - map_location[0]), (int)(m_map.m_view.centerY() - (1+length)*size*Math.cos(Math.toRadians(azimuth))- image.getHeight()/2 - map_location[1]));
		
		canvas.drawBitmap(image, matrix, null);
	}

    public void setMap(GIMap map) {
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

//	public void Disable() {
//		RelativeLayout rl = (RelativeLayout)m_map.getParent();
//		View v = rl.findViewWithTag(tag);
//		if(v != null) {
//			rl.removeView(v);
//		}
//	}

}
