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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testQRDrivenLookup() {
		Soda soda = new Soda();
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
		Soda soda = new Soda();
		Runnable r = mock(Runnable.class);
		
		SodaQR qr = SodaQR.create();
		soda.bind(r).to(qr);

		byte[] data = qr.getImageData();
		List<Runnable> l = soda.find(Runnable.class, SodaQR.fromImageData(data)).now();
		
		TestUtil.sleep(10);
		
		assertNotNull(l);
		assertSame(r, l.get(0));
	}
	
	public interface TestA {}
	public class A1 implements TestA{}
	public class A2 extends A1{}
	public class TestC extends A2{}
	
	
	public interface TestB {}	
	public class B1 implements TestB{}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testQRDrivenLookupPolymorphic() {
		Soda soda = new Soda();
		
		//Using real objects to get the hierarchy
		TestC r=new TestC();
		//TestC r = mock(TestC.class);
		TestB r2=new B1();
		//TestB r2 = mock(TestB.class);
		TestA r3=new A1();
		//TestA r3 = mock(TestA.class);
		
		Callback<List<TestA>> hdlr = mock(Callback.class);
		Callback<List<TestA>> hdlr1 = mock(Callback.class);
		
		SodaQR qr = SodaQR.create("1");
		soda.bind(r).to(qr);
				
		SodaQR qr2 = SodaQR.create("2");
		soda.bind(r2).to(qr2);
		soda.bind(r3).to(qr2);

		byte[] data = qr.getImageData();
		soda.find(TestA.class, SodaQR.fromImageData(data)).async(hdlr);
		
		TestUtil.sleep(100);
		
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(hdlr).handle(captor.capture());
		List l = captor.getValue();
		assertEquals(1,l.size());
		assertTrue(l.contains(r));
		
		
		
		data = qr2.getImageData();
		soda.find(TestA.class, SodaQR.fromImageData(data)).async(hdlr1);
		
		TestUtil.sleep(100);

		captor = ArgumentCaptor.forClass(List.class);
		verify(hdlr1).handle(captor.capture());
		l = captor.getValue();
		assertEquals(1,l.size());
		assertTrue(l.contains(r3));
	}

}
