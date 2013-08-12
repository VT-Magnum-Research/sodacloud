/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.server.wamp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.protocol.generic.DefaultProtocol;
import org.magnum.soda.server.wamp.adapters.jetty.JettyServer;
import org.magnum.soda.server.wamp.adapters.jetty.JettyServerHandler;
import org.magnum.soda.svc.AuthService;

public abstract class WebsocketServletAdapter extends HttpServlet {
	private static WebSocketFactory _wsFactory;
	
	private static WebSocketFactory get(WebsocketServletAdapter wrapper){
		if(_wsFactory == null){
			Protocol protoc = wrapper.getSodaProtocol();
			WampServer wampocServer = new WampServer();
			SourceHonestyFilter filter = new SourceHonestyFilter(protoc);
			wampocServer.addListener(filter);
			wampocServer.addPublishFilter(filter);
			wampocServer.setSubscriptions(wrapper.getSodaSubscriptionManager());
			
			// Create and configure WS factory
			_wsFactory = new WebSocketFactory(new JettyServerHandler());
			_wsFactory.setBufferSize(4096);
			_wsFactory.setMaxIdleTime(60000);
			
			
//			ServerSoda soda = new ServerSoda(
//					protoc, 
//					wrapper.getSodaAuthService(), 
//					tcpPort);
//			
//			wampocServer.addListener(soda);
//			soda.connect(null);
			
		}
		
		return _wsFactory;
	}

	@Override
	public void init() throws ServletException {
//		String value = getServletConfig().getServletContext().
		
//		get(this);
	}
	
	protected Subscriptions getSodaSubscriptionManager(){
		return new Subscriptions();
	}
	
	protected AuthService getSodaAuthService(){
		return AuthService.NO_AUTH_SVC;
	}
	
	protected Protocol getSodaProtocol(){
		return new DefaultProtocol();
	}
	
	protected abstract void started(ServerSoda soda);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (_wsFactory.acceptWebSocket(request, response))
			return;
		response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
				"Websocket only");
	}
}
