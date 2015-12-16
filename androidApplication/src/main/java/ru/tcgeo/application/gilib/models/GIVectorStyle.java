package ru.tcgeo.application.gilib.models;

import android.graphics.Bitmap;
import android.graphics.Paint;


public class GIVectorStyle implements GIStyle
{
	public Paint m_paint_pen;
	public Paint m_paint_brush;
    int m_opacity;
	Bitmap m_image;

    public GIVectorStyle ()
    {
    }

    public GIVectorStyle (Bitmap image)
    {
    	m_image = image;
    }

	public GIVectorStyle (Paint paint_pen, Paint paint_brush, int opacity)
    {
	    m_paint_pen = paint_pen;
	    m_paint_brush = paint_brush;
	    m_opacity = opacity;
    }

	public GIVectorStyle (Paint paint, int opacity)
	{
	    m_paint_pen = paint;
	    m_opacity = opacity;
	}

	public GIVectorStyle (GIVectorStyle style)
	{
		m_paint_pen = style.m_paint_pen;
	    m_opacity = style.m_opacity;
	    m_image = style.m_image;
	}

}
