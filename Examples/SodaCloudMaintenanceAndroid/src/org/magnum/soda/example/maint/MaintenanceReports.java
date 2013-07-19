/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import java.util.List;
import java.util.UUID;

import org.magnum.soda.Callback;
import org.magnum.soda.Soda;
import org.magnum.soda.proxy.SodaAsync;

public interface MaintenanceReports {

	public static final String SVC_NAME = "maintenance";
	
	public void addReport(MaintenanceReport r);
	public void modifyReport(MaintenanceReport r);
	public void deleteReport(UUID id);
	
	public void addFollower(MaintenanceReport r,String u);
	public void removeFollower(MaintenanceReport r,String u);
	

	public List<String> getFollowers(UUID r);
	public List<MaintenanceReport> getFollowing(String user);
	
	public List<MaintenanceReport> getReports();
	public MaintenanceReport getReportsById(UUID id);
	
	public void bindQRContext(Soda s, MaintenanceReport r);
	@SodaAsync
	public void getReports(Callback<List<MaintenanceReport>> callback);
	

	@SodaAsync
	public void getReports(Callback<List<MaintenanceReport>> callback,Soda s,byte[] b);
	
	@SodaAsync
	public void getReports(String username, Callback<List<MaintenanceReport>> callback);
	
	public void addListener(MaintenanceListener l);
	public void removeListener(MaintenanceListener l);
	
	public void addFollowerListener(UUID id,UserListener l);
	public void removeFollowerListener(UUID id,UserListener l);
	
	
	
}
