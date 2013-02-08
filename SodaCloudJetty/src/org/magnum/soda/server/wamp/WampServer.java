/*****************************************************************************
 * Copyright 2013 Olivier Croquette <ocroquette@free.fr>                     *
 *                                                                           *
 *  Licensed under the Apache License, Version 2.0 (the "License");          *
 *  you may not use this file except in compliance with the License.         *
 *  You may obtain a copy of the License at                                  *
 *                                                                           *
 *      http://www.apache.org/licenses/LICENSE-2.0                           *
 *                                                                           *
 *  Unless required by applicable law or agreed to in writing, software      *
 *  distributed under the License is distributed on an "AS IS" BASIS,        *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 *  See the License for the specific language governing permissions and      *
 *  limitations under the License.                                           *
 ****************************************************************************/
package org.magnum.soda.server.wamp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.magnum.soda.server.wamp.common.Channel;
import org.magnum.soda.server.wamp.messages.CallMessage;
import org.magnum.soda.server.wamp.messages.EventMessage;
import org.magnum.soda.server.wamp.messages.Message;
import org.magnum.soda.server.wamp.messages.MessageMapper;
import org.magnum.soda.server.wamp.messages.PublishMessage;
import org.magnum.soda.server.wamp.messages.SubscribeMessage;
import org.magnum.soda.server.wamp.messages.UnsubscribeMessage;
import org.magnum.soda.server.wamp.messages.WelcomeMessage;


public class WampServer {

	public WampServer() {
		init();
	}

	public WampServer(String serverIdent) {
		init();
		this.serverIdent =serverIdent; 
	}

	protected void init() {
		outgoingClientChannels = new ConcurrentHashMap<ClientId, Channel>();
		rpcHandlers = new ConcurrentHashMap<String, RpcHandler>();
		serverIdent = "<UNIDENTIFIED SERVER>";
		subscriptions = new Subscriptions();
		clientIdFactory = new ClientIdFactory();
	}

	public ClientId addClient(Channel outgoingChannel) {
		System.out.println("adding client");
		ClientId clientId = clientIdFactory.getNext();
		outgoingClientChannels.put(clientId, outgoingChannel);
		try {
			outgoingChannel.handle(MessageMapper.toJson(new WelcomeMessage(clientId.toString(), serverIdent)));
		} catch (IOException e) {
			// Could not even greet this client. How sad is that?
			return null;
		}
		
		return clientId;
	}

	public void handleIncomingMessage(ClientId clientId, String jsonText) throws IOException {
		Message message = MessageMapper.fromJson(jsonText);
		switch(message.getType()) {
		case CALL:
			handleIncomingCallMessage(clientId, (CallMessage)message);
			break;
		case SUBSCRIBE:
			handleIncomingSubscribeMessage(clientId, (SubscribeMessage)message);
			break;
		case UNSUBSCRIBE:
			handleIncomingUnsubscribeMessage(clientId, (UnsubscribeMessage)message);
			break;
		case PUBLISH:
			handleIncomingPublishMessage(clientId, (PublishMessage)message);
			break;
		default:
			// TODO logging
			System.err.println("ERROR: handleIncomingMessage doesn't know how to handle message type " + message.getType() );
		}
	}

	private void handleIncomingSubscribeMessage(ClientId clientId, SubscribeMessage message) {
		subscriptions.subscribe(clientId, message.topicUri);
	}

	private void handleIncomingUnsubscribeMessage(ClientId clientId, UnsubscribeMessage message) {
		subscriptions.unsubscribe(clientId, message.topicUri);
	}

	private void handleIncomingCallMessage(ClientId clientId, CallMessage message) throws IOException {
		String procedureId = message.procedureId;
		RpcHandler handler= rpcHandlers.get(procedureId);
		if ( handler != null ) {
			RpcCall rpcCall = new RpcCall(message);
			handler.execute(rpcCall);
			sendMessageToClient(clientId, rpcCall.getResultingJson());
		}
		else
			// TODO
			System.out.println("No handler registered for "+procedureId);
	}

	private void handleIncomingPublishMessage(final ClientId clientId, final PublishMessage message) throws IOException {
		final EventMessage eventMessage = new EventMessage(message.topicUri);
		eventMessage.setPayload(message.payload);
		Subscriptions.ActionOnSubscriber action = new Subscriptions.ActionOnSubscriber() {
			@Override
			public void execute(ClientId subscriberClientId) {
				if ( shallSendPublish(message.excludeMe, clientId, subscriberClientId))
					sendMessageToClient(subscriberClientId, MessageMapper.toJson(eventMessage));
			}
		};
		subscriptions.forAllSubscribers(message.topicUri, action);
	}

	private boolean shallSendPublish(Boolean excludeMe, ClientId from, ClientId to) {
		return excludeMe == null || ! excludeMe.booleanValue() || from != to;
	}

	protected void sendMessageToClient(ClientId clientId, String message) {
		Channel channel = outgoingClientChannels.get(clientId);
		if ( channel != null ) {
			try {
				channel.handle(message);
			}
			catch(IOException e) {
				// TODO
				System.out.println("Looks like client " + clientId + "is disconnecting. Discarding.");
				deleteClient(clientId);
			}
		}
		else
			// TODO
			System.out.println("Cannot send to client, client ID unknown: "+clientId);
	}

	private void deleteClient(ClientId clientId) {
		outgoingClientChannels.remove(clientId);
	}


	public void cancelAllSubscriptions(ClientId clientId) {
	}

	public void registerRpcHandler(String procedureId, RpcHandler rpcHandler) {
		rpcHandlers.put(procedureId, rpcHandler);
	}

	protected Map<ClientId, Channel> outgoingClientChannels;
	protected Map<String, RpcHandler> rpcHandlers;
	protected Subscriptions subscriptions;
	protected String serverIdent;
	protected ClientIdFactory clientIdFactory;

}
