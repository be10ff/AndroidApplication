package ru.tcgeo.application.gilib.parser;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

public class GIPropertiesEdit 
{
	public ArrayList<GIPropertiesLayerRef> m_Entries;
	
	public GIPropertiesEdit() 
	{
		m_Entries = new ArrayList<GIPropertiesLayerRef>();
	}
	//XmlSerializer serializer
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "Edit");
		for(GIPropertiesLayerRef entry:m_Entries)
		{
			serializer = entry.Save(serializer);
		}
		serializer.endTag("", "Edit");
		return serializer;
	}
}
