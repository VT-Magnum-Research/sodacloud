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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyFactory;

public class DefaultNamingService implements NamingService {

	public static final String SVC_NAME = "naming";

	private Map<String, Object> objects_ = new HashMap<String, Object>();

	private NamingService parent_;
	
	private ProxyFactory proxyFactory_;

	public DefaultNamingService() {
	}

	public DefaultNamingService(DefaultNamingService parent, ProxyFactory fact) {
		super();
		parent_ = parent;
		proxyFactory_ = fact;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.magnum.comms.svc.INamingService#get(java.lang.Class,
	 * java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, String name) {
		T obj = (T) objects_.get(name);
		if (obj == null && parent_ != null) {
			obj = (T) parent_.get(type, name);
			
			// Yes, this looks really bizarre! You may wonder
			// how on earth obj could be of type ObjRef if it
			// was cast to type T above. Because parent_ is actually
			// a dynamic proxy, it is possible that it will return
			// an Object.
			if(obj instanceof LinkedHashMap){
				@SuppressWarnings("rawtypes")
				LinkedHashMap data = (LinkedHashMap)obj;
				ObjRef ref = new ObjRef("" + data.get("uri"),type.getName());
				obj = (T)proxyFactory_.createProxy(new Class[]{type}, ref);
			}
			if(obj instanceof ObjRef){
				obj = (T)proxyFactory_.createProxy(new Class[]{type}, (ObjRef)obj);
			}
		}
		return obj;
	}
	
	


	@Override
	public Object get(String name) {
		return get(Object.class, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.magnum.comms.svc.INamingService#bind(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public void bind(Object o, String name) {
		if (objects_.get(name) != null) {
			throw new RuntimeException(
					"An object is already bound to the name [" + name + "]");
		}
		objects_.put(name, o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.magnum.comms.svc.INamingService#getParent()
	 */
	@Override
	public NamingService getParent() {
		return parent_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.magnum.comms.svc.INamingService#setParent(org.magnum.comms.svc.
	 * NamingService)
	 */
	@Override
	public void setParent(NamingService parent, ProxyFactory fact) {
		parent_ = parent;
		proxyFactory_ = fact;
	}

}
