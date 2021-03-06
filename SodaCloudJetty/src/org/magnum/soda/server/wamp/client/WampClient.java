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
package org.magnum.soda.server.wamp.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.magnum.soda.msg.Msg;
import org.magnum.soda.server.wamp.common.Channel;
import org.magnum.soda.server.wamp.messages.CallErrorMessage;
import org.magnum.soda.server.wamp.messages.CallMessage;
import org.magnum.soda.server.wamp.messages.CallResultMessage;
import org.magnum.soda.server.wamp.messages.EventMessage;
import org.magnum.soda.server.wamp.messages.Message;
import org.magnum.soda.server.wamp.messages.MessageMapper;
import org.magnum.soda.server.wamp.messages.MessageType;
import org.magnum.soda.server.wamp.messages.PublishMessage;
import org.magnum.soda.server.wamp.messages.SubscribeMessage;
import org.magnum.soda.server.wamp.messages.UnsubscribeMessage;
import org.magnum.soda.server.wamp.messages.WelcomeMessage;


public class WampClient {

	public boolean hasBeenWelcomed;
	public String serverIdent;
	public String sessionId;
	protected Channel outgoingChannel;
	Map<String, RpcResultReceiver> rpcResultReceivers;
	Map<String, EventReceiver> eventReceivers;

	public WampClient(Channel outgoingChannel) {
		init();
		this.outgoingChannel = outgoingChannel;
	}

	protected void init() {
		rpcResultReceivers = new HashMap<String, RpcResultReceiver>();
		eventReceivers = new HashMap<String, EventReceiver>();
	}
	
	public void handleIncomingMessage(String jsonText) {
		Message message = MessageMapper.fromJson(jsonText);

		// System.out.println("handleIncomingMessage: " + jsonText);
		if ( message.getType() == MessageType.WELCOME ) {
			handleIncomingWelcomeMessage((WelcomeMessage)message);
			return;
		}

		if ( ! hasBeenWelcomed ) {
			String msg = "handleIncomingMessage: Cannot receive messages until we got a WELCOME message";
			System.err.println(msg);
			throw new IllegalStateException("Client has not been welcomed yet");
		}

		switch(message.getType()) {
		case CALLRESULT:
			handleIncomingCallResultMessage((CallResultMessage)message);
			break;
		case CALLERROR:
			handleIncomingCallErrorMessage((CallErrorMessage)message);
			break;
		case EVENT:
			handleIncomingEventMessage((EventMessage)message);
			break;
		default:
			// TODO logging
			System.err.println("ERROR: handleIncomingMessage doesn't know how to handle message type " + message.getType() );
		}
	}

	protected void handleIncomingWelcomeMessage(WelcomeMessage message) {
		if ( hasBeenWelcomed )
			throw new IllegalStateException("Client has already been welcomed");
		checkNotNull("serverIdent", message.serverIdent);
		checkNotNull("sessionId", message.sessionId);
		serverIdent = message.serverIdent; 
		sessionId = message.sessionId;
		hasBeenWelcomed = true;
	}

	protected void handleIncomingCallResultMessage(CallResultMessage message) {
		RpcResultReceiver receiver = null;
		synchronized(rpcResultReceivers) {
			receiver = rpcResultReceivers.get(message.callId);
		}
		if ( receiver == null ) {
			// TODO logging
			System.err.println("ERROR: handleIncomingCallResultMessage doesn't know a handler for this call " + message.callId);
			return;
		}
		
		receiver.setCallResultMessage(message);
		receiver.onSuccess();
	}

	protected void handleIncomingCallErrorMessage(CallErrorMessage message) {
		RpcResultReceiver receiver = null;
		synchronized(rpcResultReceivers) {
			receiver = rpcResultReceivers.get(message.callId);
		}
		if ( receiver == null ) {
			// TODO logging
			System.err.println("ERROR: handleIncomingCallErrorMessage doesn't know a handler for this call " + message.callId);
			return;
		}
		
		receiver.setCallErrorMessage(message);
		receiver.onError();
	}


	protected void handleIncomingEventMessage(EventMessage message) {
		EventReceiver receiver = null;
		synchronized(eventReceivers) {
			receiver = eventReceivers.get(message.topicUri);
		}
		if ( receiver == null ) {
			// TODO logging
			System.err.println("ERROR: handleIncomingEventMessage doesn't know a handler for this topic " + message.topicUri);
			return;
		}
		receiver.onReceive(message);
	}

	public void call(String procedureId, RpcResultReceiver rpcResultHandler) throws IOException {
		String callId = UUID.randomUUID().toString();
		synchronized(rpcResultReceivers) {
			rpcResultReceivers.put(callId, rpcResultHandler);
		}
		outgoingChannel.handle(MessageMapper.toJson(new CallMessage(callId, procedureId)));
	}

	public <PayloadType> void call(String procedureId, RpcResultReceiver rpcResultHandler, PayloadType payload, Class<PayloadType> payloadType) throws IOException {
		String callId = UUID.randomUUID().toString();
		synchronized(rpcResultReceivers) {
			rpcResultReceivers.put(callId, rpcResultHandler);
		}
		CallMessage msg = new CallMessage(callId, procedureId);
		msg.setPayload(payload, payloadType);
		outgoingChannel.handle(MessageMapper.toJson(msg));
	}

	public void subscribe(String topicId, EventReceiver eventReceiver) throws IOException {
		synchronized (eventReceivers) {
			eventReceivers.put(topicId, eventReceiver);
		}
		outgoingChannel.handle(MessageMapper.toJson(new SubscribeMessage(topicId)));
	}

	public void unsubscribe(String topicId) throws IOException {
		synchronized (eventReceivers) {
			eventReceivers.remove(topicId);
		}
		outgoingChannel.handle(MessageMapper.toJson(new UnsubscribeMessage(topicId)));
	}
	
	public void publish(String topicId, Object payload) throws IOException {
		PublishMessage msg = new PublishMessage(
				topicId);
		msg.setPayload(payload);
		String json = MessageMapper.toJson(msg);
		outgoingChannel.handle(json);
	}


	public Object getServerIdent() {
		return serverIdent;
	}

	public Object getSessionId() {
		return sessionId;
	}

	protected void checkNotNull(String id, String s) {
		if ( s == null )
			throw new IllegalArgumentException(id+" is null");
	}

	public boolean hasBeenWelcomed() {
		return hasBeenWelcomed;
	}

}
