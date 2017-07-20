package ru.tcgeo.application.gilib.models;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import ru.tcgeo.application.gilib.parser.GIParserArray;


public class GIPList {

	//	public class GIMarker
//	{
//		public String name;
//		public String description;
//		public String image;
//		public double lon;
//		public double lat;
//		public double diag;
//	}
	public ArrayList<Marker> m_list;
	
	public GIPList()
	{

		m_list = new ArrayList<Marker>();
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
