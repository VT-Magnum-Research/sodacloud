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

import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.Mode;

import org.magnum.soda.MsgBus;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.msg.Msg;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.proxy.ObjRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public abstract class Transport {

	private static final int DEFAULT_THREADS = 3;

	private static final Logger Log = LoggerFactory.getLogger(Transport.class);

	private TransportListener listener_;
	private Protocol protocol_;
	private MsgBus msgBus_;
	private LocalAddress myAddress_;
	private ExecutorService executor_ = Executors
			.newFixedThreadPool(DEFAULT_THREADS);

	public Transport(Protocol protocol, MsgBus msgBus, LocalAddress addr) {
		super();
		msgBus_ = msgBus;
		myAddress_ = addr;
		protocol_ = protocol;

		msgBus_.subscribe(this);

		init(myAddress_);
	}

	protected void init(LocalAddress addr) {
	}

	/**
	 * This method receives msgs that are posted to the msgbus by local objects.
	 * The method marshalls the msgs into a MsgContainer and asks the transport
	 * implementation to send them.
	 * 
	 * @param m
	 */
	@Subscribe
	@Listener(delivery = Mode.Concurrent)
	public void handleLocalOutboundMsg(final Msg m) {
		try {
			if (!m.isMarked()) {
				m.setSource(myAddress_.toString());
				final MsgContainer cont = protocol_.outbound(m);
				cont.setDestination(m.getDestination());

				Runnable r = new Runnable() {

					@Override
					public void run() {
						send(cont);
					}
				};

				executor_.submit(r);
			}
		} catch (Exception e) {
			Log.error("Unexpected transport error", e);
		}
	}

	/**
	 * This method should be called by Transport subclasses to unmarshall recvd
	 * msgs and place them on the local msgbus.
	 * 
	 * @param msgc
	 */
	public void receive(MsgContainer msgc) {
		try {
			String json = msgc.getMsg();
			Msg msg = protocol_.inbound(json);
			msg.mark();
			msgBus_.publish(msg);
		} catch (Exception e) {
			Log.error("Unexpected transport error", e);
		}
	}

	public LocalAddress getMyAddress() {
		return myAddress_;
	}

	public TransportListener getListener() {
		return listener_;
	}

	public void setListener(TransportListener listener) {
		listener_ = listener;
	}

	public ExecutorService getExecutor() {
		return executor_;
	}

	public void setExecutor(ExecutorService executor) {
		executor_ = executor;
	}

	public Protocol getProtocol() {
		return protocol_;
	}

	public void setProtocol(Protocol protocol) {
		protocol_ = protocol;
	}
	
	public abstract boolean isConnected();

	public abstract void connect(Address addr);

	public abstract void disconnect();

	/**
	 * This method should be overriden by Transport subclasses to send an
	 * outbound msg.
	 * 
	 * @param msg
	 */
	public abstract void send(MsgContainer msg);

}
