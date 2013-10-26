/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.server.wamp.test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Test;
import org.magnum.soda.server.wamp.CglibProxyCreator;

public class CglibProxyCreatorTest {

	public static class Foo {
		
		public boolean bar(){
			return false;
		}
	}
	
	public static class FooWCons {
		
		public FooWCons(boolean b){
			
		}
		
		public boolean bar(){
			return false;
		}
	}
	
	@Test
	public void testProxyConcreteWithoutConstructor() {
		CglibProxyCreator ctor = new CglibProxyCreator(getClass().getClassLoader());
		Foo foo = (Foo)ctor.createProxy(ctor.getProxyClassLoader(), new Class[]{Foo.class}, new InvocationHandler() {
			
			private Foo foo_ = new Foo();
			
			@Override
			public Object invoke(Object arg0, Method arg1, Object[] arg2)
					throws Throwable {
				if(arg1.getName().equals("bar")){
					return true;
				}
				else {
					return arg1.invoke(foo_, arg2);
				}
			}
		});
		
		assertTrue(foo.bar());
	}
	
	
	@Test
	public void testProxyConcreteWithConstructor() {
		CglibProxyCreator ctor = new CglibProxyCreator(getClass().getClassLoader());
		FooWCons foo = (FooWCons)ctor.createProxy(ctor.getProxyClassLoader(), new Class[]{FooWCons.class}, new InvocationHandler() {
			
			private FooWCons foo_ = new FooWCons(false);
			
			@Override
			public Object invoke(Object arg0, Method arg1, Object[] arg2)
					throws Throwable {
				if(arg1.getName().equals("bar")){
					return true;
				}
				else {
					return arg1.invoke(foo_, arg2);
				}
			}
		});
		
		assertTrue(foo.bar());
	}

}
