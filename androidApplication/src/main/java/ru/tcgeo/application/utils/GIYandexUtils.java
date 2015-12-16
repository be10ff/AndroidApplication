package ru.tcgeo.application.utils;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.planimetry.Vertex;

public class GIYandexUtils 
{
	public static double R = 6378137;
	public static double Le = 40075000;
	public static double Lg = 20003930;
	public static double latitude_deg = Lg/180;
	
	public static GILonLat GeoToMercator(GILonLat point)
	{
		double d = Math.toRadians(point.lon());
		double m =  Math.toRadians(point.lat());
		//
		double k = 0.0818191908426;
		double f = k*Math.sin(m);
		double h = Math.tan(Math.PI / 4 + m / 2);
		double j = Math.pow(Math.tan(Math.PI / 4 + Math.asin(f) / 2), k);
		double i = h/j;

//		double[] lonlat = Mercator.merc(point.lon(), point.lat());


		return new GILonLat(R*d, R*Math.log(i));
	}
	
	public static GILonLat MercatorToGeo(GILonLat point)
	{
		//double j = Math.PI;
		//double f = Math.PI/2;
		//double i = R;
		
		double n = 0.003356551468879694;
		double  k = 0.00000657187271079536;
		double h = 1.764564338702e-8;
		double  m = 5.328478445e-11;
		
		double g = Math.PI/2 - 2*Math.atan(1/Math.exp(point.lat()/R));
		double l = g + n*Math.sin(2*g) + k*Math.sin(4*g) + h*Math.sin(6*g) + m*Math.sin(8*g);
		double d = point.lon()/R;
		
		return new GILonLat(Math.toDegrees(d), Math.toDegrees(l));
	}
	
	public static double boundaryRestrict(double f, double e, double d) 
	{
		return Math.max(Math.min(f, d), e);
	}
	
	public static int toScale(int i) 
	{
		return 23 - i;
	}
	
	public static GILonLat mercatorToTiles(GILonLat point) 
	{
		double d = Math.round((20037508.342789 + point.lon()) * 53.5865938);
		double f = Math.round((20037508.342789 - point.lat()) * 53.5865938);
		d = boundaryRestrict(d, 0, 2147483647);
		f = boundaryRestrict(f, 0, 2147483647);
		return new GILonLat(d, f);
	}
	
	public static GILonLat tileToMercator(Point d) 
	{
		return new GILonLat(Math.round(d.x / 53.5865938 - 20037508.342789), Math.round(20037508.342789 - d.y / 53.5865938));
	}
	
	public static GILonLat tileCoordinatesToPixels(GILonLat point, int h) 
	{
		double g = Math.pow(2, toScale(h));
		return new GILonLat((int) point.lon() / g, (int) point.lat() / g);
	}

	public static Point getTile(GILonLat point, int i) 
	{
		int e = 8;
		int j = toScale(i), g = (int) point.lon() >> j, f = (int) point.lat() >> j;
		return new Point(g >> e, f >> e);
	}
	
	public static Point getPxCoordFromTileCoord(GILonLat point, int i) 
	{
		int j = toScale(i);
		int g = (int) point.lon() >> j;
		int f = (int) point.lat() >> j;
		return new Point(g, f);
	}
	
	public static Point getTileCoordFromPixCoord(Point point, int i) 
	{
		int j = toScale(i);
		int g = point.x << j;
		int f = point.y << j;
		return new Point(g, f);
	}
	
    public static GILonLat ReGetTile(GILonLat point, int i) 
    {
        long e = 8;
        long j = toScale(i);
        long g = (long) point.lon() << (int) j;
        long f = (long) point.lat() << (int) j;
        double ge = g << (int) e;
        double fe = f << (int) e;
        long g2 = (long) (point.lon() + 1) << (int) j;
        long f2 = (long) (point.lat() + 1) << (int) j;
        double ge2 = g2 << (int) e;
        double fe2 = f2 << (int) e;

        double ad_g = (ge2 - ge) * (point.lon() - Math.floor(point.lon()));
        double ad_f = (fe2 - fe) * (point.lat() - Math.floor(point.lat()));

        return new GILonLat(ge + ad_g, fe + ad_f);
    }
    
