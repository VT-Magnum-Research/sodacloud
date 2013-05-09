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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.magnum.soda.marshalling.Marshaller;
import org.magnum.soda.protocol.generic.UnmarshallingInvocationInfo;
import org.magnum.soda.protocol.generic.UnmarshallingInvocationInfoBuilder;

public class UnmarshallingInvocationInfoTest {

	public static class Foo {
		private String bar_;
		private Foo child_;
		public String getBar() {
			return bar_;
		}
		public void setBar(String bar) {
			bar_ = bar;
		}
		public Foo getChild() {
			return child_;
		}
		public void setChild(Foo child) {
			child_ = child;
		}
		
	}
	
	public static class FooTarget {
		public void get(Foo foo, Class<?> t, int i){
			
		}
	}
	
	@Test
	public void testParameterDeserialization() {
		
		JSONObject child = new JSONObject();
		child.put("bar","child");
		
		JSONObject parent = new JSONObject();
		parent.put("child", child);
		parent.put("bar","parent");
		
		UnmarshallingInvocationInfo inv = UnmarshallingInvocationInfoBuilder
				.unmarshallingInvocationInfo()
				.withMethod("get")
				.withAddedMarshalledParameter(parent)
				.withAddedMarshalledParameter(Foo.class)
				.withAddedMarshalledParameter(7)
				.withMarshaller(new Marshaller(false))
				.build();
		
		inv.bind(new FooTarget());
		Object[] params = inv.getParameters();
			
		assertNotNull(params);
		assertEquals(3,params.length);
		assertEquals(Foo.class, params[0].getClass());
		
		Foo f = (Foo)params[0];
		assertEquals("parent",f.getBar());
		assertNotNull(f.getChild());
		assertEquals("child",f.getChild().getBar());
		
		assertEquals(Foo.class, params[1]);
		assertEquals(7,params[2]);
	}

}
