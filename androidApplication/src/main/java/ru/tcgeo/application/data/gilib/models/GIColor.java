package ru.tcgeo.application.data.gilib.models;

import android.graphics.Color;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class GIColor {

	public String m_description;
	public String m_name;
	public int m_red;
	public int m_green;
	public int m_blue;
	public int m_alpha;

	
	public GIColor(){

	}
	
	public GIColor(String name){
		m_name = name;
		setFromName();
	}

	public String ToString()
	{
        String res = "Color : description=" + m_description + " name=" + m_name + " m_red=" + m_red + " m_green=" + m_green + " m_blue=" + m_blue + " m_alpha=" + m_alpha + "\n";
        return res;
	}
	
	public int Get()
	{
		return Color.argb(m_alpha, m_red, m_green, m_blue);
	}
	
	public void set(int color)
	{
		m_name = "custom";
		m_alpha = Color.alpha(color);
		m_red = Color.red(color);
		m_green = Color.green(color);
		m_blue = Color.blue(color);
	}
	
	public void setFromName()
	{
		if(m_name.equalsIgnoreCase("black"))
		{
			set(Color.BLACK);
		}
		if(m_name.equalsIgnoreCase("blue"))
		{
			set(Color.BLUE);
		}
		if(m_name.equalsIgnoreCase("cyan"))
		{
			set(Color.CYAN);
		}
		if(m_name.equalsIgnoreCase("dkgray"))
		{
			set(Color.DKGRAY);
		}
		if(m_name.equalsIgnoreCase("gray"))
		{
			set(Color.GRAY);
		}
		if(m_name.equalsIgnoreCase("green"))
		{
			set(Color.GREEN);
		}
		if(m_name.equalsIgnoreCase("ltgray"))
		{
			set(Color.LTGRAY);
		}
		if(m_name.equalsIgnoreCase("magenta"))
		{
			set(Color.MAGENTA);
		}
		if(m_name.equalsIgnoreCase("red"))
		{
			set(Color.RED);
		}
		if(m_name.equalsIgnoreCase("transparent"))
		{
			set(Color.TRANSPARENT);
		}
		if(m_name.equalsIgnoreCase("white"))
		{
			set(Color.WHITE);
		}
		if(m_name.equalsIgnoreCase("yellow"))
		{
			set(Color.YELLOW);
		}
		if(m_name.equalsIgnoreCase("clear"))
		{
			m_alpha = 0;
			m_red = 0;
			m_green = 0;
			m_blue = 0;
		}
	}
	
	public XmlSerializer Save(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException
	{
		serializer.startTag("", "Color");
		serializer.attribute("", "description", m_description);
		serializer.attribute("", "name", m_name);
		serializer.attribute("", "r", String.valueOf(m_red));
		serializer.attribute("", "g", String.valueOf(m_green));
		serializer.attribute("", "b", String.valueOf(m_blue));
		serializer.attribute("", "a", String.valueOf(m_alpha));
		serializer.endTag("", "Color");
		return serializer;
	}

	public static class Builder {
		private String description;
		private String name;
		private int red;
		private int green;
		private int blue;
		private int alpha;

		public Builder(){}

		public Builder description(String description){
			this.description = description;
			return this;
		}

		public Builder name(String name){
			this.name = name;
			return this;
		}

		public Builder argb(int alpha, int red, int green, int blue){
			this.alpha = alpha;
			this.red = red;
			this.green = green;
			this.blue = blue;
			return this;
		}

		public GIColor build(){
			GIColor color = new GIColor();
			color.m_description = description;
			if(name != null && !name.isEmpty()){
				color.m_name = name;
				color.setFromName();
			} else {
				color.m_alpha = alpha;
				color.m_red = red;
				color.m_green = green;
				color.m_blue = blue;
			}
			return color;
		}

	}
}
