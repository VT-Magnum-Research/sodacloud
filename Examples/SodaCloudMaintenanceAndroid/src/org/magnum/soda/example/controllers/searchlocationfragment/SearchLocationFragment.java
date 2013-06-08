package org.magnum.soda.example.controllers.searchlocationfragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.Callback;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.MaintenanceReports;
import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.maint.ReportEditorActivity;
import org.magnum.soda.example.maint.SearchByLocationActivity.GetReportListTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;

public class SearchLocationFragment extends SherlockFragment implements
		AndroidSodaListener {
	// UI references.
	private Button searchButton;
	private EditText rangeText;
	private ListView searchResultList;
	private List<MaintenanceReport> mReportList = new ArrayList<MaintenanceReport>();
	private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private Context ctx_;
	private SimpleAdapter mAdapter;

	private AndroidSodaListener asl_ = null;

	private AndroidSoda as = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ctx_ = this.getActivity();
		
		setupActionBar();

		View rootView = inflater.inflate(R.layout.activity_searchbylocation,
				container, false);
		// setContentView(R.layout.activity_searchbylocation);

		searchButton = (Button) rootView.findViewById(R.id.searchButton);
		rangeText = (EditText) rootView.findViewById(R.id.rangeText);
		searchResultList = (ListView) rootView
				.findViewById(R.id.location_listView);

		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				double range = Double.valueOf(rangeText.getText().toString());

				AndroidSoda.async(new Runnable() {
					@Override
					public void run() {
						GetReportListTask t = new GetReportListTask(ctx_);
						t.execute((Void) null);
					}
				});
			}
		});

		list = new ArrayList<HashMap<String, String>>();
		// instantiate customized MyAdapter
		mAdapter = new SimpleAdapter(
				this.getActivity(),
				list,// data source
				R.layout.listview_item_nocheckbox,
				new String[] { "itemDescription" },
				new int[] { R.id.item_description });
		// map the layout part in xml to element in HashMap
		// bind Adapter
		searchResultList.setAdapter(mAdapter);
		// bind listView's listener
		searchResultList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				HashMap<String, String> map = (HashMap<String, String>) searchResultList
						.getItemAtPosition(position);
				String des = map.get("itemDescription");
				ReportDetailIntent(des);
			}

		});

		AndroidSoda.init(this.getActivity(), "10.0.1.8", 8081, this);
		
		return rootView;

	}

	private void ReportDetailIntent(String descript) {
		Intent i = new Intent(this.getActivity(), ReportEditorActivity.class);
		i.putExtra("description", descript);
		Log.d("SODA", "ReportDetailIntent");
		startActivity(i);
	}

	@Override
	public void connected(AndroidSoda s) {

		this.as = s;
		getReports();

	}

	private void getReports() {
		List<Future> list = new ArrayList<Future>();

		Future<?> Result = AndroidSoda.async(new Runnable() {
			@Override
			@SodaInvokeInUi
			public void run() {
				if (as != null) {

					Log.e("conected", "------------------------------------");

					MaintenanceReports reportHandle = as.get(
							MaintenanceReports.class,
							MaintenanceReports.SVC_NAME);
					reportHandle
							.getReports(new Callback<List<MaintenanceReport>>() {
								// @SodaInvokeInUi
								public void handle(List<MaintenanceReport> arg0) {
									mReportList = arg0;
									populateList();
								}
							});
					Log.e("obtained", "------------------------------------");

				}
			}
		});

		list.add(Result);
		for (Future f : list) {
			try {
				while (!f.isDone()) {

				}
				f.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void populateList() {
		Iterator<MaintenanceReport> itr = mReportList.iterator();

		while (itr.hasNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			MaintenanceReport temp = ((MaintenanceReport) itr.next());
			Log.e("-- items--", temp.getCreatorId());
			map.put("itemDescription", temp.getContents());
			list.add(map);

		}
		Log.e("size", ":" + list.size());

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mAdapter.notifyDataSetInvalidated();//
				mAdapter.notifyDataSetChanged();
			}

		});

	}

	/**
	 * Represents an asynchronous task used to get report list from server
	 */
	public class GetReportListTask extends AsyncTask<Void, Void, Boolean> {
		private Context mcontext;
		List<MaintenanceReport> reports = new ArrayList<MaintenanceReport>();

		public GetReportListTask(Context context) {
			mcontext = context;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				// Simulate network access.
				Thread.sleep(2000);

				// hard coded for now, change to get from server later
				for (int i = 0; i < 3; i++) {
					MaintenanceReport r = new MaintenanceReport();
					r.setContents("Report Id:" + i);
					reports.add(r);
				}

			} catch (InterruptedException e) {
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			for (MaintenanceReport r : reports) {
				HashMap<String, String> t = new HashMap<String, String>();
				t.put("itemDescription", r.getContents());
				list.add(t);
			}
			dataChanged();
		}

		@Override
		protected void onCancelled() {

		}
	}

	// update listview
	private void dataChanged() {
		mAdapter.notifyDataSetChanged();
	}
	
	private void setupActionBar() {
		final com.actionbarsherlock.app.ActionBar bar = getSherlockActivity().getSupportActionBar();
		bar.hide();
	}
}