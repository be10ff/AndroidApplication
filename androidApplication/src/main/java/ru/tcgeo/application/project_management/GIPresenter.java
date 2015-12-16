//package ru.tcgeo.application.project_management;
//
//public class GIPresenter {
//
//	private GIServer m_server;
//	private GIProjectManagementDialog m_dialog;
//
//	public enum ListType
//	{
//		LOCAL, REMOTE;
//	}
//	public enum ProjectInState
//	{
//		READY, DOWNLOADING, UPDATING, CHECKING, UNZIPPING, DELETING, UNKNOWN;
//	}
//
//	public GIPresenter( )
//	{
//		m_server = GIServer.Instance();
//		m_dialog = new GIProjectManagementDialog();
//	}
//
//	public GIPresenter(GIServer server)
//	{
//		m_server = server;
//		m_dialog = new GIProjectManagementDialog();
//	}
//	/**
//	 * Диалог создан
//	 * @return
//	 */
//	public boolean isDialogShown()
//	{
//		return (m_dialog.getView() != null);
//
//	}
//	/**
//	 * Crenew both lists
//	 */
//	public void SomethingHappend()
//	{
//
//		if(isDialogShown())
//		{
//			m_dialog.m_project_name.setText(null);
//			m_dialog.m_project_decsiption.setText(null);
//			((ProjectsWorkAdapter)m_dialog.m_remotes.getAdapter()).notifyDataSetChanged();
//			((ProjectsWorkAdapter)m_dialog.m_locals.getAdapter()).notifyDataSetChanged();
//		}
//
//	}
//	/**
//	 * Clearing&fill&call renew both lists
//	 */
//	public void SomethingChanged()
//	{
//		if(isDialogShown())
//		{
//	    	((ProjectsWorkAdapter)m_dialog.m_locals.getAdapter()).clear();
//	    	((ProjectsWorkAdapter)m_dialog.m_locals.getAdapter()).addAll(m_server.m_locals);
//	    	((ProjectsWorkAdapter)m_dialog.m_remotes.getAdapter()).clear();
//	    	((ProjectsWorkAdapter)m_dialog.m_remotes.getAdapter()).addAll(m_server.m_remotes);
//	    	SomethingHappend();
//		}
//	}
//
//	public GIProjectManagementDialog getDialog()
//	{
//		return m_dialog;
//	}
//}
