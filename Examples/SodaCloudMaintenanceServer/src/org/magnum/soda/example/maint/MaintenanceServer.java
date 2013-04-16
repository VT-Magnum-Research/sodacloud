/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.example.maint;

import java.util.Iterator;

import org.magnum.soda.Soda;
import org.magnum.soda.server.wamp.ServerSoda;
import org.magnum.soda.server.wamp.ServerSodaLauncher;
import org.magnum.soda.server.wamp.ServerSodaListener;
import org.magnum.soda.svc.PingSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaintenanceServer implements ServerSodaListener {

	private static final Logger Log = LoggerFactory
			.getLogger(MaintenanceServer.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSodaLauncher launcher = new ServerSodaLauncher();
		launcher.launch(8081, new MaintenanceServer());

	}

	@Override
	public void started(Soda soda) {
		MaintenanceReport r = new MaintenanceReport();
		r.setId(11);
		r.setContents("first");
		r.setCreatorId("aks");
		MaintenanceReports reports = new MaintenanceReportsImpl();
		reports.addReport(r);
		soda.bind(reports, MaintenanceReports.SVC_NAME);
		
		User u1 = new User();
		u1.setUsername_("alice@gmail.com");
		u1.setPwd_("aaaaa");
		u1.setName_("Alice");
		u1.setPhone_("540-200-1234");
		User u2 = new User();
		u2.setName_("Bob");
		u2.setPhone_("540-200-5678");
		Users userManager = new UsersImpl();
		userManager.addUser(u1);
		//userManager.addUser(u2);
		soda.bind(userManager, Users.SVC_NAME);

		PingSvc p = soda.get(PingSvc.class, "ping");

		Log.error("--------------------------");
		p.ping();
		Log.error("---" + soda.getLocalAddress().toString());

	}

}
