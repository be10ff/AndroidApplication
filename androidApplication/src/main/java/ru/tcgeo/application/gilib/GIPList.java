package ru.tcgeo.application.gilib;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import ru.tcgeo.application.gilib.parser.GIParserArray;


public class GIPList {
	
	public class GIMarker
	{
		public String m_name;
		public String m_description;
		public String m_image;
		public double m_lon;
		public double m_lat;
		public double m_diag;
	}
	public ArrayList<GIMarker> m_list;
	
	public GIPList()
	{
		
		m_list = new ArrayList<GIMarker>();
	}
	public void Load(String path)
	{

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
			try
			{
				while(parser.getEventType() != XmlPullParser.END_DOCUMENT)
				{
					if(parser.getEventType() == XmlPullParser.START_TAG)
					{
						if(parser.getName().equalsIgnoreCase("array"))
						{
							GIParserArray parser_array = new GIParserArray(parser, this);
							parser = parser_array.ReadSection();
						}
					}
					parser.next();
				}
			}
			catch(IOException e)
			{}	
			finally {}

		}
		catch(XmlPullParserException e)
		{}
		return;
		
	}

}
