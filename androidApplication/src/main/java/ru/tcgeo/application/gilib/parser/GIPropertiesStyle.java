package ru.tcgeo.application.gilib.parser;

import java.util.ArrayList;

import ru.tcgeo.application.gilib.models.GIColor;

public class GIPropertiesStyle 
{
	public String m_type;
	public double m_lineWidth;
	public double m_opacity;
	
	public ArrayList<GIColor> m_colors;
	
	public GIPropertiesStyle()
	{
		m_colors = new ArrayList<GIColor>();
	}
	public String ToString()
	{
		String Res = "Style \n";
		Res += "m_type=" + m_type + " m_lineWidth=" + m_lineWidth +  " m_opacity=" + m_opacity;
		for(GIColor clr: m_colors)
		{
			Res +=  clr.ToString() + "\n";
		}
		return Res;
	}

	public static class Builder {
		private String type;
		private double lineWidth;
		private double opacity;
		public ArrayList<GIColor> colors;

		public Builder(){

		}

		public Builder type(String type){
			this.type = type;
			return this;
		}

		public Builder lineWidth(double lineWidth){
			this.lineWidth = lineWidth;
			return this;
		}

		public Builder opacity(double opacity){
			this.opacity = opacity;
			return this;
		}

		public Builder color(GIColor color){
			if(colors == null){
				colors = new ArrayList<GIColor>();
			}
			colors.add(color);
			return this;
		}

		public GIPropertiesStyle build(){
			GIPropertiesStyle style = new GIPropertiesStyle();
			style.m_type = type;
			style.m_lineWidth = lineWidth;
			style.m_opacity = opacity;
			style.m_colors = colors;
			return style;
		}

	}
}
