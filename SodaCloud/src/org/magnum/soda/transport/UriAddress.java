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

import java.net.URI;

public class UriAddress implements Address {
	private URI uri_;

	public UriAddress(URI uri) {
		super();
		uri_ = uri;
	}

	public UriAddress(String uri) {
		try {
			uri_ = new URI(uri);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public URI getUri() {
		return uri_;
	}

	public void setUri(URI uri) {
		uri_ = uri;
	}

	public String toString(){
		return uri_.toString();
	}
}
