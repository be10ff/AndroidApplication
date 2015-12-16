package ru.tcgeo.application.gilib.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import ru.tcgeo.application.gilib.models.GIProjection;


public class GIProjectProperties 
{
	public String m_path;
	public String m_name;
	public String m_SaveAs;
	public int m_id;
	public String m_decription;
	public String m_markers;	
	public String m_markers_source;
//	public String m_search_file;
//	public String m_search_body;
	public String m_point_info;
	public String m_str_projection;
	public GIProjection m_projection;
	public double m_top;
	public double m_bottom;
	public double m_left;
	public double m_right;
//	public ArrayList<GIPropertiesPackage> m_Entries;
	public GIPropertiesGroup m_Group;
	public GIPropertiesEdit m_Edit;
	
//	public GIScriptParser m_scriptparser_info;
//	public GIScriptParser m_scriptparser_search;
	public GIProjectProperties()
	{
        m_name = "Empty";
        m_id = 0;
        m_decription = "Empty";
        m_SaveAs = "Empty.pro";
        m_path = "Empty.pro";
        m_str_projection = "WGS84";
        m_markers = "";	
//        m_markers_source = "file";
//        m_search_file = "";
//        m_search_body = "";
        m_point_info = "";
        
        m_Group = new GIPropertiesGroup();
	}
	
	public GIProjectProperties(String path)
	{
		this.m_path = path;
		m_SaveAs = "";
//		m_Entries = new ArrayList<GIPropertiesPackage>();
		this.LoadPro(path);
//		m_scriptparser_info = new GIScriptParser(m_point_info);//, null, null);
//		m_scriptparser_search = new GIScriptParser(m_search_body);//, null, null);
	}
	
	public GIProjectProperties(InputStream stream)
	{
		this.m_path = null;
		m_SaveAs = "";
//		m_Entries = new ArrayList<GIPropertiesPackage>();
		this.LoadPro(stream);
//		m_scriptparser_info = new GIScriptParser(m_point_info);//, null, null);
//		m_scriptparser_search = new GIScriptParser(m_search_body);//, null, null);
	}
	
	public GIProjectProperties(String path, boolean info_only)
	{
		this.m_path = path;
		m_SaveAs = "";
//		m_Entries = new ArrayList<GIPropertiesPackage>();
		this.LoadInfo(path);
//		m_scriptparser_info = new GIScriptParser(m_point_info);
//		m_scriptparser_search = new GIScriptParser(m_search_body);
	}
	
	public String ToString()
	{
		String res = "";
		res = res + "Project \n\r name=" + m_name + " ID=" + m_id + "\n Map \n";
		if(m_Group != null)
		{
		res = res + m_Group.ToString();
		}
		res = res + "Description text=" + m_decription + "\nBounds projection=" + m_str_projection + " top=" + m_top+ " left=" + m_left+ " bottom=" + m_bottom+ " right=" + m_right + "\n";
		res = res + "Markers file=" +m_markers + "\n";
		res = res + "PointInfo " + m_point_info + "\n";
//		res = res + "Search file=" + m_search_file + "\n" + m_search_body + "\n" ;
		return res;
	}
	public boolean LoadInfo(String path)
	{
		m_path = path;
		boolean res = false;
		try
		{
			XmlPullParser parser;
			FileInputStream xmlFile = null;
			try
			{
				xmlFile = new FileInputStream(path);
			}
			catch(FileNotFoundException e){}
			XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
			factiry.setNamespaceAware(true);
			parser = factiry.newPullParser();
			parser.setInput(xmlFile, null);
			
			while(parser.getEventType() != XmlPullParser.END_DOCUMENT)
			{
				if(parser.getEventType() == XmlPullParser.START_TAG)
				{
					if(parser.getName().equalsIgnoreCase("Project"))
					{
						GIParser parser_project = new GIParser(parser, this);
						parser = parser_project.ReadInfo();
					}
				}
				try
				{
					parser.next();
				}
				catch(IOException e)
				{}	
				finally {}


			}
		}
		catch(XmlPullParserException e)
		{}
		return res;
	}	
	
