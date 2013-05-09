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

import java.util.Collection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Mixins {

	private final Cache<Class<?>, Collection<Class<?>>> cache_ = CacheBuilder.newBuilder()
			.maximumSize(100)
			.build();
	
	private final Multimap<Class<?>, Class<?>> mixed_ = HashMultimap.create();

	public void mixin(Class a, Class b){
		mixed_.put(a,b);
	}
	
	public Collection<Class<?>> getMixins(Class a){
		Collection<Class<?>> mixes = cache_.getIfPresent(a);
		if(mixes == null){
			mixes = mixed_.get(a);
			
			if(Object.class.equals(a.getClass())){
				mixes.addAll(getMixins(a.getClass().getSuperclass()));
			}
		}
		
		if(mixes.size() > 0){
			cache_.put(a, mixes);
		}
		
		return mixes;
	}
}
