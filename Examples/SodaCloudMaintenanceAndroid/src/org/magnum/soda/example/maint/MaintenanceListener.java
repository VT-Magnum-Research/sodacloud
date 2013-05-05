/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.example.maint;

import org.magnum.soda.proxy.SodaAsync;

public interface MaintenanceListener {

	@SodaAsync
	public void reportAdded(MaintenanceReport r);
	@SodaAsync
	public void reportchanged(MaintenanceReport r);
	
}
