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

import org.magnum.soda.marshalling.Marshaller;
import org.magnum.soda.svc.ObjInvocationRespMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnmarshallingInvocationRespMsg extends ObjInvocationRespMsg {

	private static final Logger Log = LoggerFactory
			.getLogger(UnmarshallingInvocationRespMsg.class);

	private PolymorphicUnmarshallingHelper helper_;
	private Marshaller marshaller_;
	private Class<?> resultType_;

	@Override
	public void bindResultType(Class<?> t) {
		resultType_ = t;
	}

	public Marshaller getMarshaller() {
		return marshaller_;
	}

	public void setMarshaller(Marshaller marshaller) {
		marshaller_ = marshaller;
		helper_ = new PolymorphicUnmarshallingHelper(marshaller_);
	}

	public Class<?> getResultType() {
		return resultType_;
	}

	public void setResultType(Class<?> resultType) {
		resultType_ = resultType;
	}

	@Override
	public Object getResult() {
		Object result = null;

		try {
			result = (super.getResult() != null) ? helper_.unmarshall(
					getResultType(), super.getResult().toString()) : null;
		} catch (Exception e) {
			Log.error(
					"Unable to deserialize invocation response message result",
					e);
			throw new RuntimeException(e);
		}

		return result;
	}

}
