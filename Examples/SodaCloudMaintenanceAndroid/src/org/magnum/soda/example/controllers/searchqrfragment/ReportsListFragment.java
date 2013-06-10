package org.magnum.soda.example.controllers.searchqrfragment;

import java.util.List;

import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class ReportsListFragment extends SherlockFragment {
	private static final String TAG = ReportsListFragment.class.getName();
	
	private View mRootView;
	private ListView mReportsListView;
	private List<MaintenanceReport> mReports;
	private ReportsAdapter mReportsAdapter;
	
	public ReportsListFragment() {
		super();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");
		mRootView = inflater.inflate(R.layout.fragment_list_reports,container, false);
		mReportsListView = (ListView) mRootView.findViewById(R.id.listViewListReports);
		
		mReportsAdapter = new ReportsAdapter(getActivity());
		mReportsListView.setAdapter(mReportsAdapter);
		return mRootView;
	}
	public void setReports(List<MaintenanceReport> reports) {
		Log.d(TAG,"Setting reports");
		mReportsAdapter.update(reports);
		mReportsAdapter.notifyDataSetChanged();
	}

}
