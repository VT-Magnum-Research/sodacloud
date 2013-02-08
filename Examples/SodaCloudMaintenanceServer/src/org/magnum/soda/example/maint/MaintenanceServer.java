/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import org.magnum.soda.Soda;
import org.magnum.soda.server.wamp.ServerSodaLauncher;
import org.magnum.soda.server.wamp.ServerSodaListener;

public class MaintenanceServer implements ServerSodaListener{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSodaLauncher launcher = new ServerSodaLauncher();
		launcher.launch(8081, new MaintenanceServer());
	}

	@Override
	public void started(Soda soda) {
		MaintenanceReportsImpl reports = new MaintenanceReportsImpl();
		soda.bind(reports, MaintenanceReports.SVC_NAME);
	}
	
	

}
