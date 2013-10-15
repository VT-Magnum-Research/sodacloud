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
package org.magnum.soda.proxy;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

import org.magnum.soda.MsgBus;
import org.magnum.soda.ObjRegistry;
import org.magnum.soda.msg.LocalAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ProxyFactory {

	private static final Logger Log = LoggerFactory
			.getLogger(ProxyFactory.class);

	private static Map<String, Boolean> byValueClasses_ = Maps
			.newHashMap(new ImmutableMap.Builder<String, Boolean>()
					.put(String.class.getName(), true)
					.put(Boolean.class.getName(), true)
					.put(Integer.class.getName(), true)
					.put(Long.class.getName(), true)
					.put(Double.class.getName(), true)
					.put(Float.class.getName(), true)
					.put(Byte.class.getName(), true)
					.put(ObjRef.class.getName(), true)
					.put(Class.class.getName(), true)
					.put(byte[].class.getName(), true).build());

	private MsgBus msgBus_;

	private LocalAddress myAddress_;

	private ObjRegistry objRegistry_;

	private ProxyCreator creator_;

	private InvocationSettings invocationSettings_ = new DefaultInvocationSettings();

	public ProxyFactory(ObjRegistry reg, ProxyCreator creator,
			LocalAddress myaddr, MsgBus msgBus) {
		super();
		creator_ = creator;
		msgBus_ = msgBus;
		myAddress_ = myaddr;
		objRegistry_ = reg;
	}

	@SuppressWarnings("unchecked")
	public <T> T createProxy(Class<T> type, ObjRef objid) {
		return (T) createProxy(new Class[] { type }, objid);
	}

	public Object createProxy(Class<?>[] types, ObjRef objid) {

		Log.debug("Creating a proxy for ref [{}]", objid);

		Object proxy = creator_.createProxy(getClass().getClassLoader(), types,
				new ObjProxy(this, invocationSettings_, msgBus_, objid));

		return proxy;
	}

	public Object createProxy(ObjRef ref) throws ClassNotFoundException {
		Class<?>[] types = new Class[ref.getTypes().length];
		for (int i = 0; i < types.length; i++) {
			types[i] = Class.forName(ref.getTypes()[i]);
		}
		return createProxy(types, ref);
	}

	public ObjRef createObjRef(Class<?>[] types) {
		ObjRef ref = myAddress_.createObjRef(types);
		return ref;
	}

	public Object[] convertToObjectRefsIfNeeded(Object[] v) {
		int len = (v != null) ? v.length : 0;
		Object[] args = new Object[len];
		if (v != null) {
			for (int i = 0; i < v.length; i++) {
				args[i] = convertToObjectRefIfNeeded(v[i]);
			}
		}
		return args;
	}

	public Object convertToObjectRefIfNeeded(Object v) {
		if (v instanceof Object[]) {
			return convertToObjectRefsIfNeeded((Object[]) v);
		} else if (v != null && !v.getClass().isPrimitive()) {
			v = createObjRef(v);
		}
		return v;
	}

	public Object createProxiesFromRefsIfNeeded(Object o) throws Exception {
		if (o instanceof Object[]) {
			Object[] vals = (Object[]) o;
			for (int i = 0; i < vals.length; i++) {
				vals[i] = createProxiesFromRefsIfNeeded(vals[i]);
			}
		} else if (o instanceof ObjRef) {
			ObjRef ref = (ObjRef) o;
			Object ex = objRegistry_.get(ref);
			if (ex == null) {
				o = createProxy(ref);
				objRegistry_.insert(ref, o);
			} else {
				o = ex;
			}
		}

		return o;
	}

	private Object createObjRef(Object v) {
		InvocationHandler hdlr = creator_.getInvocationHandler(v);
		if (hdlr != null) {
			if (hdlr instanceof ObjProxy) {
				v = ((ObjProxy) hdlr).getObjectRef();
			}
		}

		String cls = v.getClass().getName();
		boolean byval = false;
		if (v.getClass().getAnnotation(SodaByValue.class) != null) {
			byval = true;
		} else {
			Boolean bv = byValueClasses_.get(cls);
			byval = (bv != null) ? bv : false;
		}
		if (!byval) {
			ObjRef ref = objRegistry_.publish(v);
			v = ref;
		}

		return v;
	}

	public InvocationSettings getInvocationSettings() {
		return invocationSettings_;
	}

	public void setInvocationSettings(InvocationSettings invocationSettings) {
		invocationSettings_ = invocationSettings;
	}

	public void passByValue(Class<?> type) {
		byValueClasses_.put(type.getName(), true);
	}
}
