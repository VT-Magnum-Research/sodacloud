/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.util.List;

import org.magnum.soda.Callback;
import org.magnum.soda.Soda;
import org.magnum.soda.proxy.SodaAsync;

public interface MaintenanceReports {

	public static final String SVC_NAME = "maintenance";
	
	public void addReport(MaintenanceReport r);
	public void modifyReport(MaintenanceReport r);
	
	public List<MaintenanceReport> getReports();
	
	public void bindQRContext(Soda s, MaintenanceReport r);
	@SodaAsync
	public void getReports(Callback<List<MaintenanceReport>> callback);
	

	@SodaAsync
	public void getReports(Callback<List<MaintenanceReport>> callback,Soda s,byte[] b);
	
	
	public void addListener(MaintenanceListener l);
	public void removeListener(MaintenanceListener l);
	
}
