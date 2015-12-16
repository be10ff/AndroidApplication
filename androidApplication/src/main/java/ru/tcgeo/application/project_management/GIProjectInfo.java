//package ru.tcgeo.application.project_management;
//
//
//import java.util.ArrayList;
//
//import ru.tcgeo.application.R;
//import ru.tcgeo.application.gilib.parser.GIProjectProperties;
//
//abstract public class GIProjectInfo {
//
//	public enum GIProjectStatus
//	{
//		UPTODATE, FAIL, STRANGE, UNKNOWN;
//	}
//	public boolean m_checked;
//	public int m_id;
//	public String m_name;
//	public String m_file;
//	public int m_progress_percents;
//	public GIProjectProperties m_project_properties;
//	public GIProjectStatus m_status;
//	public GIPresenter.ProjectInState m_inState;
//	public ArrayList<GIPropertiesPackage> m_packets;
//	public GIProjectInfo ()
//	{
//		m_packets = new ArrayList<GIPropertiesPackage>();
//		m_checked = false;
//		m_progress_percents = -1;
//		m_project_properties = null;
//		m_inState = GIPresenter.ProjectInState.UNKNOWN;
//	}
//
//	public GIProjectInfo (int id, String name, String file, GIProjectStatus status)
//	{
//		m_name = name;
//		m_id = id;
//		m_file = file;
//		m_status = status;
//		m_checked = false;
//		m_progress_percents = -1;
//		m_packets = new ArrayList<GIPropertiesPackage>();
//		m_project_properties = null;
//		m_inState = GIPresenter.ProjectInState.UNKNOWN;
//	}
//
//	public int getStateStringId()
//	{
//		switch (m_inState)
//		{
//			case CHECKING: return R.string.state_checking;
//			case DELETING: return R.string.state_deleting;
//			case DOWNLOADING: return R.string.state_downloading;
//			case UNZIPPING: return R.string.state_unzipping;
//			case UPDATING: return R.string.state_updating;
//			case READY: return R.string.state_ready;
//			case UNKNOWN: return R.string.state_unknown;
//			default: return R.string.state_unknown;
//		}
//	}
//
//	abstract public GIProjectStatus getStatus(GIServer server);
//	abstract public int getStatusImageId();
//	abstract public ArrayList<GIPropertiesPackage> getProjectPackages();
//
//	@Override
//	public String toString ()
//	{
//	    return m_name;
//	}
//}
