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
package org.magnum.soda;

import com.google.common.eventbus.EventBus;

/**
 * This msg bus has issues with lots of
 * concurrent connections b/c it does
 * synchronous event dispatch....
 * 
 * need to fix this guy...
 * 
 * @author jules
 *
 */
public class GuavaMsgBus implements MsgBus {

	private EventBus bus_ = new EventBus();
	
	@Override
	public void publish(Object msg) {
		bus_.post(msg);
	}

	@Override
	public void subscribe(Object sub) {
		bus_.register(sub);
	}

}
