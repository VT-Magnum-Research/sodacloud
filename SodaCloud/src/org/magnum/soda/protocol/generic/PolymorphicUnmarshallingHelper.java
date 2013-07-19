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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.magnum.soda.marshalling.Marshaller;
import org.magnum.soda.proxy.ObjRef;

public class PolymorphicUnmarshallingHelper {
	private static final String URI = "uri";
	private static final String OBJ_REF = "ObjRef";
	private static final String TYPE = "type";

	private JSONParser parser_ = new JSONParser();
	private Marshaller marshaller_;

	public PolymorphicUnmarshallingHelper(Marshaller m) {
		marshaller_ = m;
	}

	public Object[] unmarshall(Class<?>[] types, JSONArray values) {
		Object[] params = new Object[types.length];

		for (int i = 0; i < params.length; i++) {
			Class<?> type = types[i];
			Object val = values.get(i);
			if (isJsonObjRef(val)) {
				ObjRef ref = new ObjRef("" + ((JSONObject) val).get(URI),
						types[i].getName());
				params[i] = ref;
			} else {
				String data = values.get(i).toString();
				params[i] = unmarshall(type, data);
			}
		}

		return params;
	}

	private boolean isJsonObjRef(Object val) {
		return val instanceof JSONObject
				&& OBJ_REF.equals(((JSONObject) val).get(TYPE));
	}

	public Object unmarshall(Class<?> type, String data) {
		try {

			return (isPrimitive(type)) ? unMarshallPrimitive(type, data)
					: unmarshallObject(type, data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object unmarshallObject(Class<?> type, String data) {
		Object rslt = null;
		try {
			Object pval = parser_.parse(data);
			if(!(pval instanceof JSONObject) && !(pval instanceof JSONArray)){
				return pval;
			}
			else if (pval instanceof JSONArray){
				rslt = marshaller_.fromTransportFormat(type, data);
			}
			else if (isJsonObjRef((JSONObject)pval)) {
				JSONObject obj = (JSONObject)pval;
				rslt = new ObjRef("" + obj.get(URI), type.getName());
			} else {
				rslt = marshaller_.fromTransportFormat(type, data);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return rslt;
	}

	public boolean isPrimitive(Class<?> type) {
		return (type == Integer.class || type == int.class
				|| type == Double.class || type == double.class
				|| type == Float.class || type == float.class
				|| type == Long.class || type == long.class
				|| type == Boolean.class || type == boolean.class
				|| type == Byte.class || type == byte.class
				|| type == Short.class || type == short.class
				|| type == String.class || type == Class.class);
	}

	public Object unMarshallPrimitive(Class<?> type, String json) {
		if (type == Integer.class || type == int.class) {
			return Integer.parseInt(json);
		} else if (type == Double.class || type == double.class) {
			return Double.parseDouble(json);
		} else if (type == Float.class || type == float.class) {
			return Float.parseFloat(json);
		} else if (type == Long.class || type == long.class) {
			return Long.parseLong(json);
		} else if (type == Boolean.class || type == boolean.class) {
			return Boolean.parseBoolean(json);
		} else if (type == Byte.class || type == byte.class) {
			return Byte.parseByte(json);
		} else if (type == Short.class || type == short.class) {
			return Short.parseShort(json);
		} else if (type == Class.class) {
			json = (json.startsWith("class ")) ? json.substring(6) : json;
			try {
				return Class.forName(json);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} else {
			return json;
		}
	}
}