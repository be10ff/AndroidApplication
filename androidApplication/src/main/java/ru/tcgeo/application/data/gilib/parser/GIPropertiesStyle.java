package ru.tcgeo.application.data.gilib.parser;

import java.util.ArrayList;

import ru.tcgeo.application.data.gilib.models.GIColor;

public class GIPropertiesStyle 
{
	public String m_type;
	public double m_lineWidth;
	public double m_opacity;
	
	public ArrayList<GIColor> m_colors;
	
	public GIPropertiesStyle()
	{
        m_type = "";
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
		GIPropertiesStyle source;

		public Builder(){

		}
		Builder(GIPropertiesStyle source){
			this.source = source;
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
			if(source == null){
				source = new GIPropertiesStyle();
			}
			if(type != null) {
				source.m_type = type;
			}
			if(lineWidth != 0) {
				source.m_lineWidth = lineWidth;
			}
			if(opacity != 0) {
				source.m_opacity = opacity;
			}
			if(colors != null) {
				if(source.m_colors == null){
					source.m_colors = colors;
				} else {
					source.m_colors.addAll(colors);
				}
			}
			return source;
		}

	}
}
