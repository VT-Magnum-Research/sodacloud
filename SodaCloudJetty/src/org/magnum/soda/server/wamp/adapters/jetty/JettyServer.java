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
import java.lang.reflect.Field;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;
import org.eclipse.jetty.websocket.WebSocketConnectionRFC6455;
import org.magnum.soda.server.wamp.ClientId;
import org.magnum.soda.server.wamp.WampServer;


public class JettyServer implements OnTextMessage {
	
	WebSocket.FrameConnection _conn;
	
	public JettyServer(WampServer wampServer) {
		this.wampServer = wampServer; 
	}

	@Override
	public void onOpen(Connection connection) {

		System.out.println("JettyServer: "+"Open Connection");
		if(connection instanceof WebSocket.FrameConnection)
		{
		this._conn=(WebSocket.FrameConnection)connection;
		}
		/*
		 * This is a temporary code to increase the timeout for a single endpoint created by 
		 * Jetty server.
		 * TO change the timeout input a number in ms in the below shown
		 * setMaxIdleTime(xx);
		 */
		try {
			 Field this$0 = _conn.getClass().getDeclaredField("this$0");
			 this$0.setAccessible(true);
			 WebSocketConnectionRFC6455 w=(WebSocketConnectionRFC6455)this$0.get(_conn);
			 System.out.println("JettyServer: "+"Current Timeout"+w.getEndPoint().getMaxIdleTime());
			 w.getEndPoint().setMaxIdleTime(3600000);
			 System.out.println("JettyServer: "+"Timeout Set"+w.getEndPoint().getMaxIdleTime());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clientId = wampServer.addClient(new ChannelToConnectionAdapter(connection));
	}

	@Override
	public void onClose(int closeCode, String message) {
		System.out.println("close connection");
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
