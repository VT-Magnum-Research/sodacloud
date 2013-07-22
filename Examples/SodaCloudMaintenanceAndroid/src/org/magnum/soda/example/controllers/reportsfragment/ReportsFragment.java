package org.magnum.soda.example.controllerss.reportsfragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.Callback;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.android.ctx.SodaQR;
import org.magnum.soda.ctx.ImageContainer;

import org.magnum.soda.example.maint.MaintenanceListener;
import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.MaintenanceReports;
import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.maint.ReportEditorActivity;
import org.magnum.soda.example.maint.ReportParcelable;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;

public class ReportsFragment extends SherlockFragment implements
AndroidSodaListener{
	// host
	private String mHost;
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
		Properties prop = new Properties();

		try {
			InputStream rawResource = getResources().openRawResource(
					R.raw.connection);
			prop.load(rawResource);
			System.out.println("The properties are now loaded");
			System.out.println("properties: " + prop);

			mHost = prop.getProperty("host");
		} catch (IOException e) {
			Log.e("Property File not found", e.getLocalizedMessage());
		}
		
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

		AndroidSoda.init(ctx_, mHost, 8081, asl_);

		return mRootView;
	}
	private void getReports() {
		List<Future> list = new ArrayList<Future>();

		Future<?> Result = AndroidSoda.async(new Runnable() {
			@Override
			@SodaInvokeInUi
			public void run() {
				if (as != null) {

					Log.e("conected", "------------------------------------");
					SharedPreferences sharedPref = ctx_.getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
					String username = sharedPref.getString("username", "no");
					
					MaintenanceReports reportHandle = as.get(
							MaintenanceReports.class,
							MaintenanceReports.SVC_NAME);
					reportHandle
							.getReports(username, new Callback<List<MaintenanceReport>>() {
								//@SodaInvokeInUi
								public void handle(List<MaintenanceReport> arg0) {
									mReportList = arg0;
									//for test purpose. Remove later.
									fillwithReport();
									
									Log.i("ReportsFragment","reportlist size:"+mReportList.size());
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
	private void fillwithReport(){
		MaintenanceReport t1= new MaintenanceReport();
		t1.setTitle("Machine#123 went down.");
		t1.setContents("It doesn't work.");
		t1.setCreatorId("Alice@gmail.com");
		Calendar cal = Calendar.getInstance();
    	Date createTime = cal.getTime();
    	t1.setCreateTime_(createTime);
		mReportList.add(t1);
		
		MaintenanceReport t2= new MaintenanceReport();
		t2.setTitle("Replace the battery.");
		t2.setContents("The battery is replaced.");
		t2.setCreatorId("Bob@gmail.com");
    	t2.setCreateTime_(createTime);
		mReportList.add(t2);
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
			String item = "Title:"+temp.getTitle()+"\n content:"+temp.getContents()+"\n"+temp.getCreatorId()+"   "+ sdf.format(temp.getCreateTime_());
			map.put("itemDescription", item);
			
			sm.put(item,temp);
			mMapList.add(sm);
			mDisplayList.add(map);

		}
		Log.e("size", ":" + mDisplayList.size());

		getActivity().runOnUiThread(new Runnable()
		{

			@Override
			public void run() {
				mAdapter.notifyDataSetInvalidated();//
				mAdapter.notifyDataSetChanged();
			}

		});

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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null) {		
			mAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void connected(final AndroidSoda s) {
		Log.d("ReportsFragment","connected");
		this.as = s;
		getReports();

	}
}