package ru.tcgeo.application.wkt;

import java.util.ArrayList;


public class GIWKTBlock extends GIWKTDescription {

	ArrayList<GIWKTDescription> m_points;

	public GIWKTBlock()
	{
		m_points = new ArrayList<GIWKTDescription>();
	}
	@Override
	public String toString()
	{
		String res = "(";
		for(int i = 0; i < m_points.size(); i++)
		{
			res += m_points.get(i).toString();
			if(i < m_points.size() - 1)
			{
				res += ", ";
			}
		}
		res += ")";
		return res;
	}

}
