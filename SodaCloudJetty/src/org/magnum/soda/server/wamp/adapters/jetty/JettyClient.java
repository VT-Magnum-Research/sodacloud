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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.magnum.soda.server.wamp.client.WampClient;


public class JettyClient {

	private JettyClientAdapter jettyClientAdapter;
	private WebSocket.Connection connection;
	
	public void connect(String path, String protocol, int port) throws Exception {

		WebSocketClientFactory factory = new WebSocketClientFactory();
		try {
			factory.start();
		} catch (Exception e) {
			throw new Exception("Failed to start the WebSocketClientFactory");
		}

		WebSocketClient client = factory.newWebSocketClient();
		client.setProtocol(protocol);

		jettyClientAdapter = new JettyClientAdapter();
		connection = client.open(new URI("ws://"+getHost()+":"+port+path), jettyClientAdapter).get(5, TimeUnit.SECONDS);
	}

	public WampClient getWampClient() {
		return jettyClientAdapter.getWampClient();
	}

	public boolean isConnected(){
		return connection != null && connection.isOpen();
	}
	
	/**
	 * @return
	 * 
	 * Use this function if you want to run the server with local ip addess(not the 
	 */
	private String getHost()
	{
		String result=null;
		try {
			Enumeration<NetworkInterface> interfaces;
			
				interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()){
			    NetworkInterface current = interfaces.nextElement();
			    if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
			    Enumeration<InetAddress> addresses = current.getInetAddresses();
			    while (addresses.hasMoreElements()){
			        InetAddress current_addr = addresses.nextElement();
			        if (current_addr.isLoopbackAddress()|| current_addr.getHostAddress().contains(":")) continue;
			        result=current_addr.getHostAddress();
			    }
			}
		
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
}
