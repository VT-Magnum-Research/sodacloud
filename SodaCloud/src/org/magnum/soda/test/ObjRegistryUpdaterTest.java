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
package org.magnum.soda.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.magnum.soda.DefaultObjRegistry;
import org.magnum.soda.ObjRegistry;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyFactory;
import org.magnum.soda.svc.ObjAdvertisementMsg;
import org.magnum.soda.svc.ObjAdvertisementMsgBuilder;
import org.magnum.soda.svc.ObjRegistryUpdater;
import org.magnum.soda.test.ObjProxyTest.TestObj;

public class ObjRegistryUpdaterTest {

	private LocalAddress myAddress_ = new LocalAddress();

	@Test
	public void test() throws Exception {
		DefaultObjRegistry reg = new DefaultObjRegistry(new LocalAddress());
		ProxyFactory factory = mock(ProxyFactory.class);
		when(factory.createProxy(any(ObjRef.class))).thenReturn("asdf");

		ObjRef ref = myAddress_.createObjRef(TestObj.class);

		ObjRegistryUpdater updater = new ObjRegistryUpdater(factory, reg);

		ObjAdvertisementMsg msg = ObjAdvertisementMsgBuilder
				.objAdvertisementMsg().withObjectId(ref)
				.withType(Boolean.class.getName()).build();

		updater.updateRegistry(msg);

		Object proxy = reg.get(ref);

		assertNotNull(proxy);
		assertEquals("asdf", proxy);

		List<ObjRef> refs = new ArrayList<ObjRef>();
		for (int i = 0; i < 100; i++) {
			
			ref = myAddress_.createObjRef(TestObj.class);
			msg = ObjAdvertisementMsgBuilder
					.objAdvertisementMsg().withObjectId(ref)
					.withType(ObjRegistry.class.getName()).build();

			updater.updateRegistry(msg);
			refs.add(ref);
		}
		for(ObjRef r : refs){
			proxy = reg.get(r);
			assertNotNull(proxy);
			assertTrue(myAddress_.isALocalObject(r));
		}
	}

}
