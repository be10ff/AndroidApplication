package ru.tcgeo.application.gilib;


import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.models.GIProjection;

public class GISQLYandexTile extends GITileInfoOSM
{

	public GISQLYandexTile(int z, double lon, double lat)
	{
		GILonLat point = GIProjection.ReprojectLonLat(new GILonLat(lon, lat), GIProjection.WGS84(), GIProjection.WorldMercator());
		m_zoom = z;   
		getTile(new GILonLat(point.lon(), point.lat()), z);
	    getBounds();
		
	}
	public GISQLYandexTile(int z, int tile_x, int tile_y)
	{
		super(z, tile_x, tile_y);
	}
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
    public void getTile(GILonLat point, int i)
    {
		 double[] mercator = mercatorToTiles(new double[] { point.lon(),  point.lat() });
		 int [] res = getTile(mercator, i);
		 m_xtile = res[0];
		 m_ytile = res[1];

    }
    public double[] mercatorToTiles(double[] e) 
    {
        double d = Math.round((20037508.342789 + e[0]) * 53.5865938);
        double f = Math.round((20037508.342789 - e[1]) * 53.5865938);
        d = boundaryRestrict(d, 0, 2147483647);
        f = boundaryRestrict(f, 0, 2147483647);
        return new double[] { d, f };
    }
    public int[] getTile(double[] h, int i) {
    	int e = 8;
    	int j = toScale(i), g = (int) h[0] >> j, f = (int) h[1] >> j;
        return new int[] { g >> e, f >> e };
    }
    public static double boundaryRestrict(double f, double e, double d) 
    {
        return Math.max(Math.min(f, d), e);
    }
    public int toScale(int i) 
    {
        return 23 - i;
    }
}
