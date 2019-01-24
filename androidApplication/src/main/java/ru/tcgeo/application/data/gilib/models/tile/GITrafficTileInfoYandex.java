package ru.tcgeo.application.data.gilib.models.tile;

import android.graphics.Bitmap;

public class GITrafficTileInfoYandex extends GITileInfoYandex
{
	public static final int REUSE = 20;
	public long m_TimeStamp;
	public int m_used_at_last_time;
    Bitmap bitmap;

    public GITrafficTileInfoYandex(int z, double lon, double lat)
	{
		super(z, lon, lat);
		m_TimeStamp = System.currentTimeMillis() / 1000L;
		m_used_at_last_time = REUSE;
        bitmap = null;
    }
	public GITrafficTileInfoYandex(int z, int tile_x, int tile_y) 
	{
		super(z, tile_x, tile_y);
		m_TimeStamp = System.currentTimeMillis() / 1000L;
		m_used_at_last_time = REUSE;
        bitmap = null;
    }
	public GITrafficTileInfoYandex(int z, int tile_x, int tile_y, Bitmap bitmap) 
	{
		super(z, tile_x, tile_y);
		m_TimeStamp = System.currentTimeMillis() / 1000L;
		m_used_at_last_time = REUSE;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
