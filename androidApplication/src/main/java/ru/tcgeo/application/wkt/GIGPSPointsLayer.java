package ru.tcgeo.application.wkt;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import ru.tcgeo.application.gilib.GIEditableLayer;
import ru.tcgeo.application.gilib.models.GIEncoding;
import ru.tcgeo.application.gilib.models.GIVectorStyle;

public class GIGPSPointsLayer  extends GIEditableLayer
{

	public GIGPSPointsLayer(String path) 
	{
		super(path);
		type_ = GILayerType.XML;
	}
	public GIGPSPointsLayer(String path, GIVectorStyle style)
	{
		super(path, style);
		type_ = GILayerType.XML;
	}

	public GIGPSPointsLayer(String path, GIVectorStyle style, GIEncoding encoding)
	{
		super(path, style, encoding);
		type_ = GILayerType.XML;
	}

	public void DeleteObject(GI_WktGeometry geometry)
	{

	}

	
	public void AddGeometry(GI_WktGeometry geometry)
	{
		geometry.m_ID = m_shapes.size();
		m_shapes.add(geometry);
	}
	public void Load()
	{
		try
		{
			XmlPullParser parser;
			FileInputStream xmlFile = null;
			try
			{
				xmlFile = new FileInputStream(m_path);
			}
			catch(FileNotFoundException e)
			{
				Log.d("LOG_TAG", e.toString());
				return;
			}
			XmlPullParserFactory factiry = XmlPullParserFactory.newInstance();
			factiry.setNamespaceAware(true);
			parser = factiry.newPullParser();
			parser.setInput(xmlFile, null);
			
			while(parser.getEventType() != XmlPullParser.END_DOCUMENT)
			{
				if(parser.getEventType() == XmlPullParser.START_TAG)
				{
					if(parser.getName().equalsIgnoreCase("Geometries"))
					{
						GIGPSParser parser_layer = new GIGPSParser(parser, this);
						parser = parser_layer.ReadSection();
					}
				}
				try
				{
					parser.next();
				}
				catch(IOException e)
				{
					Log.d("LOG_TAG", e.toString());
				}	
				finally 
				{

				}
			}
			xmlFile.close();
		}
		catch(Exception e)
		{
			Log.d("LOG_TAG", e.toString());
		}
	}

	public void Save()
	{
		try
		{
			String path = m_path;
			FileOutputStream xmlFile = new FileOutputStream(path);
			XmlSerializer serializer = Xml.newSerializer();
		
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			StringWriter writer = new StringWriter();
			
			serializer.setOutput(writer);

			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "Geometries");
			for(GI_WktGeometry geometry : m_shapes)
			{
				serializer = geometry.Serialize(serializer);
			}
			serializer.endTag("", "Geometries");
			serializer.endDocument();

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
