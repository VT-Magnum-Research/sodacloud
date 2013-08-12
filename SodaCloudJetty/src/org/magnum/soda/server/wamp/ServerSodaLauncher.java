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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.protocol.generic.DefaultProtocol;
import org.magnum.soda.server.wamp.adapters.jetty.JettyServerHandler;
import org.magnum.soda.svc.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerSodaLauncher {

	
	private static final Logger Log = LoggerFactory
			.getLogger(ServerSodaLauncher.class);
	
	public static void main(String[] args){
		ServerSodaLauncher server = new ServerSodaLauncher();
		server.launch(8081, null);
	}
	
	public void launch(int tcpPort, ServerSodaListener l) {
		launch(new DefaultProtocol(), tcpPort, l);
	}
	
	public void launch(Protocol protoc, int tcpPort, ServerSodaListener l) {
		launch(protoc,tcpPort,l,new Subscriptions());
	}
	
	public void launch(final Protocol protoc, int tcpPort, final ServerSodaListener l, Subscriptions subs) {
		launch(protoc, tcpPort, l, AuthService.NO_AUTH_SVC, subs);
	}
	
	public void launch(final Protocol protoc, int tcpPort, ServerSodaListener l, AuthService auth, Subscriptions subs) {
	   ServerConfig config = new ServerConfig();
	   config.setProtocol(protoc);
	   config.setPort(tcpPort);
	   config.setSubscriptions(subs);
	   config.setAuthService(auth);
	   launch(config,l);
	}
	
	public void launch(final ServerConfig config, final ServerSodaListener l){
		final Server jettyServer = new Server(config.getPort());

		final WampServer wampocServer = new WampServer();
		SourceHonestyFilter filter = new SourceHonestyFilter(config.getProtocol());
		wampocServer.addListener(filter);
		wampocServer.addPublishFilter(filter);
		wampocServer.setSubscriptions(config.getSubscriptions());
		JettyServerHandler webSocketHandler = new JettyServerHandler(
				wampocServer);
		webSocketHandler.setHandler(new DefaultHandler());
		
		config.configure(jettyServer, webSocketHandler);

		Log.info("Starting the Soda server on TCP port: " + config.getPort());
		try {
			jettyServer.start();
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ServerSoda soda = new ServerSoda(config.getProtocol(), config.getAuthService(), config.getPort());
					wampocServer.addListener(soda);
					soda.setServer(jettyServer);
					soda.connect(null);
					if(l != null){
						l.started(soda);
					}
				}
			});
			t.start();
			jettyServer.join();
		} catch (Exception e) {
			Log.error("Failed to start the Soda server:",e);
		}
	}


}
