package ru.tcgeo.application.local_project_management;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

import ru.tcgeo.application.R;
import ru.tcgeo.application.gilib.parser.GIProjectProperties;
//import ru.tcgeo.application.project_management.GIPropertiesPackage;
//import ru.tcgeo.application.project_management.GIServer;


public class GILocalProjectInfo extends GIProjectInfo
{
	public GILocalProjectInfo(String file)
	{
		m_file = file;
		m_project_properties = new GIProjectProperties(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + file);
		m_id = m_project_properties.m_id;
		m_name = m_project_properties.m_name;
	}

//	@Override
//	public ArrayList<GIPropertiesPackage> getProjectPackages() {
//		return m_project_properties.m_Entries;
//	}
}
