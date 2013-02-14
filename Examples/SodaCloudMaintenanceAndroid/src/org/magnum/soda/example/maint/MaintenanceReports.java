/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.util.List;

import org.magnum.soda.Callback;
import org.magnum.soda.proxy.SodaAsync;

public interface MaintenanceReports {

	public static final String SVC_NAME = "maintenance";
	
	public void addReport(MaintenanceReport r);
	public List<MaintenanceReport> getReports();
	
	@SodaAsync
	public void getReports(Callback<List<MaintenanceReport>> callback);
	public void addListener(MaintenanceListener l);
	public void removeListener(MaintenanceListener l);
	
}
