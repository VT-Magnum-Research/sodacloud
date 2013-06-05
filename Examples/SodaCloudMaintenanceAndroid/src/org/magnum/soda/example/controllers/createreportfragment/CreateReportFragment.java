package org.magnum.soda.example.controllers.createreportfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class CreateReportFragment extends SherlockFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setupActionBar();
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private void setupActionBar() {
		final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
		bar.hide();
	}

}
