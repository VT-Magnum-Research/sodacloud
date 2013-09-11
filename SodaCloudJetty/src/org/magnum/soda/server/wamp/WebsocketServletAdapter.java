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

	private static WebSocketFactory get(final WebsocketServletAdapter wrapper) {
		if (_wsFactory == null) {

			boolean initClient = false;
			if (wrapper.getWampServer() == null) {
				Protocol protoc = wrapper.getSodaProtocol();
				WampServer wampocServer = new WampServer();
				SourceHonestyFilter filter = new SourceHonestyFilter(protoc);
				wampocServer.addListener(filter);
				wampocServer.addPublishFilter(filter);
				wampocServer.setSubscriptions(wrapper
						.getSodaSubscriptionManager());
				wrapper.setWampServer(wampocServer);
				initClient = true;
			}

			// Create and configure WS factory
			_wsFactory = new WebSocketFactory(new JettyServerHandler(
					wrapper.getWampServer()));
			_wsFactory.setBufferSize(4096);
			_wsFactory.setMaxIdleTime(60000);

			if (initClient) {
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						ServerSoda soda = new ServerSoda(
								wrapper.getSodaProtocol(),
								wrapper.getSodaAuthService(),
								wrapper.getPath(), wrapper.getPort());
						wrapper.getWampServer().addListener(soda);
						soda.connect(null);
					}
				});
				t.start();
			}
		}

		return _wsFactory;
	}

	private int port_ = -1;

	private WampServer wampServer_;

	private String path_ = "/";

	@Override
	public void init() throws ServletException {
		if (port_ == -1) {
			port_ = Integer.parseInt(getServletConfig().getServletContext()
					.getInitParameter("port"));
		}

		get(this);
	}

	public String getPath() {
		return path_;
	}

	public void setPath(String path) {
		path_ = path;
	}

	protected Subscriptions getSodaSubscriptionManager() {
		return new Subscriptions();
	}

	protected AuthService getSodaAuthService() {
		return AuthService.NO_AUTH_SVC;
	}

	protected Protocol getSodaProtocol() {
		return new DefaultProtocol();
	}

	public int getPort() {
		return port_;
	}

	public void setPort(int port) {
		port_ = port;
	}

	public WampServer getWampServer() {
		return wampServer_;
	}

	public void setWampServer(WampServer wampServer) {
		wampServer_ = wampServer;
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
