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

import net.engio.mbassy.BusConfiguration;
import net.engio.mbassy.MBassador;

/**
 * The default msg bus that transports messages
 * locally between the various components of
 * Soda.
 * 
 * @author jules
 *
 */
public class MBassyMsgBus implements MsgBus {

	private MBassador<Object> bus_ = new MBassador<Object>(BusConfiguration.Default());
	
	@Override
	public void publish(Object msg) {
		bus_.publishAsync(msg);
	}

	@Override
	public void subscribe(Object sub) {
		bus_.subscribe(sub);
	}

}
