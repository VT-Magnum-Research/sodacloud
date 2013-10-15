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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.magnum.soda.MsgBus;
import org.magnum.soda.ObjRegistry;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.proxy.JavaReflectionProxyCreator;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyFactory;
import org.magnum.soda.svc.InvocationInfo;
import org.magnum.soda.svc.InvocationInfoBuilder;
import org.magnum.soda.svc.ObjInvocationMsg;
import org.magnum.soda.svc.ObjInvocationMsgBuilder;
import org.magnum.soda.svc.ObjInvocationRespMsg;
import org.magnum.soda.svc.ObjInvoker;
import org.magnum.soda.svc.Session;
import org.magnum.soda.svc.SessionData;
import org.mockito.ArgumentCaptor;

public class ObjInvokerTest {

	public static interface TestMe {
		public void run();
		public Object foo(String a);
		public TestMe bar(String[] a, int c, Integer[] b, TestMe d);
	}

	
	@Test
	public void testNoArgNoReturn() {
		ObjRegistry reg = mock(ObjRegistry.class);
		MsgBus bus = mock(MsgBus.class);
		LocalAddress addr = new LocalAddress();
		ProxyFactory factory = new ProxyFactory(reg, new JavaReflectionProxyCreator(), addr, bus);
		
		TestMe testme = mock(TestMe.class);
				
		ObjRef ref = addr.createObjRef(testme);
		when(reg.get(ref)).thenReturn(testme);
		
		ObjInvoker invoker = new ObjInvoker(bus, reg, factory, true);
		
		InvocationInfo target = InvocationInfoBuilder.invocationInfo()
				.withMethod("run")
				.withParameters(new Object[]{})
				.build();
		ObjInvocationMsg invoke = ObjInvocationMsgBuilder.objInvocationMsg()
				.withInvocation(target)
				.withTargetObjectId(ref)
				.withSource(addr.toString())
				.build();
				
		invoker.handleInvocation(invoke);
		
		verify(testme).run();
		
		ArgumentCaptor<ObjInvocationRespMsg> respc = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus).publish(respc.capture());
		
