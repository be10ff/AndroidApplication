package ru.tcgeo.application.wkt;


public class GIWKTDescription
{

	public String m_wkt;
	GIWKTType m_str_type;
	GIWKTDescription m_block;
	public String m_file;

	GIWKTDescription()
	{
	}
	@Override
	public String toString()
	{
		return m_str_type.toString() + m_block.toString();
	}
	
}
