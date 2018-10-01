package ru.tcgeo.application.wkt;

public class GIDBaseField 
{
	public int m_id;
	public String m_name;
	public String m_description;
	public int m_type;
	public int m_sub_type;
	public int m_order;
	public String m_default_value;
	public Object m_value;
	public GIDBaseField() 
	{
		m_id = 0;
		m_name = "";
		m_description = "";
		m_type = 0;
		m_sub_type = 0;
		m_order = 0;
		m_default_value = "";
		m_value = null;
		
	}
	
	public GIDBaseField(int  id, String name, String description, int type, int sub_type, int order, String default_value, Object value) 
	{
		m_id = 0;
		m_name = "";
		m_description = "";
		m_type = 0;
		m_sub_type = 0;
		m_order = 0;
		m_default_value = "";
		m_value = value;
	}
	public GIDBaseField(GIDBaseField temp)
	{
		m_id = 0;
		m_name = new String(temp.m_name);
		m_description  = new String(temp.m_description);
		m_type = 0;
		m_sub_type = 0;
		m_order = 0;
		m_default_value = new String(temp.m_default_value);
		m_value = new String(temp.m_default_value);
	}
}
