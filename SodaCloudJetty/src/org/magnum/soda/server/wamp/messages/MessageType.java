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
package org.magnum.soda.server.wamp.messages;

public enum MessageType {
	WELCOME(0),
	PREFIX(1),
	CALL(2),
	CALLRESULT(3),
	CALLERROR(4),
	SUBSCRIBE(5),
	UNSUBSCRIBE(6),
	PUBLISH(7),
	EVENT(8);

	private int value;

	private MessageType(int value) {
		this.value = value;
	}

	public int getCode() {
		return value;
	}

	public static MessageType fromInteger(int value) {
		switch(value) {
		case 0:
			return WELCOME;
		case 1:
			return PREFIX;
		case 2:
			return CALL;
		case 3:
			return CALLRESULT;
		case 4:
			return CALLERROR;
		case 5:
			return SUBSCRIBE;
		case 6:
			return UNSUBSCRIBE;
		case 7:
			return PUBLISH;
		case 8:
			return EVENT;
		}
		return null;
	}
}
