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
package org.magnum.soda.auth;

import org.magnum.soda.SodaAuth;
import org.magnum.soda.svc.Session;

import com.google.common.base.Function;

public class SodaAuthEvaluator implements Function<String[], Boolean>{

	public Boolean apply(String[] val){
		boolean ok = false;
		String[] vals = (String[])Session.get().get(SodaAuth.SESSION_ROLES_VARIABLE);
		for(String role : val){
			if(hasRole(role, vals)){
				ok = true;
				break;
			}
		}
		return ok;
	}
	
	public boolean hasRole(String role, String[] session){
		boolean in = false;
		for(String s : session){
			if(s.equals(role)){
				in = true;
				break;
			}
		}
		return in;
	}
}
