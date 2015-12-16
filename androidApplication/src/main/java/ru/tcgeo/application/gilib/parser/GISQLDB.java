package ru.tcgeo.application.gilib.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public class GISQLDB {


	public String m_zoom_type;
	public int m_max_z;
	public int m_min_z;
	public int mRatio;
	
	public GISQLDB() 
	{
		m_zoom_type = "auto";
		m_max_z = 19;
		m_min_z = 1;
		mRatio = 1;
	}
	
	/*public GISQLDB(String zoom_type, int min, int max)
	{
		m_zoom_type = zoom_type;
		m_max_z = max;
		m_min_z = min;
	}*/
	public String ToString()
	{
		String Res = "sqlitedb \n";
		Res += "type=" + m_zoom_type + " m_min_z=" + m_min_z + " m_max_z=" + m_max_z + " mRatio=" + mRatio +"\n";
		return Res;
	}

	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "sqlitedb");
		serializer.attribute("", "zoom_type", m_zoom_type);
		serializer.attribute("", "min", String.valueOf(m_min_z));
		serializer.attribute("", "max", String.valueOf(m_max_z));
		serializer.attribute("", "ratio", String.valueOf(mRatio));
		serializer.endTag("", "sqlitedb");
		return serializer;
	}
}
