/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.server.wamp;

import org.magnum.soda.server.wamp.messages.PublishMessage;
import org.magnum.soda.server.wamp.messages.SubscribeMessage;
import org.magnum.soda.server.wamp.messages.UnsubscribeMessage;

public interface PubSubFilter {

	public PublishMessage filterPublish(ClientId from, PublishMessage msg);
	
	public SubscribeMessage filterSubscribe(ClientId id, SubscribeMessage sub);
	
	public UnsubscribeMessage filterUnsubscribe(ClientId id, UnsubscribeMessage unsub);
}
