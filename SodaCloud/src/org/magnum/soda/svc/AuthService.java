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

import org.magnum.soda.User;
import org.magnum.soda.msg.MetaAddress;
import org.magnum.soda.proxy.ObjRef;

public interface AuthService {

	public static final String SVC_NAME = "auth";

	public static final ObjRef ROOT_AUTH_SVC = new ObjRef(
			ObjRef.createObjUri(MetaAddress.META_ADDRESS.toString(), SVC_NAME),
			AuthService.class.getName());
	
	public static final AuthService NO_AUTH_SVC = new AuthService() {
		
		@Override
		public void authenticate(User user, AuthenticationListener l) {
			if(l != null){l.authenticated();}
		}
	};
	
	public void authenticate(User user, AuthenticationListener l);
	
}
