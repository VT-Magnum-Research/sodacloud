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
package org.magnum.soda.svc;

import java.lang.reflect.Proxy;

import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.Mode;

import org.magnum.soda.MsgBus;
import org.magnum.soda.ObjRegistry;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public class ObjInvoker {

	private static final Logger Log = LoggerFactory.getLogger(ObjInvoker.class);

	private ObjRegistry registry_;

	private ProxyFactory factory_;

	private LocalAddress myAddress_;

	private MsgBus msgBus_;

	private InvocationDispatcher dispatcher_ = InvocationDispatcher.DEFAULT_DISPATCHER;

	public ObjInvoker(LocalAddress addr, MsgBus bus, ObjRegistry registry,
			ProxyFactory factory) {
		super();
		factory_ = factory;
		msgBus_ = bus;
		registry_ = registry;
		myAddress_ = addr;
		msgBus_.subscribe(this);
	}

	@Subscribe
	@Listener(delivery = Mode.Concurrent)
	public void handleInvocation(ObjInvocationMsg msg) {
		InvocationInfo inv = msg.getInvocation();
		ObjRef targetid = msg.getTargetObjectId();

		Object o = registry_.get(targetid);
		if (o != null && !Proxy.isProxyClass(o.getClass())) {
			

			ObjInvocationRespMsg resp = (ObjInvocationRespMsg) msg
					.createReply();

			try {
				inv.bind(o);
				Object[] ex = inv.getParameters();
				
				Log.debug("Invoking method on: [{}] invocation: [{}]", o, inv);
				// this method will directly update the
				// ex array in place
				factory_.createProxiesFromRefsIfNeeded(ex);

				Object rslt = dispatcher_.dispatch(inv, o);
				rslt = factory_.convertToObjectRefIfNeeded(rslt);

				resp.setResult(rslt);
			} catch (Exception t) {
				Log.error("Exception executing invocation msg [{}]",msg);
				Log.error("Error:",t);
				resp.setException(t);
			}

			msgBus_.publish(resp);
		}
	}

	public InvocationDispatcher getDispatcher() {
		return dispatcher_;
	}

	public void setDispatcher(InvocationDispatcher dispatcher) {
		dispatcher_ = dispatcher;
	}

}
