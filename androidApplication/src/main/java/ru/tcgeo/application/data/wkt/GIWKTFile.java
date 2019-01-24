package ru.tcgeo.application.data.wkt;

import ru.tcgeo.application.data.gilib.script.GIScriptQueue;

public class GIWKTFile extends GIWKTDescription {

	String m_file;
	
	public GIWKTFile(GIScriptQueue text)
	{
		m_file = "";
		m_file += text.Pop();
		char current = text.Look();
		while(current != '\"')
		{
			m_file += text.Pop();
			current = text.Look();
		}
		text.Pop();
	}
	
	@Override
	public String toString()
	{
		return m_file;
		
	}

}
