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
package org.magnum.soda.android;

import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;

import org.magnum.soda.MsgBus;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.protocol.java.NativeJavaProtocol;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.transport.Address;
import org.magnum.soda.transport.MsgContainer;
import org.magnum.soda.transport.Transport;
import org.magnum.soda.transport.UriAddress;
import org.magnum.soda.transport.wamp.Wamp;
import org.magnum.soda.transport.wamp.WampConnection;
import org.magnum.soda.transport.wamp.Wamp.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Handler;

public class SodaAndroidTransport extends Transport implements
		Wamp.ConnectionHandler, EventHandler {

	private static final Logger Log = LoggerFactory
			.getLogger(SodaAndroidTransport.class);

	private final WampConnection mConnection = new WampConnection();

	private CountDownLatch connectGate_;

	private LocalAddress myAddress_;

	private UriAddress serverAddress_;

	public SodaAndroidTransport(MsgBus msgBus, LocalAddress addr) {
		super(new NativeJavaProtocol(), msgBus, addr);
		myAddress_ = addr;
	}

	public SodaAndroidTransport(Protocol protocol, MsgBus msgBus,
			LocalAddress addr) {
		super(protocol, msgBus, addr);
		myAddress_ = addr;
	}

	@Override
	public void onOpen() {
		String inbound = getInboundChannel(myAddress_);
		mConnection.subscribe(inbound, MsgContainer.class, this);
		getListener().connected();
	}

	@Override
	public void onClose(int code, String reason) {
		getListener().disconnected();
	}

	@Override
	public void connect(final Address arg0) {
		if (arg0 instanceof UriAddress) {
			serverAddress_ = (UriAddress) arg0;
			String srvr = serverAddress_.getUri().toString();
			mConnection.connect(srvr, SodaAndroidTransport.this);
		} else {
			throw new RuntimeException(
					"Only UriAddresses are supported by this transport.");
		}
	}

	@Override
	public void disconnect() {
		mConnection.disconnect();
	}

	@Override
	public void onEvent(String arg0, Object arg1) {
		final MsgContainer c = (MsgContainer) arg1;
		Log.debug("Client Receiving topic:[{}] msg:[{}]", arg0, c.getMsg());
		if (c.getMsg() == null) {
			Log.error("Malformed msg received [{}]", c.getMsg());
		} else {
			receive(c);
		}
	}

	@Override
	public void send(MsgContainer arg0) {
		String chnl = getOutboundChannel(arg0);
		Log.debug("Client Sending topic:[{}] msg:[{}]", chnl, arg0.getMsg());
		mConnection.publish(chnl, arg0);
	}

	private String getInboundChannel(LocalAddress addr) {
		return addr.toString();
	}

	private String getOutboundChannel(MsgContainer c) {
		return c.getDestination();
	}

	@Override
	public boolean isConnected() {
		return mConnection != null && mConnection.isConnected();
	}

}
