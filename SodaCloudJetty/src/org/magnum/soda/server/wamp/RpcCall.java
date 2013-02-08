/*****************************************************************************
 * Copyright 2013 Olivier Croquette <ocroquette@free.fr>                     *
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
package org.magnum.soda.server.wamp;

import org.magnum.soda.server.wamp.messages.CallErrorMessage;
import org.magnum.soda.server.wamp.messages.CallMessage;
import org.magnum.soda.server.wamp.messages.CallResultMessage;
import org.magnum.soda.server.wamp.messages.MessageMapper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;


/***
 * Represents a single RPC call instance with its input, and the output or result after execution.
 *
 */
public class RpcCall {
	public RpcCall(CallMessage msg) {
		callMessage = msg;
	}

	public <InputType> InputType getInput(Class<InputType> inputType)  {
		return callMessage.getPayload(inputType);  
	}

	public void setOutput(JsonElement jsonRoot)  {
		outputJsonElement = jsonRoot;  
	}

	public <OutputType> void setOutput(OutputType outputValue, Class<OutputType> outputType)  {
		outputJsonElement = new Gson().toJsonTree(outputValue, outputType);  
	}

	public void setError(String errorUri, String errorDesc) {
		hasFailed = true;
		this.errorUri = errorUri;
		this.errorDesc = errorDesc; 
	}

	public void setError(String errorUri, String errorDesc, Object errorDetails) {
		setError(errorUri, errorDesc);
		Gson gson = new Gson();
		this.errorDetails = gson.toJsonTree(errorDetails);
	}

	public boolean hasFailed() {
		return hasFailed;
	}

	public String getResultingJson() {
		if ( ! hasFailed ) {
			CallResultMessage callResultMessage = new CallResultMessage();
			callResultMessage.callId = callMessage.callId;
			callResultMessage.payload = outputJsonElement;
			return MessageMapper.toJson(callResultMessage);
		}
		else {
			CallErrorMessage callErrorMessage = new CallErrorMessage(callMessage.callId, errorUri, errorDesc, errorDetails);
			return MessageMapper.toJson(callErrorMessage);
		}

	}

	protected CallMessage callMessage; 

	protected String inputJsonText;
	protected JsonElement outputJsonElement;

	protected boolean hasFailed = false;

	protected String errorUri;
	protected String errorDesc;
	protected JsonElement errorDetails;

}
