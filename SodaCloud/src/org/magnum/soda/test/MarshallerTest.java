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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.security.SecureRandom;
import java.util.UUID;

import org.junit.Test;
import org.magnum.soda.marshalling.Marshaller;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.svc.InvocationInfo;
import org.magnum.soda.svc.InvocationInfoBuilder;
import org.magnum.soda.svc.ObjInvocationMsg;
import org.magnum.soda.svc.ObjInvocationMsgBuilder;
import org.magnum.soda.test.ObjInvokerTest.TestMe;

public class MarshallerTest {

	public static class TestObj {
		private byte[] data;
		private String name;
		public byte[] getData() {
			return data;
		}
		public void setData(byte[] data) {
			this.data = data;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	@Test
	public void test() throws Exception {
		LocalAddress addr = new LocalAddress();
		
		TestMe testme = mock(TestMe.class);

		ObjRef ref = addr.createObjRef(testme);

		// Note that rather than directly passing "testme" we pass a reference
		// to it as the last arg that must be dynamically converted back to
		// the right object
		Object[] args = new Object[] { new String[] { "a", "b" }, 2,
				new Integer[] { 4, 5 }, ref };
		Class<?>[] types = new Class[] { String[].class, int.class,
				Integer[].class, TestMe.class };

		InvocationInfo target = InvocationInfoBuilder.invocationInfo()
				.withMethod("bar").withParameterTypes(types)
				.withParameters(args).build();
		ObjInvocationMsg invoke = ObjInvocationMsgBuilder.objInvocationMsg()
				.withInvocation(target).withTargetObjectId(ref).build();

		Marshaller marsh = new Marshaller();
		String data = marsh.toTransportFormat(invoke);

		invoke = marsh.fromTransportFormat(ObjInvocationMsg.class, data);

		assertEquals(ref, invoke.getTargetObjectId());

		InvocationInfo info = invoke.getInvocation();
		assertEquals("bar", info.getMethod());
		assertArrayEquals(types, info.getParameterTypes());
		assertArrayEquals(args, info.getParameters());
		
		TestObj obj = new TestObj();
		obj.setData((new SecureRandom()).generateSeed(1025));
		obj.setName(UUID.randomUUID().toString());
		
		String json = marsh.toTransportFormat(obj);
		TestObj recvd = marsh.fromTransportFormat(TestObj.class, json);
		
		assertEquals(obj.getName(),recvd.getName());
		assertArrayEquals(obj.getData(), recvd.getData());
	}


}