		ObjInvocationRespMsg resp = respc.getValue();
		assertEquals(addr.toString(), resp.getDestination());
		assertNull(resp.getResult());
		assertNull(resp.getException());
		assertEquals(invoke.getId(),resp.getResponseTo());
	}
	
	@Test
	public void testSingleArgAndReturn() {
		ObjRegistry reg = mock(ObjRegistry.class);
		MsgBus bus = mock(MsgBus.class);
		LocalAddress addr = new LocalAddress();
		ProxyFactory factory = new ProxyFactory(reg, new JavaReflectionProxyCreator(), addr, bus);
		
		TestMe testme = mock(TestMe.class);
				
		ObjRef ref = addr.createObjRef(testme);
		when(reg.get(ref)).thenReturn(testme);
		when(testme.foo("a")).thenReturn("b");
		
		ObjInvoker invoker = new ObjInvoker(bus, reg, factory, true);
		
		InvocationInfo target = InvocationInfoBuilder.invocationInfo()
				.withMethod("foo")
				.withParameterTypes(new Class[]{String.class})
				.withParameters(new Object[]{"a"})
				.build();
		ObjInvocationMsg invoke = ObjInvocationMsgBuilder.objInvocationMsg()
				.withInvocation(target)
				.withTargetObjectId(ref)
				.withSource(addr.toString())
				.build();
				
		invoker.handleInvocation(invoke);
		
		verify(testme).foo("a");
		
		ArgumentCaptor<ObjInvocationRespMsg> respc = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus).publish(respc.capture());
		
		ObjInvocationRespMsg resp = respc.getValue();
		assertEquals(addr.toString(), resp.getDestination());
		assertEquals("b",resp.getResult());
		assertNull(resp.getException());
		assertEquals(invoke.getId(),resp.getResponseTo());
	}

	//bar(String[] a, int c, Integer[] b, TestMe d);
	@Test
	public void testMultiArgAndReturn() {
		ObjRegistry reg = mock(ObjRegistry.class);
		MsgBus bus = mock(MsgBus.class);
		LocalAddress addr = new LocalAddress();
		ProxyFactory factory = new ProxyFactory(reg, new JavaReflectionProxyCreator(), addr, bus);
		
		TestMe testme = mock(TestMe.class);
				
		ObjRef ref = addr.createObjRef(testme);
		when(reg.get(ref)).thenReturn(testme);
		when(reg.publish(testme)).thenReturn(ref);
		
		
		ObjInvoker invoker = new ObjInvoker( bus, reg, factory, true);
		
		Object[] args = new Object[]{new String[]{"a","b"},2,new Integer[]{4,5},testme};
		when(testme.bar((String[])args[0],(Integer)args[1],(Integer[])args[2],(TestMe)args[3])).thenReturn(testme);
		
		InvocationInfo target = InvocationInfoBuilder.invocationInfo()
				.withMethod("bar")
				.withParameterTypes(new Class[]{String[].class,int.class,Integer[].class,TestMe.class})
				.withParameters(args)
				.build();
		ObjInvocationMsg invoke = ObjInvocationMsgBuilder.objInvocationMsg()
				.withInvocation(target)
				.withTargetObjectId(ref)
				.withSource(addr.toString())
				.build();
				
		
		SessionData.forClient(addr.toString()).put("foo", "bar");
		
		invoker.handleInvocation(invoke);
		
		assertEquals("bar",Session.get().get("foo"));
		
		verify(testme).bar((String[])args[0],(Integer)args[1],(Integer[])args[2],(TestMe)args[3]);
		
		ArgumentCaptor<ObjInvocationRespMsg> respc = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus).publish(respc.capture());
		
		ObjInvocationRespMsg resp = respc.getValue();
		assertEquals(addr.toString(), resp.getDestination());
		assertEquals(ref,resp.getResult());
		assertNull(resp.getException());
		assertEquals(invoke.getId(),resp.getResponseTo());
	}
	
	//bar(String[] a, int c, Integer[] b, TestMe d);
	@Test
	public void testMultiArgWithObjRefAndReturn() {
		ObjRegistry reg = mock(ObjRegistry.class);
		MsgBus bus = mock(MsgBus.class);
		LocalAddress addr = new LocalAddress();
		ProxyFactory factory = new ProxyFactory(reg, new JavaReflectionProxyCreator(), addr, bus);
		
		TestMe testme = mock(TestMe.class);
				
		ObjRef ref = addr.createObjRef(testme);
		when(reg.get(ref)).thenReturn(testme);
		when(reg.publish(testme)).thenReturn(ref);
		
		
		ObjInvoker invoker = new ObjInvoker(bus, reg, factory, true);
		
		//Note that rather than directly passing "testme" we pass a reference
		//to it as the last arg that must be dynamically converted back to 
		//the right object
		Object[] args = new Object[]{new String[]{"a","b"},2,new Integer[]{4,5},ref};
		when(testme.bar((String[])args[0],(Integer)args[1],(Integer[])args[2],testme)).thenReturn(testme);
		
		InvocationInfo target = InvocationInfoBuilder.invocationInfo()
				.withMethod("bar")
				.withParameterTypes(new Class[]{String[].class,int.class,Integer[].class,TestMe.class})
				.withParameters(args)
				.build();
		ObjInvocationMsg invoke = ObjInvocationMsgBuilder.objInvocationMsg()
				.withInvocation(target)
				.withTargetObjectId(ref)
				.withSource(addr.toString())
				.build();
				
		invoker.handleInvocation(invoke);
		
		verify(testme).bar((String[])args[0],(Integer)args[1],(Integer[])args[2],testme);
		
		ArgumentCaptor<ObjInvocationRespMsg> respc = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus).publish(respc.capture());
		
		ObjInvocationRespMsg resp = respc.getValue();
		assertEquals(addr.toString(), resp.getDestination());
		assertEquals(ref,resp.getResult());
		assertNull(resp.getException());
		assertEquals(invoke.getId(),resp.getResponseTo());
	}
}
