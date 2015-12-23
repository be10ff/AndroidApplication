package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;

import ru.tcgeo.application.gilib.GITileInfoYandex;

public class GITrafficTileInfoYandex extends GITileInfoYandex
{
	public static final int REUSE = 20;
	public long m_TimeStamp;
	public int m_used_at_last_time;
	Bitmap m_bitmap;
	public GITrafficTileInfoYandex(int z, double lon, double lat) 
	{
		super(z, lon, lat);
		m_TimeStamp = System.currentTimeMillis() / 1000L;
		m_used_at_last_time = REUSE;
		m_bitmap = null;
	}
	public GITrafficTileInfoYandex(int z, int tile_x, int tile_y) 
	{
		super(z, tile_x, tile_y);
		m_TimeStamp = System.currentTimeMillis() / 1000L;
		m_used_at_last_time = REUSE;
		m_bitmap = null;
	}
	public GITrafficTileInfoYandex(int z, int tile_x, int tile_y, Bitmap bitmap) 
	{
		super(z, tile_x, tile_y);
		m_TimeStamp = System.currentTimeMillis() / 1000L;
		m_used_at_last_time = REUSE;
		m_bitmap = bitmap;
	}


}
