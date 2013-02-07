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
package org.magnum.soda;

import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.msg.MetaAddress;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyFactory;
import org.magnum.soda.svc.DefaultNamingService;
import org.magnum.soda.svc.NamingService;
import org.magnum.soda.svc.ObjInvoker;
import org.magnum.soda.svc.ObjRegistryUpdater;
import org.magnum.soda.svc.ObtainNamingServiceMsg;
import org.magnum.soda.svc.ObtainNamingServiceRespMsg;
import org.magnum.soda.transport.Address;
import org.magnum.soda.transport.Transport;
import org.magnum.soda.transport.TransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public class Soda implements TransportListener {

	private static final Logger Log = LoggerFactory.getLogger(Soda.class);

	private Transport transport_;

	private ObjRef namingServiceRef_;

	private NamingService namingService_ = new DefaultNamingService();

	private LocalAddress localAddress_ = new LocalAddress();
	private GuavaMsgBus msgBus_ = new GuavaMsgBus();
	private DefaultObjRegistry objRegistry_ = new DefaultObjRegistry(
			localAddress_);
	private ProxyFactory proxyFactory_ = new ProxyFactory(objRegistry_,
			localAddress_, msgBus_);
	private ObjInvoker objInvoker_ = new ObjInvoker(msgBus_, objRegistry_,
			proxyFactory_);
	private ObjRegistryUpdater objRegistryUpdater_ = new ObjRegistryUpdater(
			proxyFactory_, objRegistry_);

	public Soda() {
		namingServiceRef_ = objRegistry_.publish(namingService_);
		msgBus_.subscribe(this);
	}

	public Soda(Transport t) {
		this();
		transport_ = t;
		transport_.setListener(this);
	}

	@Subscribe
	public void handleNamingServiceRequest(ObtainNamingServiceMsg msg) {
		ObtainNamingServiceRespMsg resp = (ObtainNamingServiceRespMsg)msg.createReply();
		resp.setNamingService(namingServiceRef_);
		msgBus_.publish(resp);
	}

	@Subscribe
	public void handleNamingServiceResponse(ObtainNamingServiceRespMsg msg) {
		try {
			ObjRef ns = msg.getNamingService();
			if (ns != null) {
				NamingService naming = (NamingService) proxyFactory_
						.createProxiesFromRefsIfNeeded(ns);
				namingService_.setParent(naming);

				Log.debug("Successfully obtained the server's naming service");
			}
		} catch (Exception e) {
			Log.error("Unexpected error obtaining a reference to the server's naming service.");
		}
	}

	public LocalAddress getLocalAddress() {
		return localAddress_;
	}

	public GuavaMsgBus getMsgBus() {
		return msgBus_;
	}

	public DefaultObjRegistry getObjRegistry() {
		return objRegistry_;
	}

	public <T> T get(Class<T> type, String name) {
		return namingService_.get(type, name);
	}

	public void bind(Object o, String name) {
		namingService_.bind(o, name);
	}

	public void connect(Address addr) {
		transport_.connect(addr);
	}

	public void connect(Transport t, Address addr) {
		transport_ = t;
		transport_.setListener(this);
		connect(addr);
	}

	@Override
	public void connected() {
		ObtainNamingServiceMsg msg = new ObtainNamingServiceMsg();
		msg.setDestination(MetaAddress.META_ADDRESS.toString());
		transport_.handleLocalOutboundMsg(msg);
	}

	@Override
	public void disconnected() {
	}

}
