package org.magnum.soda.example.controllers.searchqrfragment;

import java.util.Date;
import java.util.List;

import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ReportsAdapter extends ArrayAdapter<MaintenanceReport> {
	private static final String TAG = ReportsAdapter.class.getName();
	
	private List<MaintenanceReport> mReports;
	private Context mContext;
	
	public ReportsAdapter(Context context) {
		super(context,0);
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG,"getView()");
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View reportView = inflater.inflate(R.layout.report_item_view, parent,false);
		if(mReports != null) {
			MaintenanceReport report = mReports.get(position);
			
			TextView title = (TextView) reportView.findViewById(R.id.textViewReportTitle);
			title.setText(report.getTitle());
			
			TextView content = (TextView) reportView.findViewById(R.id.textViewReportContent);
			content.setText(report.getContents());
			
			TextView location = (TextView) reportView.findViewById(R.id.textViewReportLocation);
			//This should be location. Using date because maintenance report doesn't apparently doesn't have a location attribute
			Date date = report.getCreateTime_();
			if(date != null) {
				java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
				location.setText(dateFormat.format(date));
			}

			
			ImageView image = (ImageView) reportView.findViewById(R.id.imageViewReportImage);
			if(report.getImageData() != null) {
				Bitmap imageBitmap = BitmapFactory.decodeByteArray(report.getImageData() , 0, report.getImageData().length);
				if(imageBitmap != null) {
					image.setImageBitmap(imageBitmap);
				}
			}

		}
		
		return reportView;

	}
	
	@Override
	public int getCount() {
		if(mReports != null) {
			return mReports.size();
		} else {
			return 0;
		}
	}
	public void update(List<MaintenanceReport> reports) {
		Log.d(TAG,"update()");
		mReports = reports;
		
	}


}
