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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.Mode;

import org.magnum.soda.msg.LocalAddress;
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
	private MBassyMsgBus msgBus_ = new MBassyMsgBus();
	private DefaultObjRegistry objRegistry_ = new DefaultObjRegistry(
			localAddress_);
	private ProxyFactory proxyFactory_ = new ProxyFactory(objRegistry_,
			localAddress_, msgBus_);
	private ObjInvoker objInvoker_ = new ObjInvoker(localAddress_, msgBus_,
			objRegistry_, proxyFactory_);
	private ObjRegistryUpdater objRegistryUpdater_ = new ObjRegistryUpdater(
			proxyFactory_, objRegistry_);

	private Map<Runnable, SodaBinding> ctxRunnableObjBinding_ = null;

	public Soda() {
		namingServiceRef_ = objRegistry_.publish(namingService_);
		msgBus_.subscribe(this);
		ctxRunnableObjBinding_ = new HashMap<Runnable, SodaBinding>();
	}

	public Soda(boolean becomeserver) {
		this();
		if (becomeserver) {
			getObjRegistry().insert(NamingService.ROOT_NAMING_SVC,
					getNamingService());
		}
	}

	public Soda(Transport t) {
		this();
		transport_ = t;
		transport_.setListener(this);
	}

	@Subscribe
	@Listener(delivery = Mode.Concurrent)
	public void handleNamingServiceRequest(ObtainNamingServiceMsg msg) {
		ObtainNamingServiceRespMsg resp = (ObtainNamingServiceRespMsg) msg
				.createReply();
		resp.setNamingService(namingServiceRef_);
		msgBus_.publish(resp);
	}

	public LocalAddress getLocalAddress() {
		return localAddress_;
	}

	public MBassyMsgBus getMsgBus() {
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
		setTransport(t);
		connect(addr);
	}

	public void setTransport(Transport t) {
		transport_ = t;
		transport_.setListener(this);
	}

	protected NamingService getNamingService() {
		return namingService_;
	}

	protected ObjInvoker getInvoker() {
		return objInvoker_;
	}

	public SodaBinding bind(Object o) {

		SodaBinding b = new SodaBinding();
		if (o instanceof Runnable) {
			this.ctxRunnableObjBinding_.put((Runnable) o, b);
		} else {
			throw new RuntimeException();
		}
		return b;

	}

	public <T> SodaQuery<T> find(Class<T> type, SodaContext ctx) {

		SodaQuery<Runnable> sq = null;
		if (type == Runnable.class) {
			Iterator<Runnable> itrRunnable = this.ctxRunnableObjBinding_
					.keySet().iterator();
			while (itrRunnable.hasNext()) {
				Runnable r = (Runnable) itrRunnable.next();
				SodaBinding sb = this.ctxRunnableObjBinding_.get(r);
				Iterator<SodaContext> itrSodaBinding = sb.getContexts_()
						.iterator();
				while (itrSodaBinding.hasNext()) {
					SodaContext stx = itrSodaBinding.next();
					if (stx.equals(ctx)) {
						sq = new SodaQuery<Runnable>(r);
					}
				}

			}
		}
		return (SodaQuery<T>) sq;
	}

	@Override
	public void connected() {
		try {
			ObjRef ref = NamingService.ROOT_NAMING_SVC;
			NamingService svc = (NamingService) proxyFactory_.createProxy(ref);
			namingService_.setParent(svc);
		} catch (Exception e) {
			Log.error("Error looking up root naming service", e);
		}

	}

	@Override
	public void disconnected() {
	}

}
