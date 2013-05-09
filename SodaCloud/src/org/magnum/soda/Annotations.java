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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

public class Annotations {

	private Mixins mixins_;

	public Annotations() {
	}

	public Annotations(Mixins mixes) {
		mixins_ = mixes;
	}

	public Collection<Class<?>> getMixins(Class<?> c) {
		@SuppressWarnings("unchecked")
		Collection<Class<?>> mixins = (mixins_ != null) ? mixins_.getMixins(c)
				: Collections.EMPTY_LIST;
		return mixins;
	}

	public <T extends Annotation> T getAnnotation(Class<?> on, Class<T> annotype) {
		Collection<Class<?>> mixins = getMixins(on);

		T anno = null;

		for (Class<?> mix : mixins) {
			anno = mix.getAnnotation(annotype);
			if (anno != null) {
				break;
			}
		}

		anno = (anno == null) ? on.getAnnotation(annotype) : anno;

		return anno;
	}

	public <T extends Annotation> T getAnnotation(Method m, Class<T> type) {
		Collection<Class<?>> mixins = getMixins(m.getDeclaringClass());

		T anno = null;

		for (Class<?> mix : mixins) {
			Method meth = getMethod(mix, m);
			if (meth != null) {
				anno = meth.getAnnotation(type);
				if (anno != null) {
					break;
				}
			}
		}

		anno = (anno == null) ? m.getAnnotation(type) : anno;

		return anno;
	}

	public Method getMethod(Class<?> c, Method m) {
		try {
			return c.getMethod(m.getName(), m.getParameterTypes());
		} catch (Exception e) {
			return null;
		}
	}
}
