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

public class Session {

	private static final ThreadLocal<Session> session = new ThreadLocal<Session>() {
        protected synchronized Session initialValue() {
            return new Session();
        }
    };
	
    public static SessionData get(){
    	return session.get().data_;
    }
    
    public static void set(SessionData data){
    	session.get().data_ = data;
    }
    
    public static void setCurrent(String clientid){
    	set(SessionData.forClient(clientid));
    }
    
    private SessionData data_;
}
