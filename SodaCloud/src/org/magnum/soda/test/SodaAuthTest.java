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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.magnum.soda.MsgBus;
import org.magnum.soda.ObjRegistry;
import org.magnum.soda.SodaAuth;
import org.magnum.soda.aop.DefaultInvocationProcessorFactory;
import org.magnum.soda.auth.AuthInvocationProcessor;
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
import org.magnum.soda.svc.SessionData;
import org.mockito.ArgumentCaptor;

public class SodaAuthTest {

	public interface TestSvc {
		
		public void svc();
		public void svc2();
		public void svc3();
	}
	
	public static class TestSvcImpl implements TestSvc{
		@SodaAuth("Admin")
		public void svc(){
			
		}
		
		@SodaAuth("User")
		public void svc2(){
			
		}
		
		@SodaAuth({"User","Admin"})
		public void svc3(){
			
		}
	}
	
	@Test
	public void test() {
		DefaultInvocationProcessorFactory fact = new DefaultInvocationProcessorFactory();
		fact.addProcessor(SodaAuth.class, AuthInvocationProcessor.class);
		
		ObjRegistry reg = mock(ObjRegistry.class);
		MsgBus bus = mock(MsgBus.class);
		LocalAddress addr = new LocalAddress();
		ProxyFactory factory = new ProxyFactory(reg, new JavaReflectionProxyCreator(), addr, bus);
				
		TestSvc svc = new TestSvcImpl();
		ObjRef ref = addr.createObjRef(svc);
		when(reg.get(ref)).thenReturn(svc);
		when(reg.publish(svc)).thenReturn(ref);
		
		
		ObjInvoker invoker = new ObjInvoker( bus, reg, factory, true);
		invoker.setProcessorFactory(fact);
		
		Object[] args = new Object[]{};
		
		InvocationInfo target = InvocationInfoBuilder.invocationInfo()
				.withMethod("svc")
				.withParameterTypes(new Class[]{})
				.withParameters(args)
				.build();
		ObjInvocationMsg invoke = ObjInvocationMsgBuilder.objInvocationMsg()
				.withInvocation(target)
				.withTargetObjectId(ref)
				.withSource(addr.toString())
				.build();
				
		
		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{"asdf"});
		
		invoker.handleInvocation(invoke);
		
		ArgumentCaptor<ObjInvocationRespMsg> captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus).publish(captor.capture());
		
		ObjInvocationRespMsg resp = captor.getValue();
		assertNotNull(resp.getException());
		
		
		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{"Admin"});
		
		invoker.handleInvocation(invoke);
		
		captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus,times(2)).publish(captor.capture());
		
		resp = captor.getValue();
		assertNull(resp.getException());
		
		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{"User","Admin"});
		
		invoker.handleInvocation(invoke);
		
		captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus,times(3)).publish(captor.capture());
		
		resp = captor.getValue();
		assertNull(resp.getException());
		
		
		invoke.getInvocation().setMethod("svc2");
		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{"Admin"});
		
		invoker.handleInvocation(invoke);
		
		captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus,times(4)).publish(captor.capture());
		
		resp = captor.getValue();
		assertNotNull(resp.getException());
		
		
		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{"Admin","User"});
		
		invoker.handleInvocation(invoke);
		
		captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus,times(5)).publish(captor.capture());
		
		resp = captor.getValue();
		assertNull(resp.getException());
		
		
		invoke.getInvocation().setMethod("svc3");
		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{});
		
		invoker.handleInvocation(invoke);
		
		captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus,times(6)).publish(captor.capture());
		
		resp = captor.getValue();
		assertNotNull(resp.getException());
		

		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{"User"});
		
		invoker.handleInvocation(invoke);
		
		captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus,times(7)).publish(captor.capture());
		
		resp = captor.getValue();
		assertNull(resp.getException());
		
		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{"Admin"});
		
		invoker.handleInvocation(invoke);
		
		captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus,times(8)).publish(captor.capture());
		
		resp = captor.getValue();
		assertNull(resp.getException());
		
		SessionData.forClient(addr.toString()).put(SodaAuth.SESSION_ROLES_VARIABLE, new String[]{"none","User"});
		
		invoker.handleInvocation(invoke);
		
		captor = ArgumentCaptor.forClass(ObjInvocationRespMsg.class);
		verify(bus,times(9)).publish(captor.capture());
		
		resp = captor.getValue();
		assertNull(resp.getException());
	}

}
