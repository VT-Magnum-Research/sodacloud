package org.magnum.soda.example.controllerss.reportsfragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;
import org.magnum.soda.android.ctx.SodaQR;
import org.magnum.soda.ctx.ImageContainer;
import org.magnum.soda.example.maint.CreateReportActivity;
import org.magnum.soda.example.maint.MaintenanceListener;
import org.magnum.soda.example.maint.MaintenanceReport;
import org.magnum.soda.example.maint.MaintenanceReports;
import org.magnum.soda.example.maint.R;
import org.magnum.soda.example.maint.CreateReportActivity.MyLocationListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class ReportsFragment extends SherlockFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_reports,container,false);
	}
}
