package ru.tcgeo.application.gilib;


import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;

public class GITileInfoYandex
{

	public int m_zoom;
	public int m_xtile;
	public int m_ytile;
	public GIBounds m_bounds;

	/*public GITileInfoYandex(int z, double lon, double lat, boolean old)
	{
		m_zoom = z;
		m_xtile = (int)Math.floor( (lon + 180) / 360 * (1<<z) ) ;
		m_ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<z) ) ;
	    if (m_xtile < 0)
	     m_xtile=0;
	    if (m_xtile >= (1<<z))
	     m_xtile=((1<<z)-1);
	    if (m_ytile < 0)
	     m_ytile=0;
	    if (m_ytile >= (1<<z))
	     m_ytile=((1<<z)-1);

	    getBounds();
	}*/
	public GITileInfoYandex(int z, double lon, double lat)
	{
		m_zoom = z;
		getTile(new GILonLat(lon, lat), z);


	    getBounds();
	}

	public GITileInfoYandex(int z, int tile_x, int tile_y)
	{
		m_zoom = z;
		m_xtile = tile_x;
		m_ytile = tile_y;
		getBounds();
	}

	public GIBounds getBounds()
	{
		double top = tile2lat(m_ytile, m_zoom);
		double bottom = tile2lat(m_ytile + 1, m_zoom);
		double left = tile2lon(m_xtile, m_zoom);
		double right = tile2lon(m_xtile + 1, m_zoom);
		m_bounds = new GIBounds(GIProjection.WGS84(), left, top, right, bottom);
		return m_bounds;
	}

	public String getURL()
	{
		//String urlStr = "http://sat01.maps.yandex.net/tiles?l=sat&v=3.166.0&x=" + m_xtile + "&y=" + m_ytile + "&z=" + m_zoom;
		//String urlStr = "http://vec04.maps.yandex.net/tiles?l=map&v=2.39.0&x=" + m_xtile + "&y=" + m_ytile + "&z=" + m_zoom;

		long unixTime = System.currentTimeMillis() / 1000L;
		String urlStr = "http://jgo.maps.yandex.net/1.1/tiles?l=trf,trfl&lang=ru_RU&x=" + m_xtile + "&y=" + m_ytile + "&z=" + m_zoom + "&tm=" + unixTime;
		return urlStr;
	}
	
	public double tile2lon(int x, int z) 
	{
	   return x / Math.pow(2.0, z) * 360.0 - 180;
	}
	
	/*private double tile2lat(int y, int z) 
	{
	  
		//double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
	  //return Math.toDegrees(Math.atan(Math.sinh(n)));
	}*/
	
	public double tile2lat(int y, int aZoom) 
	{

        final double MerkElipsK = 0.0000001;
        final long sradiusa = 6378137;
        final long sradiusb = 6356752;
        final double FExct = (double) Math.sqrt(sradiusa * sradiusa - sradiusb * sradiusb)/sradiusa;
        final int TilesAtZoom = 1 << aZoom;
        double result = (y - TilesAtZoom / 2) / -(TilesAtZoom / (2 * Math.PI));
        result = (2 * Math.atan(Math.exp(result)) - Math.PI / 2) * 180/ Math.PI;
        double Zu = result / (180 / Math.PI);
        double yy = ((y) - TilesAtZoom / 2);

        double Zum1 = Zu;
        Zu = Math.asin(1 - ((1 + Math.sin(Zum1)) * Math.pow(1 - FExct * Math.sin(Zum1), FExct)) / (Math.exp((2 * yy) / -(TilesAtZoom / (2 * Math.PI))) * Math.pow(1 + FExct * Math.sin(Zum1), FExct)));
        while (Math.abs(Zum1 - Zu) >= MerkElipsK) 
        {
            Zum1 = Zu;
            Zu = Math.asin(1 - ((1 + Math.sin(Zum1)) * Math.pow(1 - FExct * Math.sin(Zum1), FExct))/ (Math.exp((2 * yy) / -(TilesAtZoom / (2 * Math.PI))) * Math.pow(1 + FExct * Math.sin(Zum1), FExct)));
        }
        result = Zu * 180 / Math.PI;
        return result;

    }
	
    public double[] mercatorToTiles(double[] e) 
    {
        double d = Math.round((20037508.342789 + e[0]) * 53.5865938);
        double f = Math.round((20037508.342789 - e[1]) * 53.5865938);
        d = boundaryRestrict(d, 0, 2147483647);
        f = boundaryRestrict(f, 0, 2147483647);
        return new double[] { d, f };
    }
    
    public static double boundaryRestrict(double f, double e, double d) 
    {
        return Math.max(Math.min(f, d), e);
    }
    
    public GILonLat mercatorToTiles(GILonLat point)
    {
		 double[] res = mercatorToTiles(new double[] { point.lon(),  point.lat() });
		 return new GILonLat(res[0], res[1]);
    }
    public int toScale(int i) 
    {
        return 23 - i;
    }
    
    public int[] getTile(double[] h, int i) {
    	int e = 8;
    	int j = toScale(i), g = (int) h[0] >> j, f = (int) h[1] >> j;
        return new int[] { g >> e, f >> e };
    }
    public void getTile(GILonLat point, int i)
    {
		 double[] mercator = mercatorToTiles(new double[] { point.lon(),  point.lat() });
		 int [] res = getTile(mercator, i);
		 m_xtile = res[0];
		 m_ytile = res[1];

    }

	public int getX() 
	{
		return m_xtile;
	}

	public int getY() 
	{
		return m_ytile;
	}

	public int getZoom() 
	{
		return m_zoom;
	}
}
