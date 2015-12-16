package ru.tcgeo.application.local_project_management;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import ru.tcgeo.application.R;

public class GILocalProjectManagementDialog extends DialogFragment
{
	public ArrayList<GIProjectInfo> mProjects;

	TextView m_project_decsiption;
	TextView m_project_name;
	ListView m_locals;



	LinearLayout m_lists_panel;
	LinearLayout m_root;

	Button m_button_load;
	Button m_button_delete;
	Button m_button_close;


	final String LOG_TAG = "LOG_TAG";
	
	@Override
	public void onStart() {
	  super.onStart();
	  if (getDialog() == null) {
	    return;
	  }
	  Log.d(LOG_TAG, "Dialog: onStart");
	  int dialogWidth = 800; // specify a value here
	  int dialogHeight = 600; // specify a value here

	  getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
	  
		LocalProjectsWorkAdapter local_adapter = new LocalProjectsWorkAdapter(getDialog().getContext(), R.layout.project_list_selected_item, R.id.project_list_item_path);
		local_adapter.m_project_name = m_project_name;
		local_adapter.m_project_decsiption = m_project_decsiption;
		local_adapter.clear();
		AddLocalProjects();
		local_adapter.addAll(mProjects);
        m_locals.setAdapter(local_adapter);
   
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		getDialog().setTitle(R.string.project_management);
		View v = inflater.inflate(R.layout.local_project_management_dialog, container);
		m_locals = (ListView)v.findViewById(R.id.local_projects_list);
		m_project_decsiption = (TextView)v.findViewById(R.id.textView_project_info);
		m_lists_panel = (LinearLayout)v.findViewById(R.id.linearLayout1);
		m_root = (LinearLayout)v.findViewById(R.id.dialog_root);
		m_button_close = (Button)v.findViewById(R.id.buttonClose);



		m_button_close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
		
		m_locals.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				GILocalProjectInfo item = (GILocalProjectInfo) mProjects.get(arg2);
				if (item == null) {
					return;
				}
				m_button_load.setEnabled(false);
				m_button_delete.setEnabled(true);

				for (GIProjectInfo curr : mProjects) {
					curr.m_checked = false;
				}

				item.m_checked = true;
				((LocalProjectsWorkAdapter) m_locals.getAdapter()).notifyDataSetChanged();
			}
		});

		m_locals.setEnabled(true);
		return v;
	}


	public void AddLocalProjects ()
	{
		File dir = (Environment.getExternalStorageDirectory());
		mProjects.clear();
		for(File file : dir.listFiles())
		{
			if(file.isFile())
			{
				if(file.getName().endsWith(".pro"))
				{
					String file_name = file.getName();
					if(file_name!= null)
					{
						GILocalProjectInfo proj = new GILocalProjectInfo(file_name);
//						proj.m_packets = proj.m_project_properties.m_Entries;
						mProjects.add(proj);
					}
				}
			}
		}

	}
}
