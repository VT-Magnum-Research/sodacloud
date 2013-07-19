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
package org.magnum.soda.msg;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.transport.Address;

public class LocalAddress implements Address {

	private String hostId_ = UUID.randomUUID().toString();
	
	private String uriBase_ = "soda://"+hostId_;
	
	public LocalAddress(){}
	
	public LocalAddress(String id){
		hostId_ = id;
		uriBase_ = "soda://"+hostId_;
	}
	
	public String toString(){
		return uriBase_;
	}
	
	public String createObjUri(){
		return ObjRef.createObjUri(uriBase_, UUID.randomUUID().toString());
	}
	
	public boolean isALocalObject(ObjRef ref){
		return (ref != null) && (ref.getUri() != null) && ref.getUri().startsWith(uriBase_+"#");
	}
	
	public ObjRef createObjRef(Class<?> type){
		return createObjRef(new Class[]{type});
	}
	
	public ObjRef createObjRef(Object o){
		Set<Class<?>> ifs = new HashSet<Class<?>>();
	    findAllInterfaces(o.getClass(), ifs);
	    Class<?>[] allifs = ifs.toArray(new Class[0]);
		return createObjRef(allifs);
	}
	
	public ObjRef createObjRef(Class<?>[] types){
		String[] ifs = new String[types.length];
		
		for(int i = 0; i < ifs.length; i++){
			ifs[i] = types[i].getName();
		}
		String uri = createObjUri();
		ObjRef ref = new ObjRef(uri, ifs);
		
		return ref;
	}
	
	private void findAllInterfaces(Class<?> c, Set<Class<?>> found){
		for(Class<?> i : c.getInterfaces()){
			found.add(i);
		}
		if(c != Object.class){
			findAllInterfaces(c.getSuperclass(), found);
		}
	}
}
