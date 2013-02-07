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

public class LocalPipeTransport {

	private class BusHandler extends Transport {

		private BusHandler counterpart_;

		public BusHandler(MsgBus msgBus, LocalAddress addr) {
			super(msgBus, addr);
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
			counterpart_.receive(msg);
		}

		public void setCounterpart(BusHandler counterpart) {
			counterpart_ = counterpart;
		}

	}

	private BusHandler server_;
	private BusHandler client_;

	public LocalPipeTransport(Soda server, Soda client) {
		server_ = new BusHandler(server.getMsgBus(), server.getLocalAddress());
		client_ = new BusHandler(client.getMsgBus(), client.getLocalAddress());

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
