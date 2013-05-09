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
package org.magnum.soda.svc;

import org.magnum.soda.msg.Msg;
import org.magnum.soda.proxy.ObjRef;

public class ObjInvocationMsg extends Msg {

	private String responseMsgId_;

	private ObjRef targetObjectId_;

	private InvocationInfo invocation_;

	public ObjRef getTargetObjectId() {
		return targetObjectId_;
	}

	public void setTargetObjectId(ObjRef targetObjectId) {
		targetObjectId_ = targetObjectId;
	}

	public InvocationInfo getInvocation() {
		return invocation_;
	}

	public void setInvocation(InvocationInfo invocation) {
		invocation_ = invocation;
	}

	public String getResponseMsgId() {
		return responseMsgId_;
	}

	public void setResponseMsgId(String responseMsgId) {
		responseMsgId_ = responseMsgId;
	}

	@Override
	public Msg createReplyMsg() {
		return new ObjInvocationRespMsg(this);
	}

	@Override
	public String toString() {
		return "ObjInvocationMsg [responseMsgId_=" + responseMsgId_
				+ ", targetObjectId_=" + targetObjectId_ + ", invocation_="
				+ invocation_ + "]";
	}

}
