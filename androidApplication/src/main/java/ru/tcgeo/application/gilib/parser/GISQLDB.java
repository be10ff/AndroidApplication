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

	public static class Builder{
		GISQLDB source;
		public String zoomType;
		public int maxZ;
		public int minZ;
		public int ratio;

		Builder(){}
		Builder(GISQLDB source){
			this.source = source;
		}

		Builder zoomType(String zoomType){
			this.zoomType = zoomType;
			return this;
		}

		Builder maxZ(int maxZ){
			this.maxZ = maxZ;
			return this;
		}

		Builder minZ(int minZ){
			this.minZ = minZ;
			return this;
		}

		Builder ratio(int ratio){
			this.ratio = ratio;
			return this;
		}

		GISQLDB build(){
			if(source == null){
				source = new GISQLDB();
			}
			if(zoomType != null){
				source.m_zoom_type = zoomType;
			}
			if(maxZ != -1 && maxZ != 0){
				source.m_max_z = maxZ;
			}
			if(minZ != -1 && minZ != 0){
				source.m_min_z = minZ;
			}
			if(ratio != -1 && ratio != 0){
				source.mRatio = ratio;
			}
			return source;
		}
	}
}
