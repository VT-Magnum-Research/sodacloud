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
package org.magnum.soda.auth;

import org.magnum.soda.SodaAuth;
import org.magnum.soda.aop.InvocationProcessor;
import org.magnum.soda.svc.InvocationInfo;

import com.google.common.base.Function;

public class AuthInvocationProcessor implements InvocationProcessor<SodaAuth> {

	private SodaAuth annotation_;
	
	@Override
	public void preProcess(InvocationInfo i) {
		try{
			Function<String[],Boolean> eval = annotation_.evaluator().newInstance();
			if(!eval.apply(annotation_.value())){
				throw new RuntimeException("Authentication failure");
			}
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object postProcess(InvocationInfo i, Object rslt) {
		return rslt;
	}

	@Override
	public void setAnnotation(SodaAuth anno) {
		annotation_ = anno;
	}

}
