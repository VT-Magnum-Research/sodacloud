package org.magnum.soda.example;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.magnum.soda.Soda;
import org.magnum.soda.protocol.java.NativeJavaProtocol;
import org.magnum.soda.server.wamp.ServerSoda;
import org.magnum.soda.server.wamp.ServerSodaLauncher;
import org.magnum.soda.server.wamp.ServerSodaListener;
import org.magnum.soda.svc.PingSvc;

public class SodaServer implements ServerSodaListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSodaLauncher launcher = new ServerSodaLauncher();
		launcher.launch(new NativeJavaProtocol(), 8081, new SodaServer());

	}

	@Override
	public void started(Soda soda) {
		Report r = new Report("This is a report from server.");
		Reports manager = new ReportsImpl();
		manager.addReport(r);
		soda.bind(manager, Reports.SVC_NAME);


	}

}
