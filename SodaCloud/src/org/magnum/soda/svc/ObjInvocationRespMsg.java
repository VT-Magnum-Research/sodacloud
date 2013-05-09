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

public class ObjInvocationRespMsg extends Msg {

	private Object result_;

	private Exception exception_;

	public ObjInvocationRespMsg() {
	}

	public ObjInvocationRespMsg(ObjInvocationMsg respto) {
		assert (respto.getId() != null);
		setResponseTo(respto.getId());
	}

	public ObjInvocationRespMsg(ObjInvocationMsg respto, Object val) {
		this(respto);
		result_ = val;
	}

	public ObjInvocationRespMsg(ObjInvocationMsg respto, Exception e) {
		this(respto);
		exception_ = e;
	}

	public void bindResultType(Class<?> t) {
	}

	public Object getResult() {
		return result_;
	}

	public void setResult(Object result) {
		result_ = result;
	}

	public Exception getException() {
		return exception_;
	}

	public void setException(Exception exception) {
		exception_ = exception;
	}

	@Override
	public Msg createReplyMsg() {
		return null;
	}

	@Override
	public String toString() {
		return "ObjInvocationRespMsg [result_=" + result_ + ", exception_="
				+ exception_ + "]";
	}

}
