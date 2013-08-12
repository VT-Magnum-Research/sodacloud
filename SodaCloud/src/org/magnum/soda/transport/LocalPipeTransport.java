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
package org.magnum.soda.transport;

import org.magnum.soda.MsgBus;
import org.magnum.soda.Soda;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.msg.MetaAddress;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.protocol.java.NativeJavaProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPipeTransport {

	private static final Logger Log = LoggerFactory
			.getLogger(LocalPipeTransport.class);

	private class BusHandler extends Transport {

		private BusHandler counterpart_;

		public BusHandler(Protocol proto, MsgBus msgBus, LocalAddress addr) {
			super(proto, msgBus, addr);
		}

		@Override
		public void connect(Address a) {
			getListener().connected();
		}

		@Override
		public void disconnect() {
			getListener().disconnected();
		}

		@Override
		public void send(MsgContainer msg) {
			if (msg.getDestination().equals(counterpart_.getMyAddress().toString())
					|| (msg.getDestination().equals(
							MetaAddress.META_ADDRESS.toString()) && counterpart_ == server_)) {
				counterpart_.receive(msg);
			} else {
				Log.error(
						"Attempt to send a msg to address [{}] with a piped connection and neither end has the listed address.",
						msg.getDestination(), msg.getMsg());
			}
		}

		public void setCounterpart(BusHandler counterpart) {
			counterpart_ = counterpart;
		}
		
		public boolean isConnected(){
			return true;
		}


	}

	private BusHandler server_;
	private BusHandler client_;

	public LocalPipeTransport(Soda server, Soda client) {
		server_ = new BusHandler(new NativeJavaProtocol(), server.getMsgBus(), server.getLocalAddress());
		client_ = new BusHandler(new NativeJavaProtocol(), client.getMsgBus(), client.getLocalAddress());

		server_.setCounterpart(client_);
		client_.setCounterpart(server_);
	}
	
	public LocalPipeTransport(Soda server, Soda client, Protocol proto) {
		server_ = new BusHandler(proto, server.getMsgBus(), server.getLocalAddress());
		client_ = new BusHandler(proto, client.getMsgBus(), client.getLocalAddress());

		server_.setCounterpart(client_);
		client_.setCounterpart(server_);
	}

	public BusHandler getServerTransport() {
		return server_;
	}

	public BusHandler getClientTransport() {
		return client_;
	}


}
