package org.magnum.soda.example;

import java.util.LinkedList;
import java.util.List;

import org.magnum.soda.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportsImpl implements Reports {

	private static final Logger Log = LoggerFactory.getLogger(ReportsImpl.class);
	
	private List<ReportsListener> listeners_ = new LinkedList<ReportsListener>();
	private List<Report> reports_ = new LinkedList<Report>();

	public void addReport(Report r) {
		System.err.println("addReport #2: " + System.currentTimeMillis());
		reports_.add(r);
		
		for(ReportsListener l : listeners_){
			System.err.println("Before reportAdded in server#3: " + System.currentTimeMillis());
			l.reportAdded(r);
		}
	}
	public void modifyReport(Report r){		
		for(Report m : reports_){
			if(m.getContent().equals(r.getContent()))
				m.setContent(r.getContent());
		}
		
		for(ReportsListener l : listeners_){
			l.reportchanged(r);
		}
	}

	public List<Report> getReports() {
		return reports_;
	}

	@Override
	public void addListener(ReportsListener l) {
		listeners_.add(l);
	}

	@Override
	public void removeListener(ReportsListener l) {
		listeners_.remove(l);
	}

	public List<ReportsListener> getListeners(){
		return listeners_;
	}
}
//44