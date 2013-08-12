/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.server.wamp;

public class AuthenticatedSubscriptions extends Subscriptions {

	private SubscriptionAuthenticator authenticator_;

	public AuthenticatedSubscriptions(SubscriptionAuthenticator authenticator) {
		super();
		authenticator_ = authenticator;
	}

	@Override
	public void subscribe(ClientId clientId, String topicUri) {
		if (canSubscribe(clientId, topicUri)) {
			super.subscribe(clientId, topicUri);
		}
	}

	@Override
	public void unsubscribe(ClientId clientId, String topicUri) {
		if (canSubscribe(clientId, topicUri)) {
			super.unsubscribe(clientId, topicUri);
		}
	}

	public boolean canSubscribe(ClientId clientId, String topicUri) {
		return authenticator_ == null
				|| authenticator_.canSubscribeTo(clientId, topicUri);
	}

}
