package org.magnum.soda.example.controllers.searchlocationfragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.maint.ReportEditorActivity;
import org.magnum.soda.example.maint.ReportParcelable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
		mRootView = inflater.inflate(R.layout.fragment_list_reports, container,
				false);
		mReportsListView = (ListView) mRootView
				.findViewById(R.id.listViewListReports);
		mReportsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "onItemClicked: " + position);
				if(mReports == null) {
					Log.d(TAG, "mReports was null");
					return;
				}
				launchReportEditor(mReports.get(position));

			}
		});

		mReportsAdapter = new ReportsAdapter(getActivity());
		mReportsListView.setAdapter(mReportsAdapter);
		return mRootView;
	}

	public void setReports(List<MaintenanceReport> reports) {
		Log.d(TAG, "Setting reports");
		mReports = reports;
		getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	mReportsAdapter.update(mReports);
            	mReportsAdapter.notifyDataSetChanged();
            }
        });
	}

	private void launchReportEditor(MaintenanceReport report) {
		Log.d(TAG,"launcing report editor");
		Intent i = new Intent(this.getActivity(), ReportEditorActivity.class);
		i.putExtra("mReport", new ReportParcelable(report));
		startActivity(i);
	}

}
