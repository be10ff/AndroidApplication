package ru.tcgeo.application.gilib.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public class GIRange 
{
	public int m_from;
	public int m_to; 
	//public String m_nan;
	public GIRange()
	{
		m_from = -1;
		m_to = -1;
	}
	public String ToString()
	{
		String Res = "Range \n";
		Res += "m_from=" + m_from + " m_to=" + m_to + "\n";
		return Res;
	}
	
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "Range");
		if(m_from != -1)
		{
			serializer.attribute("", "from", String.valueOf(m_from));
		}
		else
		{
			serializer.attribute("", "from","NAN");
		}
		if(m_to != -1)
		{
			serializer.attribute("", "to",String.valueOf( m_to));
		}
		else
		{
			serializer.attribute("", "to","NAN");
		}

		serializer.endTag("", "Range");
		return serializer;
	}
	/*public GIScaleRange GetScaleRange()
	{
		
		return new GIScaleRange(1/m_from, 1/m_to);
	}*/
}
