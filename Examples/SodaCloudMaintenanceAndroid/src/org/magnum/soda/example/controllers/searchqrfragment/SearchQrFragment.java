package org.magnum.soda.example.controllers.searchqrfragment;


import org.magnum.soda.example.controllers.searchlocationfragment.SearchLocationFragment;
import org.magnum.soda.example.maint.R;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class SearchQrFragment extends SherlockFragment {
	
	private View mView;
	
	private static final String TAG = SearchQrFragment.class.getName();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");
		mView = inflater.inflate(R.layout.fragment_search_qr, container, false);
		FragmentTransaction t = this.getFragmentManager().beginTransaction();
		SearchLocationFragment frag = new SearchLocationFragment();
		t.replace(R.id.fragment_search_qr_content_frame, frag);
		t.commit();
		
		setupActionBar();
		return mView;
	}
	
	private void setupActionBar() {
		final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
		bar.hide();
	}

}
