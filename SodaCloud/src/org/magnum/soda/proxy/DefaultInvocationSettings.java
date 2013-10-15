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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class DefaultInvocationSettings implements InvocationSettings {

	private boolean invokeVoidMethodsAsync_ = false;
	
	private Set<String> asyncMethodList_ = new HashSet<String>();

	@Override
	public boolean shouldInvokeAsync(Object target, Method m, Object[] args) {
		return m.getReturnType() == void.class
				&& (m.getAnnotation(SodaAsync.class) != null
				|| invokeVoidMethodsAsync_
				|| asyncMethodList_.contains(m));
	}
	
	public void invokeAsync(Method m){
		String key = getMethodKey(m);
		asyncMethodList_.add(key);
	}
	
	private String getMethodKey(Method m){
		return m.getDeclaringClass().getName() + "." + m.getName();
	}

	public void setInvokeVoidMethodsAsync(boolean invokeVoidMethodsAsync) {
		invokeVoidMethodsAsync_ = invokeVoidMethodsAsync;
	}

}
