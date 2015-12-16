package ru.tcgeo.application.gilib.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;


public class GIPropertiesGroup extends ru.tcgeo.application.gilib.parser.GIPropertiesLayer implements ru.tcgeo.application.gilib.parser.ILayersRoot
{
	public double m_opacity;
	public boolean m_enabled;
	public boolean m_obscure;

	public String ToString()
	{
		String res = super.ToString();
		res+= "Group : opacity=" + m_opacity + " enabled=" + m_enabled + " obscure=" + m_obscure + "\n";
		return res;
	}
	//XmlSerializer serializer
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "Group");
		serializer.attribute("", "name", "");
		serializer.attribute("", "opacity", String.valueOf(m_opacity));
		serializer.attribute("", "enabled", String.valueOf(m_enabled));
		serializer.attribute("", "obscure", String.valueOf(m_obscure));
		for(ru.tcgeo.application.gilib.parser.GIPropertiesLayer entry:m_Entries)
		{
			serializer = entry.Save(serializer);
		}
		serializer.endTag("", "Group");
		return serializer;
	}
}