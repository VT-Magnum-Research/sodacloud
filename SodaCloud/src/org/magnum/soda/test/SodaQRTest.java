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
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.magnum.soda.Callback;
import org.magnum.soda.Soda;
import org.magnum.soda.ctx.SodaQR;
import org.mockito.ArgumentCaptor;

public class SodaQRTest {

	@Test
	public void testQRDrivenLookup() {
		Soda soda = null;
		Runnable r = mock(Runnable.class);
		Callback<List<Runnable>> hdlr = mock(Callback.class);
		
		SodaQR qr = SodaQR.create();
		soda.bind(r).to(qr);

		byte[] data = qr.getImageData();
		soda.find(Runnable.class, SodaQR.fromImageData(data)).async(hdlr);
		
		TestUtil.sleep(10);
		
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(hdlr).handle(captor.capture());
		List l = captor.getValue();
		assertEquals(1,l.size());
		assertTrue(l.contains(r));
	}
	
	@Test
	public void testQRDrivenAsyncLookup() {
		Soda soda = null;
		Runnable r = mock(Runnable.class);
		Callback<Runnable> hdlr = mock(Callback.class);
		
		SodaQR qr = SodaQR.create();
		soda.bind(r).to(qr);

		byte[] data = qr.getImageData();
		List<Runnable> l = soda.find(Runnable.class, SodaQR.fromImageData(data)).now();
		
		TestUtil.sleep(10);
		
		assertNotNull(l);
		assertSame(r, l.get(0));
	}
}
