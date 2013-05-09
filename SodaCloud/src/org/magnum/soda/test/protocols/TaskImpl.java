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
package org.magnum.soda.test.protocols;

import java.util.UUID;

public class TaskImpl implements Task {

	private String name_;

	public TaskImpl(String name) {
		name_ = name;
	}

	@Override
	public void run(TaskCoordinator controller) {
		TaskResult r = new TaskResult();
		r.setOutput(name_);
		r.setResultCode(name_.hashCode());
		controller.reportResult(this, r);
	}

	public String getName() {
		return name_;
	}

	public void setName(String name) {
		name_ = name;
	}

}
