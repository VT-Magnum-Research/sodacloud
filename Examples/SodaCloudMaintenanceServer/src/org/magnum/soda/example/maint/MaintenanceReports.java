/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.util.List;

public interface MaintenanceReports {

	public static final String SVC_NAME = "maintenance";
	
	public void addReport(MaintenanceReport r);
	public List<MaintenanceReport> getReports();
	public void addListener(MaintenanceListener l);
	public void removeListener(MaintenanceListener l);
	
}
