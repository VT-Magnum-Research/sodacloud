package org.magnum.soda.example.controllers.reportsfragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.Callback;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.example.maint.LoginActivity;
import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.MaintenanceReports;
import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.maint.ReportEditorActivity;
import org.magnum.soda.example.maint.ReportParcelable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockFragment;

public class ReportsFragment extends SherlockFragment implements
AndroidSodaListener{
	private static final String TAG = "ReportsFragment";	
	// UI references.
	private ListView searchResultList;
	private static SimpleAdapter mAdapter;
	private List<MaintenanceReport> mReportList = new ArrayList<MaintenanceReport>();
	private List<HashMap<String, String>> mDisplayList = new ArrayList<HashMap<String, String>>();

	private List<HashMap<String, MaintenanceReport>> mMapList = new ArrayList<HashMap<String, MaintenanceReport>>();


	Context ctx_ ;
	private AndroidSodaListener asl_ = null;

	private AndroidSoda as = null;

	private View mRootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		asl_ = this;
		ctx_ = this.getActivity();
		mRootView = inflater.inflate(R.layout.fragment_reports,container,false);
		searchResultList = (ListView) mRootView.findViewById(R.id.ListView_myreport);

		mAdapter = new SimpleAdapter(
				this.getActivity(),
				mDisplayList,// data source
				R.layout.listview_item_nocheckbox,
				new String[] { "itemDescription" },
				new int[] { R.id.item_description });
		mAdapter.notifyDataSetChanged();
		searchResultList.setAdapter(mAdapter);
		searchResultList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) searchResultList
						.getItemAtPosition(position);
				String des = map.get("itemDescription");
				ReportDetailIntent(des);
			}
		});
		AndroidSoda.init(ctx_, LoginActivity.mHost, 8081, asl_);
		
		
		return mRootView;
	}
	@Override
	public void onResume(){
		super.onResume();
		Log.i(TAG,"onResume");
		getReports();
	}
	
	private void ReportDetailIntent(String descript) {
		Intent i = new Intent(this.getActivity(), ReportEditorActivity.class);
		i.putExtra("description", descript);
		Iterator<HashMap<String, MaintenanceReport>> itr=mMapList.iterator();
		while(itr.hasNext())
		{
			HashMap<String,MaintenanceReport> m=itr.next();
			if(m.containsKey(descript))
			{
				i.putExtra("mReport", new ReportParcelable(m.get(descript)));
				break;
			}
		}
		startActivity(i);
	}
	
	private void getReports() {
		List<Future> list = new ArrayList<Future>();

		Future<?> Result = AndroidSoda.async(new Runnable() {
			@Override
			@SodaInvokeInUi
			public void run() {
				if (as != null) {

					Log.d(TAG, "conected");
					SharedPreferences sharedPref = ctx_.getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
					String username = sharedPref.getString("username", "no");
					Log.d(TAG, "username " + username);
					MaintenanceReports reportHandle = as.get(
							MaintenanceReports.class,
							MaintenanceReports.SVC_NAME);
					reportHandle
							.getReports(username, new Callback<List<MaintenanceReport>>() {
								//@SodaInvokeInUi
								public void handle(List<MaintenanceReport> arg0) {
									mReportList = arg0;
									Log.i(TAG,"Download reportlist size:"+mReportList.size());
									populateList();
								}
							});					

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

		mDisplayList.clear();
		mMapList.clear();
		Iterator<MaintenanceReport> itr = mReportList.iterator();

		while (itr.hasNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			HashMap<String,MaintenanceReport> sm=new HashMap<String,MaintenanceReport>();
			MaintenanceReport temp = ((MaintenanceReport) itr.next());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
			String item = "Title:"+temp.getTitle()+"\ncontent:"+temp.getContents()+"\n"
						  +temp.getCreatorId()+"   "+ sdf.format(temp.getCreateTime_())+"\n";
			map.put("itemDescription", item);

			sm.put(item,temp);
			mMapList.add(sm);
			mDisplayList.add(map);

		}
		Log.e(TAG, "displayList.size():" + mDisplayList.size());

		getActivity().runOnUiThread(new Runnable()
		{

			@Override
			public void run() {
				mAdapter.notifyDataSetInvalidated();//
				mAdapter.notifyDataSetChanged();
			}

		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null) {		
			
		}

	}

	@Override
	public void connected(final AndroidSoda s) {
		Log.d(TAG,"connected");
		this.as = s;
		getReports();

	}
}