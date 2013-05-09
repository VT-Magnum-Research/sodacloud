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
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.WeakHashMap;

public class JavaReflectionProxyCreator implements ProxyCreator {

	private Map<Object,InvocationHandler> proxies_ = new WeakHashMap<Object, InvocationHandler>();
	
	@Override
	public Object createProxy(ClassLoader cl, Class<?>[] types,
			InvocationHandler hdlr) {

		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
				types, hdlr);

		
		proxies_.put(proxy,hdlr);
		return proxy;
	}

	@Override
	public InvocationHandler getInvocationHandler(Object proxy) {
		return proxies_.get(proxy);
	}

	@Override
	public boolean supportsNonInterfaceProxies() {
		return false;
	}

}
