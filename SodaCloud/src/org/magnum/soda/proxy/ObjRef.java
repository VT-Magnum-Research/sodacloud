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
package org.magnum.soda.proxy;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonTypeName("ObjRef")
public class ObjRef {

	private static final boolean objRefFlag_ = true;

	private String uri_;
	private String[] types_;

	public ObjRef() {
	}

	public ObjRef(String uri, String[] types) {
		super();
		uri_ = uri;
		types_ = types;
	}

	public ObjRef(String uri, String type) {
		this(uri, new String[] { type });
	}

	public ObjRef(String uri) {
		uri_ = uri;
		types_ = new String[0];
	}

	public String getUri() {
		return uri_;
	}

	public void setUri(String uri) {
		uri_ = uri;
	}

	public String[] getTypes() {
		return types_;
	}

	public void setTypes(String[] types) {
		types_ = types;
	}

	public String getHost() {
		return uri_.substring(0, uri_.indexOf("#"));
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean match = false;
		if (obj instanceof ObjRef) {
			String uri = ((ObjRef) obj).getUri();
			match = uri_.equals(uri);
		}
		return match;
	}

	@Override
	public int hashCode() {
		return uri_.hashCode();
	}

	@Override
	public String toString() {
		return "ObjRef [uri_=" + uri_ + ", types_=" + Arrays.toString(types_)
				+ "]";
	}

	public static String createObjUri(String uribase, String oid) {
		return uribase + "#" + oid;
	}

	public static ObjRef fromObjUri(String uri) {
		return new ObjRef(uri);
	}

}
