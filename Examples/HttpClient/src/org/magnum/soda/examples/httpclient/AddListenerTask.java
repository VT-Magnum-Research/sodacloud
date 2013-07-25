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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.util.Log;


public class AddListenerTask extends AsyncTask<Void, Void, Void> {
	String fromServer = null;
	String id = null;
	
	public AddListenerTask(String listenerID){
		id = listenerID;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("httpclient", "addlistenerTask id:" + id);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost("http://10.0.2.2:8080/addlistener/");

		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter("id", id);
		request.setParams(httpParams);
   
		
			try {
				HttpEntity myEntity = new StringEntity(id);
				request.setEntity(myEntity);
				
				Log.d("httpclient", "before execute");
				HttpResponse response = httpclient.execute(request);
				Log.d("httpclient", "status code: "
						+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					while ((fromServer = rd.readLine()) != null) {
						Log.d("Httpclient", "addlistenerTask response: " + fromServer);
					}
					
				
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {

	}
}