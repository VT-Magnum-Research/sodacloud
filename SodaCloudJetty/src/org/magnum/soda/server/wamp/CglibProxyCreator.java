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
import org.mockito.cglib.proxy.Callback;
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;
import org.objenesis.ObjenesisHelper;

public class CglibProxyCreator implements ProxyCreator {
	
	public CglibProxyCreator(ClassLoader cl) {
		classLoader_ = cl;
	}
	private class CglibProxy implements MethodInterceptor {
        private InvocationHandler handler_;

        public CglibProxy (InvocationHandler hdlr) {
            handler_ = hdlr;
        }

        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy ) throws Throwable {
            return handler_.invoke(object, method, args);
        }
    }
	
	private ClassLoader classLoader_;
	private Map<Object,InvocationHandler> proxies_ = new WeakHashMap<Object, InvocationHandler>();
	
	@Override
	public Object createProxy(ClassLoader cl, Class<?>[] types,
			InvocationHandler hdlr) {
		Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(types[0]);
        CglibProxy intercept = new CglibProxy(hdlr);
        enhancer.setCallbackType(intercept.getClass());
        
		final Class<?> proxyClass = enhancer.createClass();
		Enhancer.registerCallbacks(proxyClass, new Callback[]{intercept});
		Object proxy = ObjenesisHelper.newInstance(proxyClass);
		
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

	@Override
	public ClassLoader getProxyClassLoader() {
		return classLoader_;
	}

}
