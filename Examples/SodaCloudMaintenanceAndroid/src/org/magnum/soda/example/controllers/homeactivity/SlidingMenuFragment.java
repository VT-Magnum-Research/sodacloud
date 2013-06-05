package org.magnum.soda.example.controllers.homeactivity;

import org.magnum.soda.example.maint.CreateReportActivity;
import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.maint.SearchByLocationActivity;
import org.magnum.soda.example.maint.SearchByQRActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SlidingMenuFragment extends Fragment {
	
	private HomeActivity mHomeActivity;
	
	private ListView lv;
	private MainMenuListAdapter adapter;
	// store the currently selected menu item
	private View currentedSelectedView;
	// store if this is first time start
	private boolean firstTimeStartup = true;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// init menu view
		View view = inflater.inflate(R.layout.sliding_menu, null);
		
		mHomeActivity = (HomeActivity)(this.getActivity());
		
		// init the main menu options with an adapter
		lv = (ListView) view.findViewById(R.id.menu_list);
		adapter = new MainMenuListAdapter(getActivity());
		adapter.add(new MainMenuItem(getString(R.string.menu_home_screen), 
				R.drawable.ic_menu_home));
		adapter.add(new MainMenuItem(getString(R.string.menu_search_by_location), 
				R.drawable.ic_menu_nearby));
		adapter.add(new MainMenuItem(getString(R.string.menu_search_by_qr), 
				R.drawable.ic_menu_search));	
		adapter.add(new MainMenuItem(getString(R.string.menu_create_report), 
				R.drawable.ic_menu_report));
		lv.setAdapter(adapter);
		
		// config menu item click control
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {								
				switch (position) {
				case 0:
					mHomeActivity.switchContent(HomeActivity.FRAGMENT_HOME);
					break;
				case 1:
					mHomeActivity.switchContent(HomeActivity.FRAGMENT_SEARCH_LOCATION);
					break;
				case 2:
					mHomeActivity.switchContent(HomeActivity.FRAGMENT_SEARCH_QR);
					break;
				case 3:
					mHomeActivity.switchContent(HomeActivity.FRAGMENT_CREATE_REPORT);
					break;
				}
				
				if (firstTimeStartup) {// first time  highlight first row
					currentedSelectedView = lv.getChildAt(0);
			    }
			    firstTimeStartup = false; 
				// make the right highlight for the background color
				if (currentedSelectedView != null) {
					currentedSelectedView.setBackgroundResource(R.color.menu_background_color);
				}
				view.setBackgroundResource(R.color.menu_background_select_color);				
				currentedSelectedView = view;
			}
		});

		return view;
	}	

	private class MainMenuItem {
		public String tag;
		public int iconRes;
		public MainMenuItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	public class MainMenuListAdapter extends ArrayAdapter<MainMenuItem> {

		public MainMenuListAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.sliding_menu_list_row, null);
			}
			
			if (firstTimeStartup && position == 0) {
				convertView.setBackgroundResource(R.color.menu_background_select_color);
	        }
			
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			
			return convertView;
		}
	}

}
