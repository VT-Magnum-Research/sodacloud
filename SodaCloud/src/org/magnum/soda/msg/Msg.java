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

import java.util.UUID;


import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Msg {

	private String source_;

	private String destination_;

	private String id_;

	private String responseTo_;

	private boolean marked_;

	public Msg() {
		id_ = UUID.randomUUID().toString();
	}

	public String getId() {
		return id_;
	}

	public void setId(String id) {
		id_ = id;
	}

	public String getResponseTo() {
		return responseTo_;
	}

	public void setResponseTo(String responseTo) {
		responseTo_ = responseTo;
	}

	@JsonIgnore
	public boolean isResponse() {
		return getResponseTo() != null;
	}

	public String getSource() {
		return source_;
	}

	public void setSource(String source) {
		source_ = source;
	}

	public String getDestination() {
		return destination_;
	}

	public void setDestination(String destination) {
		destination_ = destination;
	}

	public abstract Msg createReplyMsg();

	public Msg createReply() {
		Msg reply = createReplyMsg();
		reply.setResponseTo(getId());
		reply.setDestination(getSource());
		return reply;
	}

	@JsonIgnore
	public void mark() {
		marked_ = true;
	}

	@JsonIgnore
	public boolean isMarked() {
		return marked_;
	}

}
