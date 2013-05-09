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
package org.magnum.soda.marshalling;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

public class Marshaller {

	private static final Logger Log = LoggerFactory.getLogger(Marshaller.class);

	private ObjectMapper mapper_ = new ObjectMapper();

	public Marshaller() {
		this(true);
	}
	
	public Marshaller(boolean addtypeinfo) {
		if(addtypeinfo){
			mapper_.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		}
		mapper_.addHandler(new DeserializationProblemHandler() {

			@Override
			public boolean handleUnknownProperty(DeserializationContext ctxt,
					JsonParser jp, JsonDeserializer<?> deserializer,
					Object beanOrClass, String propertyName)
					throws IOException, JsonProcessingException {

				Log.error(
						"Ignoring unknown property [{}] while deserializing [{}]",
						propertyName, beanOrClass);

				return true;
			}

		});
	}
	
	protected ObjectMapper getMapper(){
		return mapper_;
	}

	public String toTransportFormat(Object o) throws Exception {
		String json = mapper_.writeValueAsString(o);
		return json;
	}

	public <T> T fromTransportFormat(Class<T> type, String data)
			throws Exception {
		T obj = mapper_.readValue(data, type);
		return obj;
	}

}
