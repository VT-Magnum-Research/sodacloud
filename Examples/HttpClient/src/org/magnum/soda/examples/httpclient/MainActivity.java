package org.magnum.soda.examples.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GetTask t = new GetTask();
		t.execute();

	}

	private class GetTask extends AsyncTask<Void, Void, Void> {
		String fromServer = null;

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet("http://10.0.2.2:8080");
			while (true) {
				try {
				    Thread.sleep(1000);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				try {
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

						if(fromServer != null && fromServer.equals("success"))
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

		}

	}

}
