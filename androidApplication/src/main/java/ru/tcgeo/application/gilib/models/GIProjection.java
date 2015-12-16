package ru.tcgeo.application.gilib.models;

import ru.tcgeo.application.utils.GIYandexUtils;

public class GIProjection
{
	int mId;
	private String mWKT;
	
	private GIProjection (String wkt_description, int id)
    {
		mWKT = wkt_description;
		mId = id;
    }
//	public GIProjection (long epgs)
//    {
//    }


	public static GIProjection WGS84()
	{
		return new GIProjection(
				"GEOGCS[\"WGS 84\"," +
                         "DATUM[\"WGS_1984\"," +
                          		 "SPHEROID[\"WGS 84\"," +
                        		 		    "6378137," +
                        				    "298.257223563," +
                        				    "AUTHORITY[\"EPSG\",\"7030\"]]," +
                        		 "AUTHORITY[\"EPSG\",\"6326\"]]," +
                         "PRIMEM[\"Greenwich\"," +
                        		  "0," +
                        		  "AUTHORITY[\"EPSG\",\"8901\"]]," +
                         "UNIT[\"degree\"," +
                        	    "0.01745329251994328," +
                        	    "AUTHORITY[\"EPSG\",\"9122\"]]," +
						 "AUTHORITY[\"EPSG\",\"4326\"]]", 0);
						 
	}
	

	public static GIProjection WorldMercator()
	
	{
		return new GIProjection(
				"PROJCS[\"WGS 84 / World Mercator\"," +
						 "GEOGCS[\"WGS 84\"," +
						 		  "DATUM[\"WGS_1984\"," +
						 		  		  "SPHEROID[\"WGS 84\"," +
						 		  		  			 "6378137," +
						 		  		  			 "298.257223563," +
						 		  		  			 "AUTHORITY[\"EPSG\"," +
						 		  		  			           "\"7030\"]]," +
						 		  		  "AUTHORITY[\"EPSG\",\"6326\"]]," +
						 		  "PRIMEM[\"Greenwich\"," +
						 		  		   "0," +
						 		  		   "AUTHORITY[\"EPSG\",\"8901\"]]," +
						 		  "UNIT[\"degree\"," +
						 		  		 "0.01745329251994328," +
						 		  		 "AUTHORITY[\"EPSG\",\"9122\"]]," +
						 		  "AUTHORITY[\"EPSG\",\"4326\"]]," +
						 "UNIT[\"metre\"," +
						 		"1," +
						 		"AUTHORITY[\"EPSG\",\"9001\"]]," +
        				 "PROJECTION[\"Mercator_1SP\"]," +
        				 "PARAMETER[\"central_meridian\",0]," +
        				 "PARAMETER[\"scale_factor\",1]," +
        				 "PARAMETER[\"false_easting\",0]," +
        				 "PARAMETER[\"false_northing\",0]," +
        				 "AUTHORITY[\"EPSG\",\"3395\"]," +
        				 "AXIS[\"Easting\",EAST]," +
    					 "AXIS[\"Northing\",NORTH]]", 1);
	}
	
	public static GILonLat ReprojectLonLat (GILonLat point, GIProjection source, GIProjection dest)
	{
			if(WorldMercator().mId == source.mId && WGS84().mId == dest.mId) {
				return GIYandexUtils.MercatorToGeo(point);
			}if(WGS84().mId == source.mId && WorldMercator().mId == dest.mId){
				return GIYandexUtils.GeoToMercator(point);
			}

		return point;
	}
}
