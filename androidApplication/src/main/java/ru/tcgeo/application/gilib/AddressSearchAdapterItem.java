package ru.tcgeo.application.gilib;

public class AddressSearchAdapterItem 
{
	final public String m_name;
	public double m_lon;
	public double m_lat;
	public double m_diag;
	
	public AddressSearchAdapterItem (String name, double lon, double lat, double diag)
	{
		m_name = name;
		m_lon = lon;
		m_lat = lat;
		m_diag = diag;
	}
	
	@Override
	public String toString ()
	{
	    return m_name;
	}
	
}
