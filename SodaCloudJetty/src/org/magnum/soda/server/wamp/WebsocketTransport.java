/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
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

import java.net.URI;

import org.magnum.soda.MsgBus;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.msg.MetaAddress;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.protocol.generic.DefaultProtocol;
import org.magnum.soda.server.wamp.client.EventReceiver;
import org.magnum.soda.server.wamp.messages.EventMessage;
import org.magnum.soda.transport.Address;
import org.magnum.soda.transport.MsgContainer;
import org.magnum.soda.transport.Transport;
import org.magnum.soda.transport.UriAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WebsocketTransport extends Transport {

	
	private static final Logger Log = LoggerFactory
			.getLogger(WebsocketTransport.class);
	
	private ServerPubSubHandler handler_;

	private ObjectMapper marshaller_ = new ObjectMapper();
	
	private UriAddress serverAddress_;

	public WebsocketTransport(MsgBus msgBus, LocalAddress addr, String path, int port) {
		super(new DefaultProtocol(), msgBus, addr);
		handler_ = new ServerPubSubHandler(path, getProtocol().getName(), port);
	}
	
	public WebsocketTransport(Protocol proto, MsgBus msgBus, LocalAddress addr,  String path, int port) {
		super(proto, msgBus, addr);
		handler_ = new ServerPubSubHandler(path, getProtocol().getName(), port);
	}

	@Override
	public void connect(Address addr) {
		try {
			
			EventReceiver rcvr = new EventReceiver() {

				@Override
				public void onReceive(EventMessage evt) {
					try {
						String raw = evt.getRawPayload();
						Log.debug("Srvr Receiving: [{}]",raw);
						MsgContainer m = marshaller_
								.readValue(raw,
										MsgContainer.class);
						receive(m);
					} catch (Exception e) {
						Log.error("Unexpected exception unmarshalling msg",e);
					}
				}
			};
			
			handler_.subscribe(MetaAddress.META_ADDRESS.toString(),
					rcvr);
			
			handler_.subscribe(getMyAddress().toString(),
					rcvr);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void send(MsgContainer msg) {
		try {
			Log.debug("Srvr Sending [{}]",msg.getMsg());
			handler_.publish(msg.getDestination(), msg);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isConnected() {
		return handler_.isConnected();
	}

}
