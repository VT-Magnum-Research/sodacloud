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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.msg.MetaAddress;
import org.magnum.soda.proxy.ObjRef;

/**
 * 
 * This class manages a repository of object references and their mapping to a
 * set of associated objects. Typically, an ObjRef is paired to a proxy,
 * although regular objects may also be mapped to an ObjRef.
 * 
 * @author jules
 * 
 */
public class DefaultObjRegistry implements ObjRegistry {

	private Map<String, Object> registry_ = new ConcurrentHashMap<String, Object>();

	private WeakHashMap<Object, ObjRef> objRefs_ = new WeakHashMap<Object, ObjRef>();

	private Set<String> localObjects_ = new HashSet<String>();

	private LocalAddress myAddress_;

	private final boolean isServer_;

	public DefaultObjRegistry(LocalAddress myAddress) {
		this(myAddress, false);
	}

	public DefaultObjRegistry(LocalAddress myAddress, boolean isserver) {
		super();
		isServer_ = isserver;
		myAddress_ = myAddress;
	}

	/**
	 * Generally, you should use publish rather than this method.
	 */
	@Override
	public void insert(ObjRef key, Object o) {
		registry_.put(key.getUri(), o);
		objRefs_.put(o, key);
	}

	@Override
	public boolean remove(ObjRef key) {
		if (localObjects_.contains(key.getUri())) {
			localObjects_.remove(key.getUri());
		}
		return registry_.remove(key.getUri()) != null;
	}

	@Override
	public Object get(ObjRef key) {
		return registry_.get(key.getUri());
	}

	/**
	 * Publish an object and create a reference to it that can be shared.
	 */
	@Override
	public ObjRef publish(Object o) {

		ObjRef ref = objRefs_.get(o);
		if (ref == null) {
			ref = myAddress_.createObjRef(o);
			insert(ref, o);
			localObjects_.add(ref.getUri());
		}
		return ref;
	}

	@Override
	public boolean isLocalObject(ObjRef ref) {
		return myAddress_.toString().equals(ref.getHost())
				|| (isServer_ && ref.getHost().startsWith(
						MetaAddress.META_ADDRESS.toString()));
	}

}
