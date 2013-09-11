/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.server.wamp;

import org.eclipse.jetty.server.Server;
import org.magnum.soda.server.wamp.adapters.jetty.JettyServerHandler;

public interface JettyConfigurer {

	public void configure(Server s, WampServer wamp, JettyServerHandler hdlr);
	
}
