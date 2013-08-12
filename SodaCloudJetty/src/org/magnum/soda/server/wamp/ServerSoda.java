/*****************************************************************************
 * Copyright [2013] [Jules White]                                            *
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.magnum.soda.Soda;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.protocol.generic.DefaultProtocol;
import org.magnum.soda.svc.AuthService;
import org.magnum.soda.svc.PingSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSoda extends Soda implements WampServerListener {
	
	private static final Logger Log = LoggerFactory.getLogger(ServerSoda.class);

	private Server server_;
	
	private List<ClientId> clients_ = new ArrayList<ClientId>();

	public ServerSoda(int port) {
		this(new DefaultProtocol(), AuthService.NO_AUTH_SVC, port);
	}

	public ServerSoda(Protocol protoc, AuthService auth, int port) {
		super(true);
		setTransport(new WebsocketTransport(protoc, getMsgBus(),
				getLocalAddress(), port));

		bind(auth,AuthService.SVC_NAME);
		
		bind(new PingSvc() {

			@Override
			public void ping() {
				Log.debug("The server recv'd a ping");
			}

			public void ping(PingMsg msg) {
				for (int i = 0; i < msg.getTimes(); i++) {
					Log.debug("Ping: [{}] from [{}]", msg.getMsg(),
							msg.getFrom());
				}
			}

			@Override
			public void pingMe(PingSvc me) {
				if (me != null) {
					me.ping();
				}
			}

		}, PingSvc.SVC_NAME);
	}

	public Server getServer() {
		return server_;
	}

	public void setServer(Server Server) {
		server_ = Server;
	}

	public void stop() {
		try {
			server_.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void clientConnected(ClientId client) {
		clients_.add(client);
	}

	@Override
	public void clientDisconnected(ClientId client) {
		clients_.remove(client);
	}

	@Override
	public void clientSubscribedToTopic(ClientId id, String topic) {
		
	}

	@Override
	public void clientUnSubscribedFromTopic(ClientId id, String topic) {
		
	}
}
