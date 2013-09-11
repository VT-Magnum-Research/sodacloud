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
package org.magnum.soda.protocol.java;

import org.magnum.soda.marshalling.Marshaller;
import org.magnum.soda.msg.Msg;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.transport.MsgContainer;


/*
 * This protocol supports serialization of arbitrary Java
 * objects and polymorphic deserialization. The format of
 * object invocation messages is shown below:
 * 
 * [
   "org.magnum.soda.svc.ObjInvocationMsg",
   {
      "invocation":[
         "org.magnum.soda.svc.InvocationInfo",
         {
            "method":"get",
            "parameterTypes":[
               "java.lang.Class",
               "java.lang.String"
            ],
            "parameters":[
               "[Ljava.lang.Object;",
               [
                  [
                     "java.lang.Class",
                     "java.util.concurrent.ExecutorService"
                  ],
                  "executor"
               ]
            ]
         }
      ],
      "targetObjectId":[
         "org.magnum.soda.proxy.ObjRef",
         {
            "host":"soda://meta",
            "uri":"soda://meta#naming",
            "types":[
               "org.magnum.soda.svc.NamingService"
            ]
         }
      ],
      "responseMsgId":null,
      "id":"db620687-7c8a-4c8f-95cd-f49981fc67c4",
      "source":"soda://c75afc3d-82bf-4331-9f37-9fb6bfd12c4a",
      "destination":"soda://meta",
      "responseTo":null
   }
]
 * 
 * 
 */

public class NativeJavaProtocol implements Protocol {

	private Marshaller marshaller_ = new Marshaller();
	
	@Override
	public Msg inbound(String msg) throws Exception {
		return marshaller_.fromTransportFormat(Msg.class, msg);
	}

	@Override
	public MsgContainer outbound(Msg m) throws Exception {
	
		String json = marshaller_.toTransportFormat(m);
	    MsgContainer cont = new MsgContainer(
				json);

		return cont;
	}

	@Override
	public String getName() {
		return "soda-native-java";
	}

}
