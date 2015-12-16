package ru.tcgeo.application.gilib.models;

import java.io.UnsupportedEncodingException;

public class GIEncoding
{
	public String m_encoding;
	
	public GIEncoding (String encoding)
	{
		if(encoding.equalsIgnoreCase("OGR"))
		{
			encoding = "CP1251";
		}
		if(encoding.equalsIgnoreCase("Latin"))
		{
			encoding = "ISO-8859-1";
		}

		m_encoding = encoding;
	}

	public String decode (byte[] text)
	{
		String result;
		try
        {
	        result = new String(text, m_encoding);
        }
        catch (UnsupportedEncodingException e)
        {
        	return null;
        }
		
		return result;
	}
	
	public static String decode (byte[] text, String encoding)
	{
		String result;
		try
        {
	        result = new String(text, encoding);
        }
        catch (UnsupportedEncodingException e)
        {
        	return null;
        }
		
		return result;
	}
	public String ToString()
	{
		String Res = "Encoding ";
		Res += "name=" + m_encoding  + "\n";
		return Res;
	}
}
