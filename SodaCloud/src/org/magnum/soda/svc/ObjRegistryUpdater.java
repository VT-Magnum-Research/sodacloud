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

import org.magnum.soda.ObjRegistry;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public class ObjRegistryUpdater {

	private static final Logger Log = LoggerFactory
			.getLogger(ObjRegistryUpdater.class);
	
	private ObjRegistry registry_;

	private ProxyFactory proxyFactory_;

	public ObjRegistryUpdater(ProxyFactory factory,
			ObjRegistry registry) {
		super();
		proxyFactory_ = factory;
		registry_ = registry;
	}

	@Subscribe
	public void updateRegistry(ObjAdvertisementMsg advmsg) {
		ObjRef id = advmsg.getObjectId();

		try {
			Log.debug("Creating a proxy for object ref [{}]",id);
			// proxy needs to be created and inserted
			Object proxy = proxyFactory_.createProxy(id);
			
			Log.debug("Proxy for [{}] created as [{}]",id,proxy);
			
			registry_.insert(id, proxy);
			
			Log.debug("Proxy added to registry");
		} catch (Exception e) {
			Log.error("Unexpected exception adding proxy object to registry [{}]",id);
			Log.error("Reason",e);
			
			throw new RuntimeException(e);
		}
	}
}
