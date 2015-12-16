package ru.tcgeo.application.local_project_management;


import java.util.ArrayList;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;


abstract public class GIProjectInfo {

	public boolean m_checked;
	public int m_id;
	public String m_name;
	public String m_file;
	public int m_progress_percents;
	public GIProjectProperties m_project_properties;
//	public ArrayList<GIPropertiesPackage> m_packets;
	public GIProjectInfo()
	{
//		m_packets = new ArrayList<GIPropertiesPackage>();
		m_checked = false;
		m_progress_percents = -1;
		m_project_properties = null;
	}
	
	public GIProjectInfo(int id, String name, String file)
	{
		m_name = name;
		m_id = id;
		m_file = file;
		m_checked = false;
		m_progress_percents = -1;
//		m_packets = new ArrayList<GIPropertiesPackage>();
		m_project_properties = null;
	}
	
//	abstract public ArrayList<GIPropertiesPackage> getProjectPackages();
	
	@Override
	public String toString ()
	{
	    return m_name;
	}
}
