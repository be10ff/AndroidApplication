package ru.tcgeo.application.gilib.parser;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.models.GIColor;
//import ru.tcgeo.application.gilib.models.GIIcon;


public class GIPropertiesStyle 
{
	public String m_type;
	public double m_lineWidth;
	public double m_opacity;
	
//	public GIIcon m_icon;
	public ArrayList<GIColor> m_colors;
	
	public GIPropertiesStyle()
	{
		/*m_type = "";
		m_lineWidth = 0;
		m_opacity = 0.0;
		m_icon = new IconProperties();*/
		m_colors = new ArrayList<GIColor>();
	}
	public String ToString()
	{
		String Res = "Style \n";
		Res += "m_type=" + m_type + " m_lineWidth=" + m_lineWidth +  " m_opacity=" + m_opacity;
//		if(m_icon != null)
//		{
//		Res += "m_icon=" + m_icon.ToString() + "\n";
//		}
		for(GIColor clr: m_colors)
		{
			Res +=  clr.ToString() + "\n";
		}
		return Res;
	}	
}
