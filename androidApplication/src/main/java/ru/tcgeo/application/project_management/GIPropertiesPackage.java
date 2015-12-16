//package ru.tcgeo.application.project_management;
//
//import java.util.ArrayList;
//
//import ru.tcgeo.application.gilib.parser.GIPropertiesFile;
//
//public class GIPropertiesPackage
//{
//	public String m_crc;
//	public int m_id;
//	public String m_name;
//	public long m_size;
//	public ArrayList<GIPropertiesFile> m_Entries;
//
//	public GIPropertiesPackage()
//	{
//		m_crc = "";
//		m_id = 0;
//		m_name = "";
//		m_size = 0;
//		m_Entries = new ArrayList<GIPropertiesFile>();
//	}
//	public GIPropertiesPackage(int id, String name)
//	{
//		m_id = id;
//		m_name = name;
//	}
//	@Override
//	public String toString()
//	{
//		return "{id:" + m_id + ", packet_name:" + m_name + "}";
//
//	}
//	public boolean equal(Object o)
//	{
//		if (this == o)
//		{
//			return true;
//		}
//		if (!(o instanceof GIPropertiesPackage))
//		{
//			return false;
//		}
//		GIPropertiesPackage obj = (GIPropertiesPackage)o;
//		if(m_crc.equalsIgnoreCase(obj.m_crc))
//		{
//			return true;
//		}
//		return false;
//	}
//}
