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
import java.util.LinkedList;
import java.util.List;

import org.magnum.soda.svc.InvocationDispatcher;
import org.magnum.soda.svc.InvocationInfo;

public class RecordingProxy implements InvocationHandler, Runnable {

	private class RecordedInvocation extends InvocationInfo {
		private Method method_;
		private Object[] args_;

		public RecordedInvocation(Method method, Object[] args) {
			super();
			method_ = method;
			args_ = args;
			
			setMethod(method.getName());
			setParameters(args_);
			setParameterTypes(method.getParameterTypes());
		}

		@Override
		public Object invoke(Object target) {
			try {
				return method_.invoke(target, args_);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Object target_;

	private ProxyCreator proxyCreator_;

	private List<RecordedInvocation> recorded_ = new LinkedList<RecordedInvocation>();

	private InvocationDispatcher dispatcher_;

	public RecordingProxy(Object target, ProxyCreator proxyCreator) {
		super();
		target_ = target;
		proxyCreator_ = proxyCreator;
	}

	public RecordingProxy(Object target, ProxyCreator proxyCreator,
			InvocationDispatcher dispatcher) {
		super();
		target_ = target;
		proxyCreator_ = proxyCreator;
		dispatcher_ = dispatcher;
	}

	@Override
	public Object invoke(Object arg0, Method arg1, Object[] arg2)
			throws Throwable {

		if(arg1.getName().equals("hashCode")){
			return super.hashCode();
		}
		else if(arg1.getName().equals("equals")){
			return super.equals(arg2[0]);
		}
		
		RecordedInvocation i = new RecordedInvocation(arg1, arg2);
		recorded_.add(i);

		if (dispatcher_ != null) {
			dispatch();
		}
		
		Class<?> rtype = arg1.getReturnType();
		if (arg1.getReturnType().isPrimitive()) {
			if (rtype == int.class || rtype == double.class
					|| rtype == float.class || rtype == byte.class
					|| rtype == short.class) {
				return 0;
			} else if (rtype == boolean.class) {
				return false;
			} else if (rtype == char.class) {
				return 'a';
			} else {
				return null;
			}
		}

		if (!rtype.equals(void.class)) {
			Class<?>[] types = { rtype };
			if (!rtype.isInterface() && !proxyCreator_.supportsNonInterfaceProxies()) {
				types = rtype.getInterfaces();
			}
			
			return proxyCreator_.createProxy(getClass().getClassLoader(),
					types, this);
		} else {
			return null;
		}
	}

	@Override
	public void run() {
		try {
			dispatch();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized void dispatch() throws Exception {
		for (RecordedInvocation i : recorded_) {
			target_ = dispatcher_.dispatch(i, target_);
		}
		recorded_.clear();
	}

}
