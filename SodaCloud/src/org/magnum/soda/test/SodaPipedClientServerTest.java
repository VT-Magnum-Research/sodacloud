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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.magnum.soda.Soda;
import org.magnum.soda.svc.AuthService;
import org.magnum.soda.transport.LocalPipeTransport;

public class SodaPipedClientServerTest {

	private byte[] data_;
	
	public interface Foo {
		public void recv(byte[] data);
	}
	
	public class FooImpl implements Foo{
		public void recv(byte[] data){
			System.out.println(data);
			data_ = data;
		}
	}
	
	@Test
	public void testBinary() throws Exception {
		Soda server = new Soda(true);
		Soda client = new Soda();
		
		server.setAllowNonLocalProxyInvocations(true);
		client.setAllowNonLocalProxyInvocations(true);
		
		LocalPipeTransport transport = new LocalPipeTransport(server, client);
		client.connect(transport.getClientTransport(),null);
		
		FooImpl f = new FooImpl();
		server.bind(f,"foo");
		
		byte[] d = new SecureRandom().generateSeed(1025);
		Foo fp = client.get(Foo.class, "foo");
		fp.recv(d);
		
		TestUtil.sleep(10);
		
		assertArrayEquals(d, data_);
	}
	
	@Test
	public void testNativeJavaProtocol() throws Exception {
		
		// This test emulates both a client and server on the same machine
		// but does full messaging and marshalling as if a real network
		// connection were being used.
		Soda server = new Soda(true);
		Soda client = new Soda();
		
		server.setAllowNonLocalProxyInvocations(true);
		client.setAllowNonLocalProxyInvocations(true);
		
		// Extra boiler plate code b/c we want to fake a network connection
		// with this funky pipe transport
		LocalPipeTransport transport = new LocalPipeTransport(server, client);
		client.connect(transport.getClientTransport(),null);
		
		// A fake runnable
		Runnable r = mock(Runnable.class);
		
		
		// First, we create an executor service and bind
		// it to the name "executor" on the server's NamingService
		ExecutorService exc = Executors.newFixedThreadPool(2);
		server.bind(exc, "executor");
		
		// Now, on the client side, we lookup a proxy to the ExecutorService
		// that is sitting on the server
		ExecutorService r2 = client.get(ExecutorService.class, "executor");
		
		// <CrazyStuff>
		//
		// Once we have the proxy to the ExecutorService, we can use it like
		// a normal object and submit tasks to it. The Runnable that we pass in
		// is automatically converted to an ObjRef and passed to the server. The
		// server constructs a Java dynamic proxy using the ref, the executor
		// invokes run() on the proxy, the proxy sends a msg back to the client
		// to invoke run, the server blocks until it receives a response msg
		// indicating run() has returned, the server returns a future, the
		// client receives the future as an ObjRef, the client constructs a
		// proxy with the ref, the code below invokes get() on the proxy, the
		// proxy sends a msg to the server to invoke get... yeah...it works!
		//
		// </CrazyStuff>
		
		// f is actually a proxy to the real future which is on the
		// server-side
		Future<?> f = r2.submit(r);
		
		// This is invoking get() on the proxy which delegates the call
		// to the server via a msg and waits for a response msg with the
		// return value
		f.get();
		
		// Prove that this crazy interaction just worked
		verify(r).run();
	}
	
	
	@Test
	public void testNativeJavaProtocolWithAuth() throws Exception {
		
		// This test emulates both a client and server on the same machine
		// but does full messaging and marshalling as if a real network
		// connection were being used.
		AuthService auth = mock(AuthService.class);
		
		Soda server = new Soda(true, auth);
		Soda client = new Soda();
		
		server.setAllowNonLocalProxyInvocations(true);
		client.setAllowNonLocalProxyInvocations(true);
		
		// Extra boiler plate code b/c we want to fake a network connection
		// with this funky pipe transport
		LocalPipeTransport transport = new LocalPipeTransport(server, client);
		client.connect(transport.getClientTransport(),null);
		
		// A fake runnable
		Runnable r = mock(Runnable.class);
		
		
		// First, we create an executor service and bind
		// it to the name "executor" on the server's NamingService
		ExecutorService exc = Executors.newFixedThreadPool(2);
		server.bind(exc, "executor");
		
		// Now, on the client side, we lookup a proxy to the ExecutorService
		// that is sitting on the server
		ExecutorService r2 = client.get(ExecutorService.class, "executor");
		
		// <CrazyStuff>
		//
		// Once we have the proxy to the ExecutorService, we can use it like
		// a normal object and submit tasks to it. The Runnable that we pass in
		// is automatically converted to an ObjRef and passed to the server. The
		// server constructs a Java dynamic proxy using the ref, the executor
		// invokes run() on the proxy, the proxy sends a msg back to the client
		// to invoke run, the server blocks until it receives a response msg
		// indicating run() has returned, the server returns a future, the
		// client receives the future as an ObjRef, the client constructs a
		// proxy with the ref, the code below invokes get() on the proxy, the
		// proxy sends a msg to the server to invoke get... yeah...it works!
		//
		// </CrazyStuff>
		
		// f is actually a proxy to the real future which is on the
		// server-side
		Future<?> f = r2.submit(r);
		
		// This is invoking get() on the proxy which delegates the call
		// to the server via a msg and waits for a response msg with the
		// return value
		f.get();
		
		// Prove that this crazy interaction just worked
		verify(r).run();
	}

}
