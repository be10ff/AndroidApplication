package ru.tcgeo.application.wkt;


import ru.tcgeo.application.gilib.script.GIScriptQueue;

public class GIWKTDigit extends GIWKTDescription {

	double m_value;
	
	public GIWKTDigit(GIScriptQueue text)
	{
		m_value = 0;
		String literal = "";
		literal += text.Pop();
		char current = text.Look();
		while(Character.isDigit(current)||current == '.')
		{
			literal += text.Pop();
			current = text.Look();
		}
		try
		{
			m_value = Double.valueOf(literal);
		}
		catch(NumberFormatException e)
		{
		}
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(m_value);
		
	}

}
