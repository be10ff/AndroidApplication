package ru.tcgeo.application.data.gilib.parser;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class GIPropertiesLayerRef 
{
	public String m_type;
	public String m_name;

	public GIPropertiesLayerRef() 
	{
		m_type = "";
		m_name = "";
	}
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "LayerRef");
		serializer.attribute("", "name", m_name);
		serializer.attribute("", "type", m_type);
		serializer.endTag("", "LayerRef");
		return serializer;
	}
}
