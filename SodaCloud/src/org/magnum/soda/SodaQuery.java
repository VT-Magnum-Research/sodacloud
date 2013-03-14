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

import java.util.ArrayList;
import java.util.List;

public class SodaQuery<T> {

	private List<T> list_;

	public List<T> getList_() {
		return list_;
	}

	public SodaQuery() {
		list_ = new ArrayList<T>();
	}

	public SodaQuery(T t) {
		list_ = new ArrayList<T>();
		list_.add(t);
	}

	public List<T> now() {

		return list_;
	}

	public void async(Callback<List<T>> hdlr) {
		hdlr.handle(list_);
	}

}
