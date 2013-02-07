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

import static org.junit.Assert.*;

import org.junit.Test;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.proxy.ObjRef;

public class LocalAddressTest {

	@Test
	public void test() {
		LocalAddress addr = new LocalAddress();
		ObjRef ref1 = addr.createObjRef(Runnable.class);
		ObjRef ref2 = addr.createObjRef(Runnable.class);
		assertNotNull(ref1);
		assertTrue(addr.isALocalObject(ref1));
		assertFalse(ref1.equals(ref2));
	}

}
