package ru.tcgeo.application.gilib.parser;

import java.io.File;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;

public class GISource 
{
	public String m_location;
	public String m_name;
	
	public GISource()
	{
		
	}
	
	public GISource(String location, String name)
	{
		m_location = location;
		m_name = name;
	}


	public String ToString()
	{
		String Res = "Source \n";
		Res += "name=" + m_name + " m_location=" + m_location + "\n";
		return Res;
	}
	public String GetLocalPath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_name;
	}
	public String GetRemotePath()
	{
		return m_name;
	}
	
	public String GetAbsolutePath()
	{
		return m_name;
	}
	
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "Source");
		serializer.attribute("", "location", m_location);
		serializer.attribute("", "name", m_name);
		serializer.endTag("", "Source");
		return serializer;
	}

	public static class Builder{
		private String location;
		private String name;
		GISource source;

		Builder(){}

		Builder(GISource source){
			this.source = source;
		}

		Builder location(String location){
			this.location = location;
			return this;
		}

		Builder name(String name){
			this.name = name;
			return this;
		}

		GISource build(){
			if(source == null){
				source = new GISource();
			}
			if(location != null) {
				source.m_location = location;
			}
			if(name != null) {
				source.m_name = name;
			}
			return source;
		}
	}
}
