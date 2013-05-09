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
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.magnum.soda.MsgBus;
import org.magnum.soda.ObjRegistry;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.proxy.JavaReflectionProxyCreator;
import org.magnum.soda.proxy.ObjProxy.ResponseCatcher;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyFactory;
import org.magnum.soda.proxy.SodaAsync;
import org.magnum.soda.svc.InvocationInfo;
import org.magnum.soda.svc.ObjInvocationMsg;
import org.magnum.soda.svc.ObjInvocationRespMsg;
import org.mockito.ArgumentCaptor;

public class ObjProxyTest {

	public interface ObjectSync {
		@SodaAsync
		public void async();

		public void sync();
	}

	public interface TestObj {
		public boolean equals(Object o);

		public String getName();

		public void setFoo(String a, Boolean b);

		public String[] calcPrefixes(String[] base, String mod, Object[] foo);

		public TestObj link(TestObj obj);
	}

	private boolean reply_ = false;

	private String replyString_ = null;

	private String[] replyStrings_;

	private TestObj replyObj_;

	private LocalAddress myAddress_ = new LocalAddress();

	@Test
	public void testNonProxyParamsAndReturnValue() {
		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(TestObj.class);
		final TestObj b = factory.createProxy(TestObj.class, ref);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				reply_ = b.equals(false);
			}
		});
		t.start();

		extractAndVerifyInvocationMsg(bus, ref, "equals",
				new Object[] { false }, true);

		assertEquals(true, reply_);

	}

	@Test
	public void testNonProxyNoParamsAndReturnValue() {
		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(TestObj.class);
		final TestObj b = factory.createProxy(TestObj.class, ref);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				replyString_ = b.getName();
			}
		});
		t.start();

		extractAndVerifyInvocationMsg(bus, ref, "getName", new Object[] {},
				"asdf");

		assertEquals("asdf", replyString_);
	}

	@Test
	public void testTwoNonProxyParamsVoidReturn() {
		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(TestObj.class);
		final TestObj b = factory.createProxy(TestObj.class, ref);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				b.setFoo("asdf", true);
			}
		});
		t.start();

		extractAndVerifyInvocationMsg(bus, ref, "setFoo", new Object[] {
				"asdf", true }, null);

	}

	@Test
	public void testNonProxyMultipleParamsArraysAndArrayReturn() {
		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(TestObj.class);
		final TestObj b = factory.createProxy(TestObj.class, ref);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				replyStrings_ = b.calcPrefixes(new String[] { "a", "b" }, "c",
						null);
			}
		});
		t.start();

		extractAndVerifyInvocationMsg(bus, ref, "calcPrefixes", new Object[] {
				new String[] { "a", "b" }, "c", null },
				new String[] { "e", "f" });

		assertArrayEquals(new String[] { "e", "f" }, replyStrings_);
	}

	@Test
	public void testNonProxyChainedInvocations() {
		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(TestObj.class);
		final TestObj b = factory.createProxy(TestObj.class, ref);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String a = b.getName();
				boolean v = b.equals(a);
				replyStrings_ = b.calcPrefixes(new String[] { "a", "b" }, a,
						new Object[] { v });
			}
		});
		t.start();

		extractAndVerifyInvocationMsg(bus, ref, "getName", new Object[] {}, "a");
		extractAndVerifyInvocationMsg(bus, ref, "equals", new Object[] { "a" },
				true);
		extractAndVerifyInvocationMsg(bus, ref, "calcPrefixes", new Object[] {
				new String[] { "a", "b" }, "a", new Object[] { true } },
				new String[] { "22" });

		assertArrayEquals(new String[] { "22" }, replyStrings_);
	}

	@Test
	public void testProxyAsParam() {
		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(TestObj.class);
		final TestObj b = factory.createProxy(TestObj.class, ref);
		final TestObj b2 = factory.createProxy(TestObj.class, ref);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				replyObj_ = b.link(b2);
			}
		});
		t.start();

		extractAndVerifyInvocationMsg(bus, ref, "link", new Object[] { ref },
				ref);
		verify(reg).insert(ref, b2);

		assertEquals(b2, replyObj_);
	}

	@Test
	public void testAutoPublishObjectParam() {
		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(TestObj.class);
		ObjRef ref2 = myAddress_.createObjRef(TestObj.class);
		final TestObj b = factory.createProxy(TestObj.class, ref);
		final TestObj b2 = new TestObj() {

			@Override
			public void setFoo(String a, Boolean b) {

			}

			@Override
			public TestObj link(TestObj obj) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String[] calcPrefixes(String[] base, String mod, Object[] foo) {
				// TODO Auto-generated method stub
				return null;
			}
		};

		when(reg.publish(b2)).thenReturn(ref2);
		when(reg.get(ref2)).thenReturn(b2);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				replyObj_ = b.link(b2);
			}
		});
		t.start();

		extractAndVerifyInvocationMsg(bus, ref, "link", new Object[] { ref2 },
				ref2);
		verify(reg).publish(b2);

		assertEquals(b2, replyObj_);
	}

	@Test
	public void testSyncAndAsyncBehavior() {
		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(ObjectSync.class);
		final ObjectSync b = factory.createProxy(ObjectSync.class, ref);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				reply_ = false;
				b.async();
				reply_ = true;
			}
		});
		t.start();

		sleep(50);
		assertEquals(true, reply_);

		t = new Thread(new Runnable() {

			@Override
			public void run() {
				reply_ = false;
				b.sync();
				reply_ = true;
			}
		});
		t.start();

		sleep(50);
		assertEquals(false, reply_);

	}

	@Test
	public void testEqualsToStringHashcode() {

		MsgBus bus = mock(MsgBus.class);
		ObjRegistry reg = mock(ObjRegistry.class);
		ProxyFactory factory = new ProxyFactory(reg,
				new JavaReflectionProxyCreator(), myAddress_, bus);

		ObjRef ref = myAddress_.createObjRef(TestObj.class);
		final TestObj b = factory.createProxy(TestObj.class, ref);
		final TestObj b2 = factory.createProxy(TestObj.class, ref);

		assertEquals(b, b2);
		assertEquals(b.hashCode(), b2.hashCode());
		assertEquals(b.toString(), b2.toString());
	}

	private void sleep(int time) {
		TestUtil.sleep(time);
	}

	private ObjInvocationMsg extractAndVerifyInvocationMsg(MsgBus bus,
			ObjRef objid, String method, Object[] args, Object returnval) {

		sleep(10);

		ArgumentCaptor<ResponseCatcher> scaptor = ArgumentCaptor
				.forClass(ResponseCatcher.class);
		ArgumentCaptor<ObjInvocationMsg> icaptor = ArgumentCaptor
				.forClass(ObjInvocationMsg.class);
		verify(bus, atLeast(1)).subscribe(scaptor.capture());
		verify(bus, atLeast(1)).publish(icaptor.capture());

		ObjInvocationMsg inv = icaptor.getValue();
		assertEquals(objid, inv.getTargetObjectId());

		InvocationInfo iinfo = inv.getInvocation();
		assertEquals(method, iinfo.getMethod());
		assertEquals(args.length, iinfo.getParameters().length);

		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof Object[]) {
					assertArrayEquals((Object[]) args[i],
							(Object[]) iinfo.getParameters()[i]);
				} else {
					assertEquals(args[i], iinfo.getParameters()[i]);
				}
			}
		}

		ObjInvocationRespMsg resp = new ObjInvocationRespMsg(inv, returnval);
		ResponseCatcher catcher = scaptor.getValue();
		catcher.handleResponse(resp);

		sleep(10);

		return inv;
	}

}
