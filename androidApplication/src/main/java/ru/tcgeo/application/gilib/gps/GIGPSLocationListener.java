package ru.tcgeo.application.gilib.gps;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import ru.tcgeo.application.gilib.GIEditLayersKeeper;
import ru.tcgeo.application.gilib.models.GILonLat;
import ru.tcgeo.application.gilib.GIMap;
import ru.tcgeo.application.gilib.models.GIProjection;

public class GIGPSLocationListener implements LocationListener 
{
	public GILonLat m_location;
	public LocationManager m_location_manager;
	GIMap m_map;
	public GIGPSLocationListener(GIMap map)
	{
		m_map = map;
		m_location_manager = (LocationManager) m_map.getContext().getSystemService(Context.LOCATION_SERVICE);
		m_location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	2000, 5, this);
		m_location_manager.requestLocationUpdates(	LocationManager.NETWORK_PROVIDER, 2000, 5, this);
		
		/**/
		//TODO test provider
		String mocLocationProvider = "mock"/*LocationManager.GPS_PROVIDER*/;
		if (null == m_location_manager.getProvider(mocLocationProvider))
		{
			m_location_manager.addTestProvider(mocLocationProvider, false, false, false, false, true, true, true, 0, 5);
		}
		m_location_manager.setTestProviderEnabled(mocLocationProvider, true);
		m_location_manager.requestLocationUpdates(mocLocationProvider, 0, 0, this);
//		GI_WktLinestring track = (GI_WktLinestring) GIWKTParser.CreateGeometryFromWKT("LINESTRING(37.559085280886386 55.80221944939844, 37.55960621469864 55.80165563337241, 37.5601063111584 55.8011035555688, 37.56075226908558 55.80050448352692, 37.56198167288249 55.800868626459504, 37.563294426089364 55.801174034024015, 37.56448215518129 55.801420707608315, 37.56416959489393 55.80226643369717, 37.56341945020429 55.80301817473325, 37.562752654924616 55.80361720793568, 37.561148178782894 55.8031708703501, 37.559647889403614 55.80267754388229, 37.5584393229592 55.80206675003432, 37.557376617982214 55.801491185487016, 37.556251400947765 55.80092735887029, 37.555563768315594 55.80097434473485, 37.554813623625954 55.801526424378324, 37.553813430706434 55.80244262431031, 37.553313334246674 55.80280674904012, 37.55262570161451 55.803382294057506, 37.551792207514914 55.80386386597578, 37.550937876062825 55.80463906674227, 37.549979357848294 55.80418099543566, 37.5484999058215 55.80353498824023, 37.54616612234264 55.80261881412274, 37.547270502024595 55.80178484193591, 37.54841655641155 55.80090386591665, 37.54979182167588 55.80048099031706, 37.552188117212225 55.80041051060207, 37.55441771392865 55.800833386970346, 37.55668898535006 55.80228992582518, 37.55843932295921 55.803100395524744)");
//		new MockLocationProvider(m_location_manager, mocLocationProvider, track).start();
		/**/
	}
	public void onLocationChanged(Location location) 
	{
		// Assuming we get wgs84 coordinates
		m_location = new GILonLat(location.getLongitude(), location.getLatitude());
		m_location = GIProjection.ReprojectLonLat(m_location, GIProjection.WGS84(), m_map.Projection());
		GIEditLayersKeeper.Instance().onGPSLocationChanged(location);
	}

	public void onProviderDisabled(String provider) 
	{

	}

	public void onProviderEnabled(String provider) 
	{

	}

	public void onStatusChanged(String provider, int status, Bundle extras) 
	{

	}
}
