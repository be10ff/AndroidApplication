package ru.tcgeo.application.data.wkt;

import ru.tcgeo.application.data.gilib.script.GIScriptQueue;

public class GIWKTType extends GIWKTDescription {
	String m_type; 
	
	public GIWKTType(GIScriptQueue text)
	{
		String literal = "";
		literal += text.Pop();
		char current = text.Look();
		while(Character.isLetter(current) || Character.isDigit(current) || current == '.'|| current == '-')
		{
			literal += text.Pop();
			current = text.Look();
		}
		m_type = literal;
	}
	@Override
	public String toString()
	{
		return m_type;
		
	}

}
