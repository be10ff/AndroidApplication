package ru.tcgeo.application.local_project_management;


import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.tcgeo.application.R;

/**/
public class LocalProjectsWorkAdapter extends ArrayAdapter<GIProjectInfo>
{
	private final Context m_context;
	public TextView m_project_decsiption;
	public TextView m_project_name;



	class ViewHolder //static
	{
		public TextView m_TextViewName;
		public TextView m_TextViewPath;
		public TextView m_TextViewPackages;
		public ImageView m_ImageViewStatus;
		public ProgressBar m_ProgressBar;
		public TextView m_TextViewProgress;
	}
	@Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
		final GIProjectInfo item = getItem(position);
		View v = LayoutInflater.from(getContext()).inflate(R.layout.project_list_selected_item, null);
		ViewHolder viewHolder = new ViewHolder();

		//
		viewHolder.m_TextViewPackages = (TextView)v.findViewById(R.id.textViewPackages);
		viewHolder.m_ProgressBar = (ProgressBar)v.findViewById(R.id.progressBarLoadProject);
		viewHolder.m_TextViewProgress =(TextView)v.findViewById(R.id.textViewItemProgress);
		viewHolder.m_TextViewName = (TextView)v.findViewById(R.id.project_list_item_name);
		viewHolder.m_TextViewPath  = (TextView)v.findViewById(R.id.project_list_item_path);
		viewHolder.m_ImageViewStatus = (ImageView)v.findViewById(R.id.imageView_project_status);
		//
		if(item != null)
		{
			if(item.m_checked)
			{
				v.setBackgroundColor(m_context.getResources().getColor(R.color.Item_Background_Selected));
				m_project_name .setText(item.m_name);
				if(item.m_project_properties != null)
				{
					m_project_decsiption.setText(item.m_project_properties.m_decription);
				}
				else
				{
					m_project_decsiption.setText(null);
				}

				viewHolder.m_TextViewPackages.setVisibility(View.VISIBLE);

				if(item.m_progress_percents >= 0)
				{
					viewHolder.m_ProgressBar.setVisibility(View.VISIBLE);
					viewHolder.m_ProgressBar.setProgress(item.m_progress_percents);
					viewHolder.m_TextViewProgress.setVisibility(View.VISIBLE);
					viewHolder.m_TextViewProgress.setText(item.m_progress_percents + "%");
				}
				else
				{
					viewHolder.m_ProgressBar.setVisibility(View.GONE);
					viewHolder.m_ProgressBar.setProgress(0);
					viewHolder.m_TextViewProgress.setVisibility(View.GONE);
					viewHolder.m_TextViewProgress.setText(null);
				}
			}
			else
			{
				v.setBackgroundColor(m_context.getResources().getColor(R.color.Item_Background_Unselected));
				viewHolder.m_ProgressBar.setVisibility(View.GONE);
				viewHolder.m_TextViewPackages.setVisibility(View.GONE);
				viewHolder.m_TextViewProgress.setVisibility(View.GONE);
			}
			viewHolder.m_TextViewName.setText(item.m_name);
			viewHolder.m_TextViewPath.setText(item.m_file);
		}
		return v;
    }
	public LocalProjectsWorkAdapter(Context context, int resource, int textViewResourceId)
    {
        super(context, resource, textViewResourceId);
        m_context = context;
    }

}