/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.server.wamp;

import org.magnum.soda.server.wamp.messages.PublishMessage;

public interface WampServerListener {

	public void clientConnected(ClientId client);
	
	public void clientDisconnected(ClientId client);
	
	public void clientSubscribedToTopic(ClientId id, String topic);
	
	public void clientUnSubscribedFromTopic(ClientId id, String topic);
	
}
