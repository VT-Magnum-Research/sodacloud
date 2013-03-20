package org.magnum.soda.example.maint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SearchByLocationActivity extends Activity implements
		AndroidSodaListener {
	// UI references.
	private Button searchButton;
	private EditText rangeText;
	private ListView searchResultList;
	private ArrayList<HashMap<String, String>> list;
	private Context ctx_ = this;
	private SimpleAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_searchbylocation);

		searchButton = (Button) findViewById(R.id.searchButton);
		rangeText = (EditText) findViewById(R.id.rangeText);
		searchResultList = (ListView) findViewById(R.id.location_listView);

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
				this,
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

		AndroidSoda.init(this, "10.0.1.8", 8081, this);

	}
	private void ReportDetailIntent(String descript){	
    	Intent i =new Intent(this, ReportEditorActivity.class);
    	i.putExtra("description", descript);
    	Log.d("SODA", "ReportDetailIntent");
		startActivity(i);
    }
	@Override
	public void connected(final AndroidSoda s) {
		MaintenanceReports reports = s.get(MaintenanceReports.class,
				MaintenanceReports.SVC_NAME);

		reports.addListener(new MaintenanceListener() {

			@SodaInvokeInUi
			public void reportAdded(MaintenanceReport r) {
				Log.d("SODA", "Maintenance report added: " + r.getContents());

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
				
				//hard coded for now, change to get from server later
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
			for(MaintenanceReport r:reports){
				HashMap<String, String> t = new HashMap<String, String>();
				t.put("itemDescription",r.getContents());
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
}
