/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import org.magnum.soda.proxy.SodaAsync;

public interface UserListener {

	@SodaAsync
	public void userAdded(User u);
	
	@SodaAsync
	public void notifyFollowers(MaintenanceReport u);
	
}
