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

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;

public class SessionData {

	private static CacheLoader<String, SessionData> loader_ = new CacheLoader<String, SessionData>() {
		public SessionData load(String key) {
			return new SessionData();
		}
	};

	private static Cache<String, SessionData> sessions_ = createCache(10000,
			10, TimeUnit.MINUTES);

	public static void init(int maxSize, int expireAfter, TimeUnit unit) {
		sessions_ = createCache(maxSize, expireAfter, unit);
	}

	private static Cache<String, SessionData> createCache(int maxSize,
			int expireAfter, TimeUnit unit) {
		return CacheBuilder.newBuilder().maximumSize(maxSize)
				.expireAfterAccess(expireAfter, unit)
				// .removalListener(MY_LISTENER)
				.build(loader_);
	}

	public static SessionData forClient(final String id) {
		try {
			return sessions_.get(id,new Callable<SessionData>() {

				@Override
				public SessionData call() throws Exception {
					return loader_.load(id);
				}
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<Object, Object> data_ = Maps.newConcurrentMap();

	@SuppressWarnings("unchecked")
	public <T> T get(Object key) {
		return (T)data_.get(key);
	}

	public void put(Object key, Object data) {
		data_.put(key, data);
	}
}