    public static GILonLat ReGetTile(Point point, int i) {
        long e = 8;
        long j = toScale(i);
        long g = (long) point.x << (int) j;
        long f = (long) point.y << (int) j;
        return new GILonLat( g << (int) e, f << (int) e );
    }
    //TODO
    public static GILonLat getGeoFromTile(int x, int y, int zoom) 
    {
        double a, c1, c2, c3, c4, g, z, mercX, mercY;
        a = 6378137;
        c1 = 0.00335655146887969;
        c2 = 0.00000657187271079536;
        c3 = 0.00000001764564338702;
        c4 = 0.00000000005328478445;
        mercX = (x * 256 * 2 ^ (23 - zoom)) / 53.5865938 - 20037508.342789;
        mercY = 20037508.342789 - (y * 256 * 2 ^ (23 - zoom)) / 53.5865938;

        g = Math.PI / 2 - 2 * Math.atan(1 / Math.exp(mercY / a));
        z = g + c1 * Math.sin(2 * g) + c2 * Math.sin(4 * g) + c3
                * Math.sin(6 * g) + c4 * Math.sin(8 * g);

        return new GILonLat( mercX / a * 180 / Math.PI, z * 180 / Math.PI );
    }
    public static long[] getTileFromGeo(double lat, double lon, int zoom) {
        double rLon, rLat, a, k, z;
        rLon = lon * Math.PI / 180;
        rLat = lat * Math.PI / 180;
        a = 6378137;
        k = 0.0818191908426;
        z = Math.pow(
                Math.tan(Math.PI / 4 + rLat / 2)
                        / (Math.tan(Math.PI / 4 + Math.asin(k * Math.sin(rLat))
                                / 2)), k);
        return new long[] {
                (int) (((20037508.342789 + a * rLon) * 53.5865938 / Math.pow(2,
                        (23 - zoom))) / 256),
                (int) (((20037508.342789 - a * Math.log(z)) * 53.5865938 / Math
                        .pow(2, (23 - zoom)))) / 256 };
    }

    public static double tile2lon(int x, int aZoom) {
        return (x / Math.pow(2.0, aZoom) * 360.0) - 180;
    }

    public static double tile2lat(int y, int aZoom) {

        final double MerkElipsK = 0.0000001;
        final long sradiusa = 6378137;
        final long sradiusb = 6356752;
        final double FExct = (double) Math.sqrt(sradiusa * sradiusa - sradiusb
                * sradiusb)
                / sradiusa;
        final int TilesAtZoom = 1 << aZoom;
        double result = (y - TilesAtZoom / 2) / -(TilesAtZoom / (2 * Math.PI));
        result = (2 * Math.atan(Math.exp(result)) - Math.PI / 2) * 180
                / Math.PI;
        double Zu = result / (180 / Math.PI);
        double yy = ((y) - TilesAtZoom / 2);

        double Zum1 = Zu;
        Zu = Math.asin(1
                - ((1 + Math.sin(Zum1)) * Math.pow(1 - FExct * Math.sin(Zum1),
                        FExct))
                / (Math.exp((2 * yy) / -(TilesAtZoom / (2 * Math.PI))) * Math
                        .pow(1 + FExct * Math.sin(Zum1), FExct)));
        while (Math.abs(Zum1 - Zu) >= MerkElipsK) {
            Zum1 = Zu;
            Zu = Math
                    .asin(1
                            - ((1 + Math.sin(Zum1)) * Math.pow(
                                    1 - FExct * Math.sin(Zum1), FExct))
                            / (Math.exp((2 * yy)
                                    / -(TilesAtZoom / (2 * Math.PI))) * Math
                                        .pow(1 + FExct * Math.sin(Zum1), FExct)));
        }

        result = Zu * 180 / Math.PI;

        return result;

    }

    public static int[] getMapTileFromCoordinates(final double aLat,
            final double aLon, final int zoom) {
        final int[] out = new int[2];

        final double E2 = (double) aLat * Math.PI / 180;
        final long sradiusa = 6378137;
        final long sradiusb = 6356752;
        final double J2 = (double) Math.sqrt(sradiusa * sradiusa - sradiusb
                * sradiusb)
                / sradiusa;
        final double M2 = (double) Math.log((1 + Math.sin(E2))
                / (1 - Math.sin(E2)))
                / 2
                - J2
                * Math.log((1 + J2 * Math.sin(E2)) / (1 - J2 * Math.sin(E2)))
                / 2;
        final double B2 = (double) (1 << zoom);
        out[0] = (int) Math.floor(B2 / 2 - M2 * B2 / 2 / Math.PI);

        out[1] = (int) Math.floor((aLon + 180) / 360 * (1 << zoom));

        return out;
    }
    
