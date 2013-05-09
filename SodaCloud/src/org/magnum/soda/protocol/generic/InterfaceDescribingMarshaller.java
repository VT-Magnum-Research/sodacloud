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
package org.magnum.soda.protocol.generic;

import org.magnum.soda.marshalling.Marshaller;
import org.magnum.soda.proxy.ObjRef;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class InterfaceDescribingMarshaller extends Marshaller {

	public InterfaceDescribingMarshaller(boolean addtypeinfo) {
		super(addtypeinfo);
		
		SimpleModule simpleModule = new SimpleModule("InteraceDescribingModule", 
                new Version(1,0,0,null));
		simpleModule.addSerializer(ObjRef.class, new ObjRefSerializer());
		getMapper().registerModule(simpleModule);
	}

	

}
