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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.magnum.soda.msg.MetaAddress;
import org.magnum.soda.msg.Msg;
import org.magnum.soda.server.wamp.adapters.jetty.JettyClient;
import org.magnum.soda.server.wamp.adapters.jetty.JettyServerHandler;
import org.magnum.soda.server.wamp.client.EventReceiver;
import org.magnum.soda.server.wamp.client.WampClient;
import org.magnum.soda.server.wamp.common.Channel;
import org.magnum.soda.server.wamp.messages.EventMessage;
import org.magnum.soda.server.wamp.messages.MessageMapper;
import org.magnum.soda.server.wamp.messages.PublishMessage;
import org.magnum.soda.transport.MsgContainer;


public class JettyBasedServer {

	public static void main(String[] args) {
		int tcpPort = 8081;

		Server jettyServer = new Server(tcpPort);

		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		jettyServer.setHandler(context);

		WampServer wampocServer = new WampServer();
		JettyServerHandler webSocketHandler = new JettyServerHandler(
				wampocServer);
		webSocketHandler.setHandler(new DefaultHandler());
		jettyServer.setHandler(webSocketHandler);

		System.err.println("Starting the WS server on TCP port: " + tcpPort);
		try {
			jettyServer.start();
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ServerSoda soda = new ServerSoda(8081);
					soda.connect(null);
				}
			});
			t.start();
			jettyServer.join();
		} catch (Exception e) {
			System.err.println("Failed to start the WS server:\n" + e);
		}
	}


}
