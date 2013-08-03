package org.magnum.soda.examples.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView status_;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		status_ = (TextView) findViewById(R.id.status);
		
		Reports reports = new Reports();
		ReportsListener listener = new ReportsListener();
		reports.addListener(listener);
		 
		reports.addReport(new Report("First Report"));
		
		CheckUpdateTask t = new CheckUpdateTask(listener.getID());
		t.execute();
		
		
				

	}

	private class CheckUpdateTask extends AsyncTask<Void, Void, Void> {
		String fromServer = null;
		String id;
		public CheckUpdateTask(String listenerId){
			id = listenerId;
		}
		@Override
		protected Void doInBackground(Void... params) {
			Log.d("httpclient", "CheckUpdateTask:" + id);
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost request = new HttpPost("http://10.0.2.2:8080/get");
			
			// polling with an interval of 1000ms
			while (true) {
				try {  
					Thread.sleep(1000);
				} catch (InterruptedException ex) {  
					Thread.currentThread().interrupt();
				}
				try {
					HttpEntity myEntity = new StringEntity(id);
					request.setEntity(myEntity);
					
					HttpResponse response = httpclient.execute(request);
					Log.d("httpclient", "status code: "
							+ response.getStatusLine().getStatusCode());
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						BufferedReader rd = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						while ((fromServer = rd.readLine()) != null) {
							Log.e("Httpclient", "response: " + fromServer);
						}

						if (fromServer != null )
							break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			status_.setText(fromServer);
		}

	}

}
