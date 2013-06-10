/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.example.maint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.magnum.soda.Callback;
import org.magnum.soda.Soda;
import org.magnum.soda.SodaQuery;
import org.magnum.soda.ctx.SodaQR;
import org.magnum.soda.proxy.SodaAsync;

public class MaintenanceReportsImpl implements MaintenanceReports {

	private List<MaintenanceListener> listeners_ = new LinkedList<MaintenanceListener>();
	private List<MaintenanceReport> reports_ = new LinkedList<MaintenanceReport>();

	private Map<UUID, List<String>> followers_ = new HashMap<UUID, List<String>>();
	private Map<UUID, List<UserListener>> followerlisteners_ = new HashMap<UUID, List<UserListener>>();

	@Override
	public void addReport(MaintenanceReport r) {

		System.out.println("content :" + r.getContents() + " :"
				+ r.getCreatorId());
		reports_.add(r);

		for (MaintenanceListener l : listeners_) {
			l.reportAdded(r);
		}
	}

	@Override
	public void deleteReport(UUID id) {

		Iterator<MaintenanceReport> it = reports_.iterator();
		while (it.hasNext()) {
			if (it.next().getId().equals(id)) {
			
				reports_.remove(it.next());
				break;
			}
		}
		
		if(followerlisteners_.containsKey(id))
		{
		for (UserListener l : followerlisteners_.get(id)) {
			//l.notifyFollowers(r);
		}
		followerlisteners_.remove(id);		
		}

	}

	public void bindQRContext(Soda s, MaintenanceReport r) {
		SodaQR qr = SodaQR.create(r.getContents());
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
	public void getReports(Callback<List<MaintenanceReport>> callback, Soda s,
			byte[] b) {

		SodaQR _objQR = SodaQR.fromImageData(b);
		SodaQuery<MaintenanceReport> _objSQ = s.find(MaintenanceReport.class,
				_objQR);

		callback.handle(_objSQ.getList_());

	}

	@Override
	@SodaAsync
	public void getReports(String username,
			Callback<List<MaintenanceReport>> callback) {

		List<MaintenanceReport> queryresult = new LinkedList<MaintenanceReport>();
		Iterator<MaintenanceReport> itr = reports_.iterator();
		while (itr.hasNext()) {
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
		boolean success = false;
		System.out.println("content :" + r.getContents() + " :"
				+ r.getCreatorId());

		Iterator<MaintenanceReport> it = reports_.iterator();
		while (it.hasNext()) {
			MaintenanceReport temp = it.next();
			if (temp.getId().equals(r.getId())) {
				temp.setImageData(r.getImageData());
				temp.setContents(r.getContents());
				success = true;
				break;
			}
		}
		if (success) {
			for (MaintenanceListener l : listeners_) {
				l.reportchanged(r);

			}
			if(followerlisteners_.containsKey(r.getId()))
			{
			for (UserListener l : followerlisteners_.get(r.getId())) {
				l.notifyFollowers(r);
			}
			}
		}
	}

	@Override
	public void addFollower(MaintenanceReport r, String u) {
		// TODO Auto-generated method stub
		List<String> lusr = null;
		if (followers_.containsKey(r.getId())) {
			lusr = followers_.get(r.getId());
		} else {
			lusr = new ArrayList<String>();
		}
		lusr.add(u);
		followers_.put(r.getId(), lusr);
		if (followerlisteners_.containsKey(r.getId())) {
			for (UserListener l : followerlisteners_.get(r.getId())) {
				l.notifyFollowers(r);
			}
		}
	}

	@Override
	public void removeFollower(MaintenanceReport r, String u) {
		// TODO Auto-generated method stub
		
		followers_.remove(r.getId());
		if(followers_.containsKey(r.getId()))
		{
			List<String> lusr=followers_.get(r.getId());
			if(lusr.contains(u))
				lusr.remove(u);
		}
		if (followerlisteners_.containsKey(r.getId())) {
			for (UserListener l : followerlisteners_.get(r.getId())) {
				l.notifyFollowers(r);
			}
		}
	}

	@Override
	public void addFollowerListener(UUID id, UserListener l) {
		// TODO Auto-generated method stub
		List<UserListener> lusr = null;
		if (followerlisteners_.containsKey(id)) {
			lusr = followerlisteners_.get(id);
		} else {
			lusr = new ArrayList<UserListener>();
		}
		lusr.add(l);
		followerlisteners_.put(id, lusr);

	}

	@Override
	public void removeFollowerListener(UUID id, UserListener l) {
		// TODO Auto-generated method stub
		if (followerlisteners_.containsKey(id)) {
			List<UserListener> lu = followerlisteners_.get(id);
			//Iterator itr=lu.iterator();
			if (lu.contains(l))
				lu.remove(l);
			
		}
	}

	@Override
	public List<String> getFollowers(UUID r) {
		// TODO Auto-generated method stub
		if(followers_.containsKey(r))
			return followers_.get(r);
		
		return null;
	}

	@Override
	public List<MaintenanceReport> getFollowing(String user) {
		// TODO Auto-generated method stub
		List<MaintenanceReport> result=new ArrayList<MaintenanceReport>();
		Iterator<UUID> itr=followers_.keySet().iterator();
		while(itr.hasNext())
		{
			UUID i=itr.next();
			Iterator<String> users=followers_.get(i).iterator();
			while(users.hasNext())
			{
				if(users.equals(user))
					result.add(getReportsById(i));
			}
		}
		
		
		return null;
	}

	@Override
	public MaintenanceReport getReportsById(UUID id) {
		// TODO Auto-generated method stub
		MaintenanceReport result=null;
		Iterator<MaintenanceReport> rep=reports_.iterator();
		while(rep.hasNext())
		{
			result=rep.next();
			if(result.getId().equals(id))
				return result;
				
		}
		return null;
	}

}
