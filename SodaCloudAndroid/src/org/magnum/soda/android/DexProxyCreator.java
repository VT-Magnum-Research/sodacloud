/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.android;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.magnum.soda.proxy.ProxyCreator;

import com.google.dexmaker.stock.ProxyBuilder;

public class DexProxyCreator implements ProxyCreator {

	private Map<Object, InvocationHandler> proxies_ = new HashMap<Object, InvocationHandler>();

	private File dexCache_;

	public DexProxyCreator(File dexCache) {
		super();
		dexCache_ = dexCache;
	}

	@Override
	public Object createProxy(ClassLoader arg0, Class<?>[] arg1,
			InvocationHandler arg2) {

		Object proxy = null;
		try {
			if (arg1.length == 1 && !arg1[0].isInterface()) {
				proxy = ProxyBuilder.forClass(arg1[0]).dexCache(dexCache_)
						.handler(arg2).build();
			} else {
				proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
						arg1, arg2);
			}
			
			if(proxy != null){
				proxies_.put(proxy, arg2);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return proxy;
	}

	@Override
	public InvocationHandler getInvocationHandler(Object arg0) {
		return proxies_.get(arg0);
	}

	@Override
	public boolean supportsNonInterfaceProxies() {
		return true;
	}

}
