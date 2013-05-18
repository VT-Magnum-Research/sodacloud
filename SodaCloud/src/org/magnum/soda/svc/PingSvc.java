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

public interface PingSvc {

	public static class PingMsg {
		private String from_;
		private String msg_;
		private int times_;
		public String getFrom() {
			return from_;
		}
		public void setFrom(String from) {
			from_ = from;
		}
		public String getMsg() {
			return msg_;
		}
		public void setMsg(String msg) {
			msg_ = msg;
		}
		public int getTimes() {
			return times_;
		}
		public void setTimes(int times) {
			times_ = times;
		}
		
		
	}
	
	public static final String SVC_NAME = "ping";
	
	/**
	 * Performs a synchronous ping() that can
	 * be used to test if a connection to another
	 * node is alive.
	 * 
	 */
	public void ping();
	
	public void ping(PingMsg msg);
	
	public void pingMe(PingSvc me);
}
