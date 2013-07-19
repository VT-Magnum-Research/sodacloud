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
package org.magnum.soda.transport;

import org.magnum.soda.Soda;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.protocol.java.NativeJavaProtocol;

public class TestSoda {

	private Soda client_ = new Soda();
	private Soda server_ = new Soda(true);
	private LocalPipeTransport pipe_;
	
	public TestSoda(){
		this(new NativeJavaProtocol());
	}
	
	public TestSoda(Protocol proto){
		pipe_ = new LocalPipeTransport(server_, client_, proto);
		client_.connect(pipe_.getClientTransport(),null);
	}
	
	public Soda getClientSoda(){
		return client_;
	}
	
	public Soda getServerSoda(){
		return server_;
	}

}
