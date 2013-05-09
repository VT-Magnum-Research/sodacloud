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

import java.lang.reflect.Method;

import org.json.simple.JSONArray;
import org.magnum.soda.marshalling.Marshaller;
import org.magnum.soda.svc.InvocationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnmarshallingInvocationInfo extends InvocationInfo {

	private static final Logger Log = LoggerFactory
			.getLogger(UnmarshallingInvocationInfo.class);

	private PolymorphicUnmarshallingHelper helper_;

	private Marshaller marshaller_;

	private JSONArray marshalledParameters_;

	public JSONArray getMarshalledParameters() {
		return marshalledParameters_;
	}

	public void setMarshalledParameters(JSONArray marshalledParameters) {
		marshalledParameters_ = marshalledParameters;
	}

	public Marshaller getMarshaller() {
		return marshaller_;
	}

	public void setMarshaller(Marshaller marshaller) {
		marshaller_ = marshaller;
		helper_ = new PolymorphicUnmarshallingHelper(marshaller_);
	}

	public Method resolve(Class<?> c) {
		int argl = (marshalledParameters_ != null) ? marshalledParameters_
				.size() : 0;
		Method m = null;

		try {
			for (Method mt : c.getMethods()) {
				if (mt.getParameterTypes().length == argl
						&& mt.getName().equals(getMethod())) {
					m = mt;
					break;
				}
			}
		} catch (Exception e) {
		}

		return m;
	}

	@Override
	public void bind(Object target) {
		if (getParameters() == null) {
			Object[] params = null;

			try {
				Method m = resolve(target.getClass());

				if (m == null) {
					Log.error(
							"Attempt to call a non-existant method [{}] on target [{}]",
							getMethod(), target);
				}

				setParameterTypes(m.getParameterTypes());
				params = helper_.unmarshall(getParameterTypes(),
						getMarshalledParameters());
			} catch (Exception e) {
				Log.error(
						"Unable to unmarshall InvocationInfo call parameters.",
						e);
				throw new RuntimeException(e);
			}

			setParameters(params);
		}
	}

}
