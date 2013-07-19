/*****************************************************************************
 * Copyright [2013] [Jules White]                                            *
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

import org.magnum.soda.server.wamp.adapters.jetty.JettyClient;
import org.magnum.soda.server.wamp.client.EventReceiver;
import org.magnum.soda.server.wamp.client.WampClient;

public class ServerPubSubHandler {

	private WampClient wampClient_;
	private JettyClient client_;

	public ServerPubSubHandler(int port) {

		try {
			JettyClient client_ = new JettyClient();
			client_.connect(null, "", port);
			wampClient_ = client_.getWampClient();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void subscribe(String topicId, EventReceiver rcvr)
			throws IOException {
		wampClient_.subscribe(topicId, rcvr);
	}

	public void publish(String topicId, Object msg) throws IOException {
		wampClient_.publish(topicId, msg);
	}
	
	public boolean isConnected(){
		return client_.isConnected();
	}
}