	public boolean LoadPro(String path)
	{
		m_path = path;
		boolean res = false;
		try
		{
			XmlPullParser parser;
			FileInputStream xmlFile = null;
			try
			{
				xmlFile = new FileInputStream(path);
			}
			catch(FileNotFoundException e){}
			XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
			factiry.setNamespaceAware(true);
			parser = factiry.newPullParser();
			parser.setInput(xmlFile, null);
			
			while(parser.getEventType() != XmlPullParser.END_DOCUMENT)
			{
				if(parser.getEventType() == XmlPullParser.START_TAG)
				{
					if(parser.getName().equalsIgnoreCase("Project"))
					{
						GIParser parser_project = new GIParser(parser, this);
						parser = parser_project.ReadSection();
					}
				}
				try
				{
					parser.next();
				}
				catch(IOException e)
				{}	
				finally {}
			}
		}
		catch(XmlPullParserException e)
		{}
		return res;
	}	
	
	public boolean LoadPro(InputStream xmlFile)
	{
		//m_path = path;
		boolean res = false;
		try
		{
			XmlPullParser parser;
			/*FileInputStream xmlFile = null;
			try
			{
				xmlFile = new FileInputStream(path);
			}
			catch(FileNotFoundException e){}*/
			XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
			factiry.setNamespaceAware(true);
			parser = factiry.newPullParser();
			parser.setInput(xmlFile, null);
			
			while(parser.getEventType() != XmlPullParser.END_DOCUMENT)
			{
				if(parser.getEventType() == XmlPullParser.START_TAG)
				{
					if(parser.getName().equalsIgnoreCase("Project"))
					{
						GIParser parser_project = new GIParser(parser, this);
						parser = parser_project.ReadSection();
					}
				}
				try
				{
					parser.next();
				}
				catch(IOException e)
				{}	
				finally {}
			}
		}
		catch(XmlPullParserException e)
		{}
		return res;
	}
	//----------------------------------------------------------------------------------------
	public void SavePro(String name)
	{
		try
		{
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name;
			//File file = new File(getFilesDir(), path)
			FileOutputStream xmlFile = new FileOutputStream(path);
			XmlSerializer serializer = Xml.newSerializer();
			// indentation as 3 spaces
			//serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "\t");
			// also set the line separator
			//serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
			
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			StringWriter writer = new StringWriter();
			
			serializer.setOutput(writer);
			//
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "Project");
			serializer.attribute("", "name", m_name);
			serializer.attribute("", "SaveAs", m_SaveAs);
			serializer.attribute("", "ID", String.valueOf(m_id));	
			
			serializer.startTag("", "Map");
			serializer = m_Group.Save(serializer);

			serializer.endTag("", "Map");
			
			serializer.startTag("", "Description");
			serializer.attribute("", "text", m_decription);
			serializer.endTag("", "Description");
			
			serializer.startTag("", "Bounds");
			serializer.attribute("", "projection", m_str_projection);
			serializer.attribute("", "top", String.valueOf(m_top));
			serializer.attribute("", "bottom", String.valueOf(m_bottom));
			serializer.attribute("", "left", String.valueOf(m_left));
			serializer.attribute("", "right", String.valueOf(m_right));
			serializer.endTag("", "Bounds");
			
			if(m_Edit != null)
			{
				serializer = m_Edit.Save(serializer);
			}
			
			serializer.startTag("", "Markers");
			serializer.attribute("", "file", m_markers);
			serializer.attribute("", "source", m_markers_source);
			serializer.endTag("", "Markers");
			
			if(m_point_info != null)
			{
				serializer.startTag("", "PointInfo");
				serializer.text(m_point_info);
				serializer.endTag("", "PointInfo");
			}
			
//			serializer.startTag("", "Search");
//			serializer.attribute("", "file", m_search_file);
//			serializer.text(m_search_body);
//			serializer.endTag("", "Search");
//			if(m_Entries != null)
//			{
//				for(GIPropertiesPackage pack : m_Entries)
//				{
//					serializer.startTag("", "Package");
//					serializer.attribute("", "name", pack.m_name);
//					serializer.attribute("", "size", String.valueOf(pack.m_size));
//					serializer.attribute("", "CRC", pack.m_crc);
//					serializer.attribute("", "ID", String.valueOf(pack.m_id));
//					for(GIPropertiesFile file : pack.m_Entries)
//					{
//						serializer.startTag("", "File");
//						serializer.attribute("", "name", file.m_name);
//						serializer.attribute("", "CRC", file.m_crc);
//						serializer.endTag("", "File");
//					}
//					serializer.endTag("", "Package");
//				}
//			}

			serializer.endTag("", "Project");
			serializer.endDocument();
			//
			writer.toString();
			xmlFile.write(writer.toString().getBytes());
			xmlFile.flush();
			xmlFile.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
		
	}
}

