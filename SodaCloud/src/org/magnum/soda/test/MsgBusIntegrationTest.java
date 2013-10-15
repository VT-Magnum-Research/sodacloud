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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.magnum.soda.DefaultObjRegistry;
import org.magnum.soda.MBassyMsgBus;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.proxy.JavaReflectionProxyCreator;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyFactory;
import org.magnum.soda.svc.ObjInvoker;

public class MsgBusIntegrationTest {

	private LocalAddress addr = new LocalAddress();
	private MBassyMsgBus bus = new MBassyMsgBus();
	private DefaultObjRegistry reg = new DefaultObjRegistry(addr);
	private ProxyFactory factory = new ProxyFactory(reg, new JavaReflectionProxyCreator(), addr, bus);
	@SuppressWarnings("unused")
	private ObjInvoker inovker = new ObjInvoker(bus, reg, factory, true);
	
	@Test
	public void testSimpleProxying() {
		Runnable run = mock(Runnable.class);
		ObjRef ref = reg.publish(run);
		
		assertEquals(run,reg.get(ref));
		
		Runnable proxy = factory.createProxy(Runnable.class, ref);
		
		proxy.run();
		
		TestUtil.sleep(20);
		
		verify(run).run();
	}

	@Test
	public void testDualProxyInvocation() {
		ExecutorService svc = Executors.newSingleThreadExecutor();
		Runnable run = mock(Runnable.class);
		
		ObjRef ref = reg.publish(run);
		ObjRef exec = reg.publish(svc);
		
		assertEquals(run,reg.get(ref));
		
		final Runnable proxy = factory.createProxy(Runnable.class, ref);
		ExecutorService execproxy = factory.createProxy(ExecutorService.class, exec);
		Runnable r2 = new Runnable() {
			
			@Override
			public void run() {
				proxy.run();
			}
		};
		ObjRef ref3 = reg.publish(r2);
		Runnable proxy2 = factory.createProxy(Runnable.class, ref3);
		
		execproxy.submit(proxy2);
		
		TestUtil.sleep(10);
		
		verify(run).run();
	}	
}
