package ru.tcgeo.application.data.gilib.layer.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import ru.tcgeo.application.data.GIMap;
import ru.tcgeo.application.data.gilib.layer.GIEditableLayer;
import ru.tcgeo.application.data.gilib.layer.GILayer;
import ru.tcgeo.application.data.gilib.models.GIBounds;
import ru.tcgeo.application.data.gilib.models.GIStyle;
import ru.tcgeo.application.data.gilib.models.GIVectorStyle;
import ru.tcgeo.application.data.wkt.GI_WktGeometry;


public class GIEditableRenderer extends GIRenderer {

	public GIVectorStyle m_style;
	public ArrayList<GIVectorStyle> m_additional_styles;

	public GIEditableRenderer(GIVectorStyle style)
	{
		m_style = style;
		m_additional_styles = new ArrayList<GIVectorStyle>();
	}


	@Override
	public void RenderImage(GILayer _layer, GIBounds area, int opacity, Bitmap bitmap, double scale)
	{
		Canvas m_canvas = new Canvas(bitmap);
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
				geom.Draw(m_canvas, area, scale_factor, layer.getStyle());
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
