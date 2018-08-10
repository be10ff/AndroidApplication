package ru.tcgeo.application.views.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.tcgeo.application.R;
import ru.tcgeo.application.view.IFolderItemListener;
import ru.tcgeo.application.views.fragment.CreateAdditionalLayerFragment;
import ru.tcgeo.application.views.fragment.OpenFileFragment;
import ru.tcgeo.application.views.fragment.OpenSDFileFragment;

public class OpenFileDialog extends DialogFragment {

	@BindView(R.id.tabs)
	TabLayout tabs;

	@BindView(R.id.vpViewPager)
	ViewPager pager;

    IFolderItemListener folderListener;

    private SampleFragmentPagerAdapter adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInctanceState)
	{
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	    View v = inflater.inflate(R.layout.open_filedialog_layout, null);
        ButterKnife.bind(this, v);
		setupViewPager();
		return v;
	}

    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        this.folderListener = folderItemListener;
    }

    private void setupViewPager() {
        adapter = new SampleFragmentPagerAdapter(getChildFragmentManager());

        OpenFileFragment openFileFragment = new OpenFileFragment();
        openFileFragment.setIFolderItemListener(folderListener);
        adapter.addFragment(openFileFragment, getActivity().getString(R.string.add_from_file));
        OpenSDFileFragment openSDFileFragment = new OpenSDFileFragment();
        openSDFileFragment.setIFolderItemListener(folderListener);
        adapter.addFragment(openSDFileFragment, getActivity().getString(R.string.add_from_sd_file));
        CreateAdditionalLayerFragment createAdditionalLayerFragment = new CreateAdditionalLayerFragment();
        createAdditionalLayerFragment.setIFolderItemListener(folderListener);
        adapter.addFragment(createAdditionalLayerFragment, getActivity().getString(R.string.add_additional_layer));
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }


	public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
		private List<android.app.Fragment> fragments = new ArrayList<>();
		private List<String> titles = new ArrayList<>();

		public SampleFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

        @Override
        public android.app.Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles.get(position);
		}

		public void addFragment(android.app.Fragment fragment, String title) {
			fragments.add(fragment);
			titles.add(title);
		}
	}

}
