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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskCoordinatorImpl implements TaskCoordinator {

	private List<Task> tasks_ = new ArrayList<Task>();
	private Map<Task, TaskResult> results_ = new HashMap<Task, TaskResult>();

	private boolean running_ = false;
	
	@Override
	public void addTask(Task t) {
		tasks_.add(t);
	}

	@Override
	public void reportResult(Task t, TaskResult result) {
		results_.put(t, result);
	}

	@Override
	public TaskResult getResultFor(Task t) {
		return results_.get(t);
	}

	public synchronized void runAll() {
		running_ = true;
		for (Task t : tasks_) {
			t.run(this);
		}
		running_ = false;
		notifyAll();
	}

	@Override
	public synchronized void waitForCompletion() {
		if(running_){
			try{wait();}catch(Exception e){}
		}
	}

	@Override
	public Task nthTask(int i) {
		return tasks_.get(i);
	}
	
	

}
