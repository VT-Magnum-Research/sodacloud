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
import org.magnum.soda.msg.Msg;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.svc.InvocationInfo;
import org.magnum.soda.svc.ObjInvocationMsg;
import org.magnum.soda.svc.ObjInvocationMsgBuilder;
import org.magnum.soda.svc.ObjInvocationRespMsg;
import org.magnum.soda.transport.MsgContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 
 * 
 {
 "type":"invoke",
 "method":"get",
 "parameters":["executor"]
 "uri":"soda://meta#naming",
 "responseMsgId":null,
 "id":"db620687-7c8a-4c8f-95cd-f49981fc67c4",
 "source":"soda://c75afc3d-82bf-4331-9f37-9fb6bfd12c4a",
 "destination":"soda://meta",
 "responseTo":null
 }
 * 
 * 
 */

public class DefaultProtocol implements Protocol {

	private static final Logger Log = LoggerFactory
			.getLogger(DefaultProtocol.class);

	private static final String RESPONSE_TO = "responseTo";
	private static final String RESULT = "result";
	private static final String EXCEPTION = "exception";
	private static final String RESPONSE = "response";
	private static final String PARAMETERS = "parameters";
	private static final String DESTINATION = "destination";
	private static final String SOURCE = "source";
	private static final String RESPONSE_MSG_ID = "responseMsgId";
	private static final String ID = "id";
	private static final String URI = "uri";
	private static final String METHOD = "method";
	private static final String TYPE = "type";
	private static final String INVOCATION = "invocation";


	@Override
	public Msg inbound(String msg) throws Exception {
		JSONObject obj = (JSONObject) (new JSONParser()).parse(msg);
		String type = "" + obj.get(TYPE);
		Msg unmsg = null;

		String id = "" + obj.get(ID);
		String source = "" + obj.get(SOURCE);
		String destination = "" + obj.get(DESTINATION);
		String responseTo = "" + obj.get(RESPONSE_TO);

		if (INVOCATION.equals(type)) {
			String method = "" + obj.get(METHOD);
			String uri = "" + obj.get(URI);
			String responseMsgId = "" + obj.get(RESPONSE_MSG_ID);

			JSONArray params = (JSONArray) obj.get(PARAMETERS);

			InvocationInfo info = UnmarshallingInvocationInfoBuilder
					.unmarshallingInvocationInfo().withMethod(method)
					.withMarshaller(new Marshaller(false))
					.withMarshalledParameters(params).build();

			unmsg = ObjInvocationMsgBuilder.objInvocationMsg()
					.withDestination(destination).withId(id)
					.withResponseMsgId(responseMsgId)
					.withResponseTo(responseTo).withSource(source)
					.withTargetObjectId(ObjRef.fromObjUri(uri))
					.withInvocation(info).build();
		} else if (RESPONSE.equals(type)) {
			Exception ex = null;
			if (obj.get(EXCEPTION) != null) {
				ex = new RuntimeException("" + obj.get(EXCEPTION));
			}

			ObjInvocationRespMsg resp = UnmarshallingInvocationRespMsgBuilder
					.unmarshallingInvocationRespMsg()
					.withDestination(destination).withId(id).withSource(source)
					.withResponseTo(responseTo).withResult(obj.get(RESULT))
					.withException(ex).withMarshaller(new Marshaller(false)).build();

			unmsg = resp;
		}

		return unmsg;
	}

	@Override
	public synchronized MsgContainer outbound(Msg msg) throws Exception {
		JSONObject obj = new JSONObject();

		if (msg instanceof ObjInvocationMsg) {
			ObjInvocationMsg oi = (ObjInvocationMsg) msg;
			obj.put(TYPE, INVOCATION);
			obj.put(METHOD, oi.getInvocation().getMethod());
			obj.put(URI, oi.getTargetObjectId().getUri());

			if (oi.getResponseMsgId() != null) {
				obj.put(RESPONSE_MSG_ID, oi.getResponseMsgId());
			}

			try {

				Object[] params = oi.getInvocation().getParameters();
				JSONArray jparams = new JSONArray();
				for (int i = 0; i < params.length; i++) {
					String data = new InterfaceDescribingMarshaller(false).toTransportFormat(params[i]);
					jparams.add(new JSONParser().parse(data));
				}

				obj.put(PARAMETERS, jparams);

			} catch (Exception e) {
				Log.error("Exception marshalling ObjInvocationMsg", e);
				throw new RuntimeException(e);
			}

		} else if (msg instanceof ObjInvocationRespMsg) {
			ObjInvocationRespMsg roi = (ObjInvocationRespMsg) msg;

			obj.put(TYPE, RESPONSE);
			String data = new InterfaceDescribingMarshaller(false).toTransportFormat(roi.getResult());
			obj.put(RESULT, new JSONParser().parse(data));
			if (roi.getException() != null) {
				obj.put(EXCEPTION, roi.getException().getMessage());
			}
		}

		obj.put(ID, msg.getId());
		obj.put(RESPONSE_TO, msg.getResponseTo());
		obj.put(SOURCE, msg.getSource());
		obj.put(DESTINATION, msg.getDestination());

		MsgContainer mc = new MsgContainer(obj.toJSONString());
		return mc;
	}

	@Override
	public String getName() {
		return "soda-default";
	}

}
