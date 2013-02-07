package org.magnum.comms.android;

import java.util.UUID;

import org.magnum.comms.msg.Msg;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import de.tavendo.autobahn.Autobahn;
import de.tavendo.autobahn.AutobahnConnection;

public class MagComm extends Activity {

	private static final String TAG = MagComm.class.getName();

	private String me_ = UUID.randomUUID().toString();

	private final AutobahnConnection mConnection = new AutobahnConnection();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mag_comm);

		final String wsuri = "ws://10.0.1.8:8081";

		mConnection.connect(wsuri, new Autobahn.SessionHandler() {

			@Override
			public void onOpen() {
				testPubSub();
			}

			@Override
			public void onClose(int code, String reason) {
				Log.e(TAG, reason);
			}
		});
	}

	private void testPubSub() {

		mConnection.subscribe("http://example.com/events#myevent1", Msg.class,
				new Autobahn.EventHandler() {

					@Override
					public void onEvent(String topic, Object event) {
						Msg m = (Msg) event;
						if (!me_.equals(m.getSource())) {
							m.setPayload("asdf");
							m.setSource(me_);
							Log.d(TAG, "" + event);
							mConnection.publish(
									"http://example.com/events#myevent1", m);
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_mag_comm, menu);
		return true;
	}

}
