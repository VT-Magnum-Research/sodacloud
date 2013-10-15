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
package org.magnum.soda.test.protocols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;
import org.magnum.soda.Soda;
import org.magnum.soda.protocol.generic.DefaultProtocol;
import org.magnum.soda.transport.LocalPipeTransport;

public class DefaultProtocolTest {


	@Test
	public void testDefaultProtocol() throws Exception {
		
		// This test emulates both a client and server on the same machine
		// but does full messaging and marshalling as if a real network
		// connection were being used.
		Soda server = new Soda(true);
		Soda client = new Soda();
		
		server.setAllowNonLocalProxyInvocations(true);
		client.setAllowNonLocalProxyInvocations(true);
		
		// Extra boiler plate code b/c we want to fake a network connection
		// with this funky pipe transport
		LocalPipeTransport transport = new LocalPipeTransport(server, client, new DefaultProtocol());
		client.connect(transport.getClientTransport(),null);
		
		server.bind(new TaskCoordinatorImpl(), "coordinator");
		
		TaskCoordinator clientCoord = client.get(TaskCoordinator.class, "coordinator");
		TaskImpl[] tasks = new TaskImpl[10];
		for(int i = 0; i < tasks.length; i++){
			tasks[i] = new TaskImpl(UUID.randomUUID().toString());
			clientCoord.addTask(tasks[i]);
		}
		
		clientCoord.runAll();
		clientCoord.waitForCompletion();
		
		for(int i = 0; i < tasks.length; i++){
			TaskImpl task = tasks[i];
			TaskResult r = clientCoord.getResultFor(task);
			assertNotNull(r);
			assertEquals(task.getName(),r.getOutput());
			assertEquals(task.getName().hashCode(),r.getResultCode());
		}
		
		assertEquals(tasks[tasks.length - 1], clientCoord.nthTask(tasks.length-1));
	}
}
