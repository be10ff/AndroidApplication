package ru.tcgeo.application.gilib;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.gilib.models.GIStyle;
import ru.tcgeo.application.gilib.models.GIVectorStyle;
import ru.tcgeo.application.wkt.GIWKTParser;
import ru.tcgeo.application.wkt.GI_WktGeometry;


public class GIEditableRenderer extends GIRenderer {

	//TODO
	//Canvas m_canvas;
	public GIVectorStyle m_style;
	public ArrayList<GIVectorStyle> m_additional_styles;
	
	public GIEditableRenderer(GIVectorStyle style) 
	{
		//m_canvas = new Canvas();
		m_style = style;
		m_additional_styles = new ArrayList<GIVectorStyle>();
	}


	public void RenderImageFromDBase(GILayer _layer, GIBounds area, int opacity, Bitmap bitmap, double scale)
	{
		Canvas m_canvas = new Canvas(bitmap);
		area = area.Reprojected(GIProjection.WGS84());
		double _scale = GIMap.getScale(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
		if(_scale == 0){return;}
		float scale_factor = (float) (scale/_scale);
		GIEditableSQLiteLayer layer = (GIEditableSQLiteLayer)_layer;

		SQLiteDatabase db;
		Cursor c;
		try
		{
			db = SQLiteDatabase.openDatabase(layer.m_path, null, SQLiteDatabase.OPEN_READWRITE);
			String sql_string = String.format("SELECT Geometry FROM Layer");
			c = db.rawQuery(sql_string, null);
		    if (c.moveToFirst())
		    {
		    	while ( !c.isAfterLast() )
		        {
		            String wkt = c.getString(0);
		    		GI_WktGeometry geom = GIWKTParser.CreateGeometryFromWKT(wkt);
					switch(layer.m_Status)
					{
						/*case UNEDITED:
						{
							layer.m_Status = GIEditableLayerStatus.EDITED;
							break;
						}*/
						case EDITED:
						{
			    			if( m_additional_styles.size() > 0)
			    			{
								geom.Draw(m_canvas, area, scale_factor, m_additional_styles.get(0).m_paint_pen);
				    			geom.Draw(m_canvas, area, scale_factor, m_additional_styles.get(0).m_paint_brush);
			    			}
							break;
						}
						case UNSAVED:
						{
			    			if( m_additional_styles.size() > 0)
			    			{
								geom.Draw(m_canvas, area, scale_factor, m_additional_styles.get(0).m_paint_pen);
				    			geom.Draw(m_canvas, area, scale_factor, m_additional_styles.get(0).m_paint_brush);
			    			}
							break;
						}
						default: case UNEDITED:
						{
			    			geom.Draw(m_canvas, area, scale_factor, layer.m_style.m_paint_pen);
			    			geom.Draw(m_canvas, area, scale_factor, layer.m_style.m_paint_brush);
			    			break;
						}

					}

		            c.moveToNext();
		        }
		    }
	        c.close();
	        db.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
	}

	@Override
	public void RenderImage(GILayer _layer, GIBounds area, int opacity, Bitmap bitmap, double scale)
	{
		Canvas m_canvas = new Canvas(bitmap);
		//area = area.Reprojected(GIProjection.WGS84());
		double _scale = GIMap.getScale(area, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
		if(_scale == 0){return;}
		float scale_factor = (float) (scale/_scale);
		//TODO
		if(scale_factor != 1)
		{
			Log.d("LOG_TAG", "skipped");
			return;
		}
		GIEditableLayer layer = (GIEditableLayer)_layer;
		try
		{
    		for(GI_WktGeometry geom : layer.m_shapes)
	        {
    			geom.Draw(m_canvas, area, scale_factor, layer.m_style.m_paint_pen);
    			geom.Draw(m_canvas, area, scale_factor, layer.m_style.m_paint_brush);
	        }
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap, double scale) 
	{
	}

	@Override
	public void RenderText(GILayer layer, GIBounds area, Bitmap bitmap, float scale_factor, double scale) 
	{
	}

	@Override
	public void AddStyle(GIStyle style)
	{
		m_additional_styles.add((GIVectorStyle) style);
	}

	@Override
	public int getType(GILayer layer) 
	{
		return 0;
	}

}
