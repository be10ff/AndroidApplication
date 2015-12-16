package ru.tcgeo.application.wkt;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.script.GIScriptQueue;


public class GIWKTVertex extends GIWKTDescription
{
	ArrayList<GIWKTDescription> m_data;
	public GIWKTVertex(GIScriptQueue text)
	{
		m_data = new ArrayList<GIWKTDescription>();
		char current = text.Look();
		while(current != ',' && current != ')')
		{
			GIWKTDescription data = new GIWKTDigit(text);
			m_data.add(data);
			current = text.Look();
		}
	}
	@Override
	public String toString()
	{
		String res = "";
		for(int i = 0; i < m_data.size(); i++)
		{
			res += m_data.get(i).toString();
			if(i < m_data.size() - 1)
			{
				res += " ";
			}
		}
		return res;
	}

}
