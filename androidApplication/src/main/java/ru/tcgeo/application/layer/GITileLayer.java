package ru.tcgeo.application.layer;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.GILayer;
import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GICustomTile;
import ru.tcgeo.application.gilib.models.GIProjection;
import ru.tcgeo.application.layer.renderer.GITileRenderer;
public class GITileLayer extends GILayer {

	public ArrayList<GICustomTile> m_tiles;
	
	public GITileLayer(String path) 
	{
		type_ = GILayerType.TILE_LAYER;
		m_renderer = new GITileRenderer();
		m_tiles = new ArrayList<GICustomTile>();
//		m_id = initTileLayer(path);
//		m_projection = new GIProjection(getTileProjection(m_id), true);
		m_projection = GIProjection.WGS84();
	}
	@Override
	public void Redraw(GIBounds area, Bitmap bitmap, Integer opacity, double scale)
	{
		synchronized(this)
		{
			m_renderer.RenderImage(this, area, opacity, bitmap, scale);
		}

	}


	public void add(GICustomTile tile)
	{
		m_tiles.add(tile);
	}
}
