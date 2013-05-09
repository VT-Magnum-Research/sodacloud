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
package org.magnum.soda.protocol.generic;

import java.io.IOException;
import java.lang.reflect.Method;

import org.magnum.soda.proxy.ObjRef;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ObjRefSerializer extends StdSerializer<ObjRef> {

		public ObjRefSerializer(){
			super(ObjRef.class);
		}
	
		@Override
		public void serialize(ObjRef value, JsonGenerator jgen,
		        SerializerProvider provider) throws IOException,
		        JsonProcessingException {
		    jgen.writeStartObject();
		    jgen.writeStringField("type", "ObjRef");
		    jgen.writeStringField("uri", value.getUri());
		    jgen.writeStringField("host", value.getHost());
		    jgen.writeArrayFieldStart("interface");
		    for(String type : value.getTypes()){
		    	try{
		    		Class<?> c = Class.forName(type);
		    		for(Method m : c.getMethods()){
		    			jgen.writeString(m.getName()+"/"+m.getParameterTypes().length);
		    		}
		    	}
		    	catch(Exception e){}
		    }
		    jgen.writeEndArray();
		    jgen.writeEndObject();
		}

		@Override
		public void serializeWithType(ObjRef value, JsonGenerator jgen,
				SerializerProvider provider, TypeSerializer typeSer)
				throws IOException, JsonProcessingException {
			serialize(value, jgen, provider);
		}
		
		
}
