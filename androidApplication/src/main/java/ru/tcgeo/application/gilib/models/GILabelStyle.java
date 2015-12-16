package ru.tcgeo.application.gilib.models;


import ru.tcgeo.application.gilib.models.GIColor;

public class GILabelStyle
{
	public boolean m_shadow;
	public int m_fontSize;
	public String m_layout;
	public GIColor m_Color;
	/*LabelStyleProperties()
	{
		m_shadow = false;
		m_fontSize = 0;
		m_layout = "";
		m_Color = new ColorProperties();
	}*/
	public String ToString()
	{
		String Res = "LabelStyle \n";
		Res += "m_shadow=" + m_shadow + " m_fontSize=" + m_fontSize +  " m_layout=" + m_layout ;
		if(m_Color != null)
		{
			Res += "m_Color=" + m_Color.ToString() + "\n";
		}
		return Res;
	}	
}
