/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.util.LinkedList;
import java.util.List;

public class MaintenanceReportsImpl implements MaintenanceReports {

	private List<MaintenanceListener> listeners_ = new LinkedList<MaintenanceListener>();
	private List<MaintenanceReport> reports_ = new LinkedList<MaintenanceReport>();
	
	@Override
	public void addReport(MaintenanceReport r) {
		reports_.add(r);
		
		for(MaintenanceListener l : listeners_){
			l.reportAdded(r);
		}
	}

	@Override
	public List<MaintenanceReport> getReports() {
		return reports_;
	}

	@Override
	public void addListener(MaintenanceListener l) {
		listeners_.add(l);
	}

	@Override
	public void removeListener(MaintenanceListener l) {
		listeners_.remove(l);
	}

}
