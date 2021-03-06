package org.magnum.soda.example;

import java.util.List;

public interface Reports {
	
	public static final String SVC_NAME = "report";
	
	public void addReport(Report r);
	public void modifyReport(Report r);
	public List<Report> getReports();
	
	public void addListener(ReportsListener l);
	public void removeListener(ReportsListener l);
	public List<ReportsListener> getListeners();
}
//12