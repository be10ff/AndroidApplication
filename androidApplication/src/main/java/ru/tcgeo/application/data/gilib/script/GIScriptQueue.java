package ru.tcgeo.application.gilib.script;

public class GIScriptQueue {

	private String m_string;

	final static boolean IsVal(char c)
	{
		String Vals = "=+-!?><_";
		String tmp = "";
		tmp += c;
		return Vals.contains(tmp);
	}
	public GIScriptQueue(String string)
	{
		m_string = "";
		m_string += string;
	}
	public char Pop()
	{
		if(m_string.length() > 0)
		{
			char res = m_string.charAt(0);
			m_string = m_string.substring(1);
			return res;
		}
		return ' ';
	}
	public char Look()
	{
		if(m_string.length() > 0)
		{
			return m_string.charAt(0);
		}
		else
		{
			return '\0';
		}
	}
	public char Look(int index)
	{
		if((index >= 0) && (index < m_string.length()))
		{
			return m_string.charAt(index);
		}
		else
		{
			return '\0';
		}
	}
	public boolean Empty()
	{
		return (m_string.length() == 0);//|| (m_string.isEmpty()));
	}
	public String toString()
	{
		return m_string;
	}


}
