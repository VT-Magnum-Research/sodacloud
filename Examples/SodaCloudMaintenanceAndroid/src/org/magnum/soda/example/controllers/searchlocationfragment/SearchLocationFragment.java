package org.magnum.soda.example.controllers.searchlocationfragment;

import org.magnum.soda.example.maint.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class SearchLocationFragment extends SherlockFragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setupActionBar();
		return inflater.inflate(R.layout.fragment_search_location, container,false);
	}
	
	private void setupActionBar() {
		final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
		bar.hide();
	}

}
