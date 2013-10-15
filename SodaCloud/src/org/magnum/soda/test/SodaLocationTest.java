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
import org.magnum.soda.ctx.SodaLocation;
import org.magnum.soda.ctx.SodaLocation.Accuracy;
import org.magnum.soda.ctx.SodaLocation.Proximity;
import org.mockito.ArgumentCaptor;

public class SodaLocationTest {

	@Test
	public void testWriterDrivenGeoLookup() {

		Soda soda = new Soda();

		Runnable r = mock(Runnable.class);

		soda.bind(r ).to(SodaLocation.within(Proximity.TWENTY_METERS)
				.of(45.0, -100.0).atAccuracy(Accuracy.FINE));

		List<Runnable> l = soda.find(Runnable.class, SodaLocation.at(45.0, -100.0)).now();

		assertNotNull(l);
		assertSame(r, l.get(0));
	}
	
	@Test
	public void testWriterDrivenGeoLookup2() {

		Soda soda = new Soda();

		Runnable r = mock(Runnable.class);

		soda.bind(r ).to(SodaLocation.within(Proximity.TWENTY_METERS)
				.of("cb0b").atAccuracy(Accuracy.FINE));

		List<Runnable> l = soda.find(Runnable.class, SodaLocation.at("cb0b")).now();

		assertNotNull(l);
		assertSame(r, l.get(0));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testWriterDrivenAsyncGeoLookup() {

		Soda soda = new Soda();

		Runnable r = mock(Runnable.class);
		Callback<List<Runnable>> hdlr = mock(Callback.class);
		soda.bind(r ).to(SodaLocation.within(Proximity.TWENTY_METERS)
				.of(45.0, -100.0).atAccuracy(Accuracy.FINE));

		soda.find(Runnable.class, SodaLocation.at(45.0, -100.0)).async(hdlr);

		TestUtil.sleep(10);
		
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(hdlr).handle(captor.capture());
		List l = captor.getValue();
		assertEquals(1,l.size());
		assertTrue(l.contains(r));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testWriterDrivenAsyncListGeoLookup() {

		Soda soda = new Soda();

		Runnable r = mock(Runnable.class);
		Callback<List<Runnable>> hdlr = (Callback<List<Runnable>>)mock(Callback.class);
		soda.bind(r ).to(SodaLocation.within(Proximity.TWENTY_METERS)
				.of(45.0, -100.0).atAccuracy(Accuracy.FINE));

		soda.find(Runnable.class, SodaLocation.at(45.0, -100.0)).async(hdlr);

		TestUtil.sleep(10);
		
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(hdlr).handle(captor.capture());
		List l = captor.getValue();
		assertEquals(1,l.size());
		assertTrue(l.contains(r));
	}

	@Test
	public void testReaderDrivenGeoLookup() {
		Soda soda = new Soda();
		Runnable r = mock(Runnable.class);
		soda.bind(r ).to(SodaLocation.at(45.0, -100));

		List<Runnable> r2 = soda.find(Runnable.class, SodaLocation
				.within(Proximity.TWENTY_METERS).of(45.0, -100.0)
				.atAccuracy(Accuracy.FINE)).now();
		
		assertNotNull(r2);
		assertSame(r, r2.get(0));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testReaderDrivenAsyncGeoLookup() {
		Soda soda = new Soda();
		Runnable r = mock(Runnable.class);
		Callback<List<Runnable>> hdlr = mock(Callback.class);
		soda.bind(r ).to(SodaLocation.at(45.0, -100));

		soda.find(Runnable.class, SodaLocation
				.within(Proximity.TWENTY_METERS).of(45.0, -100.0)
				.atAccuracy(Accuracy.FINE)).async(hdlr);
		
		TestUtil.sleep(10);
		
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(hdlr).handle(captor.capture());
		List l = captor.getValue();
		assertEquals(1,l.size());
		assertTrue(l.contains(r));
	}
	
	
	public interface TestA {}
	public class A1 implements TestA{}
	public class A2 extends A1{}
	public class TestC extends A2{}
	
	
	public interface TestB {}	
	public class B1 implements TestB{}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testPolymorphicLookup() {
		Soda soda = new Soda();
		TestC r=new TestC();//Subclass of A1( implements interface TestA)
		//TestC r = mock(TestC.class);
		TestB r2=new B1();//class implementation of TestB
		//TestB r2 = mock(TestB.class);
		TestA r3=new A1();//class implementation of TestA
		//TestA r3 = mock(TestA.class);
		
		Callback<List<TestA>> hdlr = mock(Callback.class);
		
		soda.bind(r ).to(SodaLocation.at(45.0, -100));
		soda.bind(r2).to(SodaLocation.at(45.0, -100));
		soda.bind(r3).to(SodaLocation.at(45.0, -100));
		
	/*	SodaQR qr2 = SodaQR.create();
		soda.bind(r2).to(qr2);
		soda.bind(r3).to(qr2);
*/
		soda.find(TestA.class, SodaLocation
				.within(Proximity.TWENTY_METERS).of(45.0, -100.0)
				.atAccuracy(Accuracy.FINE)).async(hdlr);
		
		TestUtil.sleep(10);
		
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(hdlr).handle(captor.capture());
		List l = captor.getValue();
		assertEquals(2,l.size());
		assertTrue(l.contains(r));
		assertTrue(l.contains(r3));
		
	}

}
