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
package org.magnum.soda.server.wamp.adapters.jetty;

import org.eclipse.jetty.websocket.WebSocket;
import org.magnum.soda.server.wamp.client.WampClient;


public class JettyClientAdapter implements WebSocket.OnTextMessage {
	@Override
	public void onOpen(Connection connection) {
		client = new WampClient(new ChannelToConnectionAdapter(connection));
		System.out.println("JettyClientAdapter:onOpen " + client);
	}

	@Override
	public void onClose(int closeCode, String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessage(String data) {
		client.handleIncomingMessage(data);
	}
	
	public WampClient getWampClient() {
		return client;
	}

	private WampClient client;

}

