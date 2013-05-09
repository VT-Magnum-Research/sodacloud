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
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.magnum.soda.Soda;
import org.magnum.soda.svc.InvocationDispatcher;
import org.magnum.soda.svc.InvocationInfo;
import org.mockito.ArgumentCaptor;

public class RecordingProxyTest {
	
	public static class FooImpl extends Bar implements Iter {
		public void doSomething(){}
	}
	
	public interface Iter {
		public void doSomething();
	}
	
	public interface Foo {
		public Iter getFoo();
	}
	
	public static class Bar implements Runnable, Foo{
		public void run(){
			
		}
		public FooImpl getFoo(){
			return new FooImpl();
		}
	}

	@Test
	public void testSingleInvocation() throws Exception {
		Soda s = new Soda();
		Runnable r = mock(Runnable.class);
		InvocationDispatcher d = mock(InvocationDispatcher.class);
		
		s.invoke(r,d).run();
		
		ArgumentCaptor<InvocationInfo> inv = ArgumentCaptor.forClass(InvocationInfo.class);
		verify(d).dispatch(inv.capture(), eq(r));
		
		InvocationInfo ii = inv.getValue();
		assertEquals("run",ii.getMethod());
		assertEquals(null,ii.getParameters());
	}
	
	@Test
	public void testChainedInvocation() throws Exception {
		Soda s = new Soda();
		Foo r = mock(Foo.class);
		FooImpl fi = mock(FooImpl.class);
		InvocationDispatcher d = mock(InvocationDispatcher.class);
		when(r.getFoo()).thenReturn(fi);
		when(d.dispatch(any(InvocationInfo.class), any(Object.class))).thenReturn(fi);
		assertNotNull(r.getFoo());
		
		s.invoke(r,d).getFoo().doSomething();
		
		ArgumentCaptor<InvocationInfo> inv = ArgumentCaptor.forClass(InvocationInfo.class);
		verify(d).dispatch(inv.capture(), eq(r));
		
		InvocationInfo ii = inv.getValue();
		assertEquals("getFoo",ii.getMethod());
		assertEquals(null,ii.getParameters());
		
		inv = ArgumentCaptor.forClass(InvocationInfo.class);
		verify(d).dispatch(inv.capture(), eq(fi));
		
		ii = inv.getValue();
		assertEquals("doSomething",ii.getMethod());
		assertEquals(null,ii.getParameters());
	}

}
