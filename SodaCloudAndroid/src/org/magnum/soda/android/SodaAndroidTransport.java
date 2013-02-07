/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.android;

import org.magnum.soda.MsgBus;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.transport.Address;
import org.magnum.soda.transport.MsgContainer;
import org.magnum.soda.transport.Transport;
import org.magnum.soda.transport.UriAddress;

import de.tavendo.autobahn.Autobahn;
import de.tavendo.autobahn.Autobahn.EventHandler;
import de.tavendo.autobahn.AutobahnConnection;

public class SodaAndroidTransport extends Transport implements
		Autobahn.SessionHandler, EventHandler {

	private final AutobahnConnection mConnection = new AutobahnConnection();

	private LocalAddress myAddress_;

	public SodaAndroidTransport(MsgBus msgBus, LocalAddress addr) {
		super(msgBus, addr);
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
	public void connect(Address arg0) {
		if (arg0 instanceof UriAddress) {
			mConnection.connect(((UriAddress) arg0).getUri().toString(), this);
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
		MsgContainer c = (MsgContainer)arg1;
		receive(c);
	}

	@Override
	public void send(MsgContainer arg0) {
		String chnl = getOutboundChannel(arg0);
		mConnection.publish(chnl, arg0);
	}

	private String getInboundChannel(LocalAddress addr) {
		return addr.toString();
	}

	private String getOutboundChannel(MsgContainer c) {
		return c.getDestination();
	}

}
