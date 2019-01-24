package ru.tcgeo.application.data.gilib.layer;

import android.graphics.Bitmap;

import ru.tcgeo.application.data.gilib.layer.renderer.GIYandexRenderer;
import ru.tcgeo.application.data.gilib.models.GIBounds;
import ru.tcgeo.application.data.gilib.models.GIProjection;

public class GIYandexLayer extends GILayer {

	String m_site;
	public GIYandexLayer() 
	{
		m_site = "http://vec01.maps.yandex.net/tiles";
        type = GILayerType.ON_LINE;
        m_renderer = new GIYandexRenderer();
        m_projection = GIProjection.WGS84();
    }

    public GIYandexLayer(String path) {
		m_site = "http://vec01.maps.yandex.net/tiles";
        type = GILayerType.ON_LINE;
        m_renderer = new GIYandexRenderer();
        m_projection = GIProjection.WGS84();
    }

    @Override
    public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity,
					   double scale)
	{
		synchronized(this)
		{
			m_renderer.RenderImage(this, area, opacity, bitmap, scale);
		}
	}

}
