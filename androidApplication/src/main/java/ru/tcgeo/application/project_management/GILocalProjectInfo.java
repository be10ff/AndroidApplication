//package ru.tcgeo.application.project_management;
//
//import java.io.File;
//import java.util.ArrayList;
//
//import android.os.Environment;
//
//import ru.tcgeo.application.R;
//import ru.tcgeo.application.gilib.parser.GIProjectProperties;
//import ru.tcgeo.application.local_project_management.GIProjectInfo;
//
//
//public class GILocalProjectInfo extends GIProjectInfo
//{
//	public GILocalProjectInfo(String file)
//	{
//		m_file = file;
//		m_project_properties = new GIProjectProperties(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + file);
//		m_id = m_project_properties.m_id;
//		m_name = m_project_properties.m_name;
//	}
//
//	@Override
//	public GIProjectStatus getStatus(GIServer server)
//	{
//		return server.getLocalProjectStatus(this);
//	}
//	@Override
//	public int getStatusImageId() {
//		if(m_status == GIProjectInfo.GIProjectStatus.UPTODATE)
//		{
//			return R.drawable.project_mark;
//		}
//		if(m_status == GIProjectInfo.GIProjectStatus.FAIL)
//		{
//			return R.drawable.project_mark_fail;
//		}
//		if(m_status == GIProjectInfo.GIProjectStatus.STRANGE)
//		{
//			return R.drawable.project_mark_alert;
//		}
//		if(m_status == GIProjectInfo.GIProjectStatus.UNKNOWN)
//		{
//			return R.drawable.project_mark_alert;
//		}
//		return R.drawable.project_mark_alert;
//	}
//	@Override
//	public ArrayList<GIPropertiesPackage> getProjectPackages() {
//		return m_project_properties.m_Entries;
//	}
//}
