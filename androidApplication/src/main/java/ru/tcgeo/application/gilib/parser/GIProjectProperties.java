package ru.tcgeo.application.gilib.parser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import ru.tcgeo.application.App;
import ru.tcgeo.application.R;
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
//	public GIPropertiesEdit m_Edit;

	private Context context;
	
//	public GIScriptParser m_scriptparser_info;
//	public GIScriptParser m_scriptparser_search;
public GIProjectProperties(Context context)
	{
		this.context = context;


		m_name = context.getString(R.string.default_project_name);
		m_id = 0;
		m_decription = context.getString(R.string.default_project_name);
//        m_SaveAs = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getString(R.string.default_project_path);

		m_path = App.getInstance().getPreference().getNewProjectName();

		m_str_projection = "WGS84";
		m_markers = "";
//        m_markers_source = "file";
//        m_search_file = "";
//        m_search_body = "";
		m_top = 65;
		m_bottom = 46;
		m_left=28;
		m_right=48;
        m_point_info = "";

        m_Group = new GIPropertiesGroup();
//		GIBounds temp = new GIBounds(GIProjection.WGS84(), 28, 65, 48, 46);
		//  <Bounds projection="WGS84" top="57.32512080429306" bottom="53.7729298508898"
		// //left="35.68824394054793" right="39.35910281197162" />
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
	public void SavePro(String path)
	{
		try {

			String sourcePath = new File(path).getParent() + "/temp.pro";
			FileOutputStream tmpFile = new FileOutputStream(sourcePath);
			XmlSerializer serializer = Xml.newSerializer();

			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			StringWriter writer = new StringWriter();
			
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "Project");
			serializer.attribute("", "name", m_name);
			if (m_SaveAs != null && !m_SaveAs.isEmpty()) {
				serializer.attribute("", "SaveAs", m_SaveAs);
			}
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

//			if (m_Edit != null) {
//				serializer = m_Edit.Save(serializer);
//			}
			
			if(m_markers_source != null) {
				serializer.startTag("", "Markers");
				serializer.attribute("", "file", m_markers);
				serializer.attribute("", "source", m_markers_source);
				serializer.endTag("", "Markers");
			}

			if (m_point_info != null) {
				serializer.startTag("", "PointInfo");
				serializer.text(m_point_info);
				serializer.endTag("", "PointInfo");
			}

			serializer.endTag("", "Project");
			serializer.endDocument();
			writer.toString();
			tmpFile.write(writer.toString().getBytes());
			tmpFile.flush();
			tmpFile.close();

			FileUtils.copyFile(new File(sourcePath), new File(path));

		} catch (Exception e) {
			Log.d("LOG_TAG", e.toString());
		}
		
	}
}

