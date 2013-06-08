/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.magnum.soda.Callback;
import org.magnum.soda.Soda;
import org.magnum.soda.SodaQuery;
import org.magnum.soda.ctx.SodaQR;
import org.magnum.soda.proxy.SodaAsync;

public class MaintenanceReportsImpl implements MaintenanceReports {

	private List<MaintenanceListener> listeners_ = new LinkedList<MaintenanceListener>();
	private List<MaintenanceReport> reports_ = new LinkedList<MaintenanceReport>();
	
	private List<User> followers_ = new LinkedList<User>();
	
	@Override
	public void addReport(MaintenanceReport r) {
		
		System.out.println("content :"+r.getContents()+" :"+r.getCreatorId());
		reports_.add(r);
				
		for(MaintenanceListener l : listeners_){
			l.reportAdded(r);
		}
	}
	
	@Override
	public void deleteReport(UUID id) {

		Iterator<MaintenanceReport> it=reports_.iterator();
		while(it.hasNext())
		{
			if(it.next().getId().equals(id))
			{
			reports_.remove(it.next());
			break;
			}
		}
		
	}

	public void bindQRContext(Soda s, MaintenanceReport r)
	{
		SodaQR qr=SodaQR.create(r.getContents());
		s.bind(r).to(qr);
	}

	@Override
	@SodaAsync
	public void getReports(Callback<List<MaintenanceReport>> callback) {
		callback.handle(reports_);
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

	@Override
	@SodaAsync
	public void getReports(Callback<List<MaintenanceReport>> callback,Soda s, byte[] b) {
		
		SodaQR _objQR=	SodaQR.fromImageData(b);
		SodaQuery<MaintenanceReport> _objSQ=s.find(MaintenanceReport.class,_objQR);
	
		callback.handle(_objSQ.getList_());	
		
	}
	
	@Override
	@SodaAsync
	public void getReports(String username, Callback<List<MaintenanceReport>> callback) {
		
		List<MaintenanceReport> queryresult = new LinkedList<MaintenanceReport>();
		Iterator<MaintenanceReport> itr = reports_.iterator();
		while(itr.hasNext()){
			MaintenanceReport mreport = itr.next();
			if (mreport.getCreatorId().equals(username)) {
				queryresult.add(mreport);
			}
		}
	
		callback.handle(queryresult);	
		
	}

	@Override
	public void modifyReport(MaintenanceReport r) {
		// TODO Auto-generated method stub
		boolean success=false;
		System.out.println("content :"+r.getContents()+" :"+r.getCreatorId());
		
		Iterator<MaintenanceReport> it=reports_.iterator();
		while(it.hasNext())
		{
			MaintenanceReport temp =it.next();
			if(temp.getId().equals(r.getId()))
			{
			temp.setImageData(r.getImageData());
			temp.setContents(r.getContents());
			success=true;
			break;
			}
		}
		if(success)
		{
		for(MaintenanceListener l : listeners_){
			l.reportchanged(r);
		
		}
		}
	}

	@Override
	public void addFollower(User u) {
		// TODO Auto-generated method stub
	/*	followers_.add(u);
		for(User  ur : followers_)
		{
			
		for(UserListener  l : ){
			l.notifyFollowers(u);
		
		}
		}*/
	}

}
