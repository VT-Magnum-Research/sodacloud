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

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;
import org.magnum.soda.server.wamp.ClientId;
import org.magnum.soda.server.wamp.WampServer;


public class JettyServer implements OnTextMessage {
	public JettyServer(WampServer wampServer) {
		this.wampServer = wampServer; 
	}

	@Override
	public void onOpen(Connection connection) {
		clientId = wampServer.addClient(new ChannelToConnectionAdapter(connection));
	}

	@Override
	public void onClose(int closeCode, String message) {
	}

	public void onMessage(String data) {
		try {
			wampServer.handleIncomingMessage(clientId, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private WampServer wampServer;
	private ClientId clientId;
}	
