//package ru.tcgeo.application.project_management;
//
//import android.app.DialogFragment;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import ru.tcgeo.application.R;
//import ru.tcgeo.application.local_project_management.GILocalProjectInfo;
//import ru.tcgeo.application.local_project_management.GIProjectInfo;
//
//
//public class GIProjectManagementDialog extends DialogFragment //implements OnClickListener //DialogFragment
//{
//	private GIServer m_server;
//	TextView m_project_decsiption;
//	TextView m_project_name;
//	ListView m_locals;
//	ListView m_remotes;
//	ProgressBar m_progressStatus;
//	ProgressBar m_progressLoad;
//	LinearLayout m_lists_panel;
//	LinearLayout m_root;
//	TextView m_percents;
//	TextView m_server_status;
//	Button m_button_load;
//	Button m_button_delete;
//	Button m_button_update;
//	Button m_button_close;
//	TextView m_job;
//
//	final String LOG_TAG = "LOG_TAG";
//
//	@Override
//	public void onStart() {
//	  super.onStart();
//	  if (getDialog() == null) {
//	    return;
//	  }
//	  Log.d(LOG_TAG, "Dialog: onStart");
//	  int dialogWidth = 800; // specify a value here
//	  int dialogHeight = 600; // specify a value here
//
//	  getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
//
//		m_server = GIServer.Instance();
//
//		ProjectsWorkAdapter local_adapter = new ProjectsWorkAdapter(getDialog().getContext(), R.layout.project_list_selected_item, R.id.project_list_item_path); //project_list_item
//		local_adapter.m_project_name = m_project_name;
//		local_adapter.m_project_decsiption = m_project_decsiption;
//		local_adapter.m_Type = GIPresenter.ListType.LOCAL;
//		local_adapter.clear();
//		local_adapter.addAll(m_server.m_locals);
//        m_locals.setAdapter(local_adapter);
//
//        ProjectsWorkAdapter remote_adapter = new ProjectsWorkAdapter(getDialog().getContext(), R.layout.project_list_selected_item, R.id.project_list_item_path);
//        remote_adapter.m_project_name = m_project_name;
//        remote_adapter.m_project_decsiption = m_project_decsiption;
//        remote_adapter.m_Type = GIPresenter.ListType.REMOTE;
//        remote_adapter.clear();
//        remote_adapter.addAll(m_server.m_remotes);
//        m_remotes.setAdapter(remote_adapter);
//	}
//
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		getDialog().setTitle("Управление проектами");
//		View v = inflater.inflate(R.layout.project_management_dialog, container);
//		m_locals = (ListView)v.findViewById(R.id.local_projects_list);
//		m_remotes = (ListView)v.findViewById(R.id.remote_projects_list);
//		m_project_decsiption = (TextView)v.findViewById(R.id.textView_project_info);
//		m_project_name = (TextView)v.findViewById(R.id.textView_project_name);
//		m_remotes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//		m_lists_panel = (LinearLayout)v.findViewById(R.id.linearLayout1);
//		m_root = (LinearLayout)v.findViewById(R.id.dialog_root);
//		m_server_status = (TextView)v.findViewById(R.id.textViewServer);
//		m_button_load = (Button)v.findViewById(R.id.buttonLoad);
//		m_button_delete = (Button)v.findViewById(R.id.buttonDelete);
//		m_button_update = (Button)v.findViewById(R.id.buttonUpdate);
//		m_button_close = (Button)v.findViewById(R.id.buttonClose);
//		m_button_load.setOnClickListener(new OnClickListener() {
//			public void onClick(View v)
//			{
//				GIProjectInfo proj = null;
//				for(GIProjectInfo curr : m_server.m_remotes)
//				{
//					if(curr.m_checked == true)
//					{
//						proj = curr;
//						break;
//					}
//				}
//				if(proj != null)
//				{
//					m_server.getProject(proj);
//					m_server.getProjectPackages(proj);
//				}
//			}
//		});
//		m_button_update.setOnClickListener(new OnClickListener() {
//			public void onClick(View v)
//			{
//				GIProjectInfo proj = null;
//				for(GIProjectInfo curr : m_server.m_remotes)
//				{
//					if(curr.m_checked == true)
//					{
//						proj = curr;
//						break;
//					}
//				}
//				if(proj != null)
//				{
//					m_server.getProjectPackages(proj);
//				}
//			}
//		});
//		m_button_delete.setOnClickListener(new OnClickListener() {
//			public void onClick(View v)
//			{
//				GILocalProjectInfo proj = null;
//				for(GIProjectInfo curr : m_server.m_locals)
//				{
//					if(curr.m_checked == true)
//					{
//						proj = (GILocalProjectInfo)curr;
//						break;
//					}
//				}
//				if(proj != null)
//				{
//					m_server.DeleteLocalProject(proj);
//				}
//			}
//		});
//
//		m_button_close.setOnClickListener(new OnClickListener() {
//			public void onClick(View v)
//			{
//				dismiss();
//			}
//		});
//
//		m_locals.setOnItemClickListener(new OnItemClickListener()
//		{
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
//			{
//				GILocalProjectInfo item = (GILocalProjectInfo) m_server.getAt(GIPresenter.ListType.LOCAL, arg2);
//				if(item == null)
//				{
//					return;
//				}
//				m_button_load.setEnabled(false);
//				m_button_delete.setEnabled(true);
//				if(item.m_status == GIProjectInfo.GIProjectStatus.UPTODATE)
//				{
//					m_button_update.setEnabled(false);
//				}
//				else
//				{
//					m_button_update.setEnabled(true);
//				}
//				for(GIProjectInfo curr : m_server.m_locals)
//				{
//					curr.m_checked = false;
//				}
//				for(GIProjectInfo curr : m_server.m_remotes)
//				{
//					curr.m_checked = false;
//				}
//				item.m_checked = true;
//				((ProjectsWorkAdapter)m_locals.getAdapter()).notifyDataSetChanged();
//				((ProjectsWorkAdapter)m_remotes.getAdapter()).notifyDataSetChanged();
//			}
//		});
//		m_remotes.setOnItemClickListener(new OnItemClickListener()
//		{
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
//			{
//				GIProjectInfo item = (GIProjectInfo) m_server.getAt(GIPresenter.ListType.REMOTE, arg2);
//				if(item.m_status == GIProjectInfo.GIProjectStatus.UPTODATE)
//				{
//					m_button_load.setEnabled(false);
//				}
//				else
//				{
//					m_button_load.setEnabled(true);
//				}
//				m_button_delete.setEnabled(false);
//				m_button_update.setEnabled(false);
//				for(GIProjectInfo curr : m_server.m_locals)
//				{
//					curr.m_checked = false;
//				}
//				for(GIProjectInfo curr : m_server.m_remotes)
//				{
//					curr.m_checked = false;
//				}
//				item.m_checked = true;
//				m_server.getProjectInfo(item);
//				m_server.ReadProject(item);
//				((ProjectsWorkAdapter)m_locals.getAdapter()).notifyDataSetChanged();
//				((ProjectsWorkAdapter)m_remotes.getAdapter()).notifyDataSetChanged();
//			}
//		});
//		m_locals.setEnabled(true);
//		return v;
//	}
//
//	public void onDismiss(DialogInterface dialog)
//	{
//		super.onDismiss(dialog);
//		Log.d(LOG_TAG, "Dialog 1: onDismiss");
//	}
//
//	public void onCancel(DialogInterface dialog)
//	{
//		super.onCancel(dialog);
//		Log.d(LOG_TAG, "Dialog 1: onCancel");
//	}
//
//}
