package ru.tcgeo.application.gilib.parser;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

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

	public boolean IsWithinRange(double scale) {
		double m_min = 1 / ((double) m_to);
		double m_max = 1 / ((double) m_from);

		if (scale > m_max && -1.0f != m_max)
			return false;

		return scale >= m_min;
	}

	public static class Builder{
		GIRange source;
		private int from;
		private int to;
		Builder(GIRange source){
			this.source = source;
		}

		Builder from(int from){
			this.from = from;
			return this;
		}
		Builder to(int to){
			this.to = to;
			return this;
		}

		public GIRange build() {
			if(from != -1 && from != 0){
				source.m_from = from;
			}
			if(to != -1 && to != 0){
				source.m_to = to;
			}
			return source;
		}
	}

	public static class ScaleRange {
		private double m_min;
		private double m_max;

		public ScaleRange(GIRange range) {
			if (range == null) {
				m_min = -1.0f;
				m_max = -1.0f;
			} else {
				m_min = 1 / ((double) range.m_from);
				m_max = 1 / ((double) range.m_to);
			}
		}
	}
}
