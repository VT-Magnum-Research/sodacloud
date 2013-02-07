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

import org.magnum.soda.msg.Msg;
import org.magnum.soda.proxy.ObjRef;

public class ObjAdvertisementMsg extends Msg {

	private String server_;
	private String topicId_;
	private ObjRef objectId_;
	private String type_;

	public String getServer() {
		return server_;
	}

	public void setServer(String server) {
		server_ = server;
	}

	public String getTopicId() {
		return topicId_;
	}

	public void setTopicId(String topicId) {
		topicId_ = topicId;
	}

	public ObjRef getObjectId() {
		return objectId_;
	}

	public void setObjectId(ObjRef objectId) {
		objectId_ = objectId;
	}

	public String getType() {
		return type_;
	}

	public void setType(String type) {
		type_ = type;
	}

	@Override
	public Msg newInstance() {
		return new ObjAdvertisementMsg();
	}

	
}
