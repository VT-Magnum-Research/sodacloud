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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView status_;
	private Button addButton_;
	public final static int POLLING_INTERVAL = 100; // ms
	public final static int ADD_REPORT = 100;
	private Reports reports = new Reports();
	private ReportsListener listener;
	
	public Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {  
             switch (msg.what) {                	
                  case ADD_REPORT:  
                	   Log.d("SODA","reportAdded:#4 " + System.currentTimeMillis());
                	   status_.setText(msg.getData().getString("content")); 
                       break;   
             }   
             super.handleMessage(msg);   
        }   
   };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		status_ = (TextView) findViewById(R.id.status);
		addButton_ = (Button) findViewById(R.id.button_add);
		addButton_.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("SODA","reportAdded:#1 " + System.currentTimeMillis());
				reports.addReport(new Report("First Report"));
			}
		});

		listener = new ReportsListener();
		reports.addListener(listener);

		new Thread(new CheckUpdatesTask(listener.getID())).start(); 

	}
	public class CheckUpdatesTask implements Runnable {
		String fromServer = null;
		String id = null;

		public CheckUpdatesTask(String id) {
			this.id = id;
		}

		public void run() {
			polling();
		}

		private void polling() {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost request = new HttpPost(Host.hostaddress + "get");

			// polling with an interval of POLLING_INTERVAL ms
			while (true) {
				try {
					Thread.sleep(POLLING_INTERVAL);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				try {
					HttpEntity myEntity = new StringEntity(id);
					request.setEntity(myEntity);

					HttpResponse response = httpclient.execute(request);

					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						BufferedReader rd = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent()));
						fromServer = rd.readLine();					
						
						Log.e("Httpclient", "fromServer: " + fromServer);
						
						if (!fromServer.equals("null")) {
							
							Message message = new Message();
							message.what = MainActivity.ADD_REPORT;
							
							Bundle bundle = new Bundle();
							bundle.putString("Content", fromServer);
							message.setData(bundle);
							
							myHandler.sendMessage(message);
							break; 
						}
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}	

}
//104-4=100