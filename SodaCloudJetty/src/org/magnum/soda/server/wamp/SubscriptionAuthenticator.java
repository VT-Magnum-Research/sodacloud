/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.server.wamp;

public interface SubscriptionAuthenticator {

	public boolean canSubscribeTo(ClientId c, String topic);
	
}
