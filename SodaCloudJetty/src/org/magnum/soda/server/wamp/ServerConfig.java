/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.server.wamp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.protocol.generic.DefaultProtocol;
import org.magnum.soda.server.wamp.adapters.jetty.JettyServerHandler;
import org.magnum.soda.svc.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConfig {

	
	private static final Logger Log = LoggerFactory
			.getLogger(ServerConfig.class);
	
	private Protocol protocol_ = new DefaultProtocol();
	private int port_ = 8081;
	private AuthService authService_ = AuthService.NO_AUTH_SVC;
	private Subscriptions subscriptions_ = new Subscriptions();
	
	private JettyConfigurer configurer_ = new JettyConfigurer() {
		
		@Override
		public void configure(Server s, JettyServerHandler hdlr) {
			s.setHandler(hdlr);
		}
	};

	public Protocol getProtocol() {
		return protocol_;
	}

	public void setProtocol(Protocol protocol) {
		protocol_ = protocol;
	}

	public int getPort() {
		return port_;
	}

	public void setPort(int port) {
		port_ = port;
	}

	public AuthService getAuthService() {
		return authService_;
	}

	public void setAuthService(AuthService authService) {
		authService_ = authService;
	}

	public Subscriptions getSubscriptions() {
		return subscriptions_;
	}

	public void setSubscriptions(Subscriptions subscriptions) {
		subscriptions_ = subscriptions;
	}

	public void configure(Server jettyServer, JettyServerHandler sodahdlr){
		configurer_.configure(jettyServer, sodahdlr);
	}
}
