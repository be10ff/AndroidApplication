package ru.tcgeo.application.gilib.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import ru.tcgeo.application.layer.GISQLLayer;

public class GISQLDB {


	public String m_zoom_type;
	public GISQLLayer.GISQLiteZoomingType m_zooming_type;
	public int m_max_z;
	public int m_min_z;
	public int mRatio;

	
	public GISQLDB() 
	{
		m_zoom_type = "auto";
		m_zooming_type = GISQLLayer.GISQLiteZoomingType.AUTO;
		m_max_z = 19;
		m_min_z = 1;
		mRatio = 1;
	}
	
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
		private GISQLDB source;
		private String zoomType;
		private GISQLLayer.GISQLiteZoomingType zoomingType;
		private int maxZ;
		private int minZ;
		private int ratio;

		public Builder(){}
		public Builder(GISQLDB source){
			this.source = source;
		}

        public Builder zoomType(String zoomType){
			this.zoomType = zoomType;
			if(zoomType.equalsIgnoreCase("adaptive")) {
				this.zoomingType = GISQLLayer.GISQLiteZoomingType.ADAPTIVE;
			} else if(zoomType.equalsIgnoreCase("smart")) {
				this.zoomingType = GISQLLayer.GISQLiteZoomingType.SMART;
			} else {
				this.zoomingType = GISQLLayer.GISQLiteZoomingType.AUTO;
			}
			return this;
		}

        public Builder maxZ(int maxZ){
			this.maxZ = maxZ;
			return this;
		}

        public Builder minZ(int minZ){
			this.minZ = minZ;
			return this;
		}

        public Builder ratio(int ratio){
			this.ratio = ratio;
			return this;
		}

        public GISQLDB build(){
			if(source == null){
				source = new GISQLDB();
			}
			if(zoomType != null){
				source.m_zoom_type = zoomType;
			}
			if(zoomingType != null){
				source.m_zooming_type = zoomingType;
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
