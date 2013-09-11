/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.server.wamp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import org.magnum.soda.proxy.ProxyCreator;
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;

public class CglibProxyCreator implements ProxyCreator {

	private class CglibProxy implements MethodInterceptor {
        private InvocationHandler handler_;

        public CglibProxy (InvocationHandler hdlr) {
            handler_ = hdlr;
        }

        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy ) throws Throwable {
            return handler_.invoke(object, method, args);
        }
    }
	
	private Map<Object,InvocationHandler> proxies_ = new WeakHashMap<Object, InvocationHandler>();
	
	@Override
	public Object createProxy(ClassLoader cl, Class<?>[] types,
			InvocationHandler hdlr) {
		Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(types[0]);
        CglibProxy intercept = new CglibProxy(hdlr);
        enhancer.setCallback(intercept);
        Object proxy = enhancer.create();
		
		proxies_.put(proxy,hdlr);
		return proxy;
	}

	@Override
	public InvocationHandler getInvocationHandler(Object proxy) {
		return proxies_.get(proxy);
	}

	@Override
	public boolean supportsNonInterfaceProxies() {
		return true;
	}

}
