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
package org.magnum.soda.ctx;

import org.magnum.soda.SodaContext;

public class SodaLocation implements SodaContext {

	public enum Proximity {
		TWENTY_METERS
	}
	
	public enum Accuracy {
		FINE
	}
	
	public enum PostionUpdates {
		EVERY_HUNDRED_METERS
	}

	public static SodaLocation at(double lat, double lon){
		throw new RuntimeException();
	}
	
	public static SodaLocation within(Proximity range){
		throw new RuntimeException();
	}
	
	public SodaLocation of(double lat, double lon){
		throw new RuntimeException();
	}
	
	public SodaLocation atAccuracy(Accuracy accuracy){
		throw new RuntimeException();
	}
}

