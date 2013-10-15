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

import org.magnum.soda.proxy.ObjRef;

/**
 * 
 * This class manages a repository of object
 * references and their mapping to a set of 
 * associated objects. Typically, an ObjRef
 * is paired to a proxy, although regular
 * objects may also be mapped to an ObjRef.
 * 
 * @author jules
 *
 */
public interface ObjRegistry {

	public void insert(ObjRef ObjRef, Object o);
	
	public boolean remove(ObjRef ObjRef);
	
	public Object get(ObjRef ObjRef);
	
	public ObjRef publish(Object o);
	
	public boolean isLocalObject(ObjRef ref);
}