    public static double[] DegreeWeight(GILonLat lonlat)
    {
    	double[] res = new double[2];
    	res[0] = Le*(Math.cos(Math.toRadians(lonlat.lat())))/360;
    	res[1] = latitude_deg;
    	return res;
    }
    public static double[] DegreeWeight(double lat)
    {
    	double[] res = new double[2];
    	res[0] = Le*(Math.cos(Math.toRadians(lat)))/360;
    	res[1] = latitude_deg;
    	return res;
    }
    /**
     * 
     * @param point в проекции Меркатора
     * @param canvas
     * @param area
     * @return
     */
	public static PointF MapToScreen(GILonLat point, Rect canvas, GIBounds area)
	{
		float koeffX = (float) (canvas.width() / (area.right() - area.left()));
		float koeffY = (float) (canvas.height() / (area.top() - area.bottom()));
		
		float x = (float) ((point.lon() - area.left()) * koeffX);
		float y = (float) (canvas.height() - (point.lat() - area.bottom()) * koeffY);
		return new PointF(x, y);
	}
    /**
     * 
     * @param point в проекции Меркатора
     * @param canvas
     * @param area
     * @return
     */	
	public static PointF MapToScreen(GILonLat point, Canvas canvas, GIBounds area)
	{
		return MapToScreen(point, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), area);
	}
	public static PointF MapToScreen(PointF point, Canvas canvas, GIBounds area)
	{
		return MapToScreen(new GILonLat(point.x, point.y), new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), area);
	}
	public static PointF MapToScreen(PointF point, Rect canvas, GIBounds area)
	{
		return MapToScreen(new GILonLat(point.x, point.y),  canvas, area);
	}
	public static PointF MapToScreen(Vertex point, Canvas canvas, GIBounds area)
	{
		return MapToScreen(point, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), area);
	}
    
	public static double DoubleLonLatFromString(String string)
	{
		int pos = -1;
		double k = 1f/60f;
		double result = 0;
		if(string.indexOf('.') != -1)
		{
			pos = string.indexOf('.');
		}
		if(string.indexOf(',') != -1)
		{
			pos = string.indexOf(',');
		}
		if(pos == -1)
		{
			//TODO
			if(string.length() >= 2)
			{
				result = result + Double.valueOf(string.substring(0, 2));
			}
			if(string.length() >= 4)
			{
				result = result + k* Double.valueOf(string.substring(2, 4));
			}
			if(string.length() >= 6)
			{
				result = result + k*k* Double.valueOf(string.substring(4, string.length()));
			}
		}
		//dd,dddddddddd
		//ddd.ddddddddd
		if(pos == 1 || pos == 2)
		{
			result = Double.valueOf(string);
		}
		//ddmm.mmmmmm
		//dddmm.mmmmmmmm
		if(pos == 5)
		{
			result =  Double.valueOf(string.substring(0, 3)) + k* Double.valueOf(string.substring(3, string.length() - 1));
		}
		//dddmm.mmmmmmmm
		if(pos == 4)
		{
			result =  Double.valueOf(string.substring(0, 2)) + k* Double.valueOf(string.substring(2, string.length()));
		}

		//ddmmss.ssssssssss
		if(pos == 6)
		{
			result =  Double.valueOf(string.substring(0, 2)) + k* Double.valueOf(string.substring(2, 4)) + k*k* Double.valueOf(string.substring(4, string.length()));
		}
		//dddmmss.ssssssssss
		if(pos == 7)
		{
			result =  Double.valueOf(string.substring(0, 3)) + k* Double.valueOf(string.substring(3, 5)) + k*k* Double.valueOf(string.substring(5, string.length()));
		}
		return result;
	}

	public class SphericalMercator {
		public  double y2lat(double aY) {
			return Math.toDegrees(2* Math.atan(Math.exp(Math.toRadians(aY))) - Math.PI/2);
		}

		public  double lat2y(double aLat) {
			return Math.toDegrees(Math.log(Math.tan(Math.PI/4+Math.toRadians(aLat)/2)));
		}
	}
}



