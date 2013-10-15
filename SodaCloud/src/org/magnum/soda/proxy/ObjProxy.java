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
package org.magnum.soda.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.Mode;

import org.magnum.soda.MsgBus;
import org.magnum.soda.svc.InvocationInfo;
import org.magnum.soda.svc.InvocationInfoBuilder;
import org.magnum.soda.svc.ObjInvocationMsg;
import org.magnum.soda.svc.ObjInvocationMsgBuilder;
import org.magnum.soda.svc.ObjInvocationRespMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public class ObjProxy implements InvocationHandler {

	
	private static final Logger Log = LoggerFactory.getLogger(ObjProxy.class);
	
	public class ResponseCatcher {
		private String respId_;
		private MsgBus msgBus_;
		private ObjInvocationRespMsg response_;
		private Object condition_ = new Object();

		public ResponseCatcher(MsgBus bus, String respId) {
			super();
			respId_ = respId;
			msgBus_ = bus;

			msgBus_.subscribe(this);
		}


		@Subscribe
		@Listener(delivery=Mode.Concurrent)
		public void handleResponse(ObjInvocationRespMsg resp) {
			if (respId_.equals(resp.getResponseTo())) {
				response_ = resp;
				synchronized (condition_) {
					condition_.notify();
				}
			}
		}

		public ObjInvocationRespMsg getResponse() {
			if (response_ == null) {
				synchronized (condition_) {
					try {
						if (response_ == null) {
							condition_.wait();
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}

			return response_;
		}
	}

	private MsgBus msgBus_;

	private ProxyFactory factory_;

	private ObjRef objectRef_;
	
	private InvocationSettings invocationSettings_;
	
	public ObjProxy(ProxyFactory fact, InvocationSettings settings, MsgBus msgBus, ObjRef objid) {
		super();
		invocationSettings_ = settings;
		objectRef_ = objid;
		msgBus_ = msgBus;
		factory_ = fact;
	}

	@Override
	public Object invoke(Object arg0, Method arg1, Object[] arg2)
			throws Throwable {

		if(arg1.getName().equals("equals")){
			Object to = arg2[0];
			if(to != null && Proxy.isProxyClass(to.getClass())){
				Object i = Proxy.getInvocationHandler(to);
				if(i instanceof ObjProxy){
					return objectRef_.equals(((ObjProxy)i).objectRef_);
				}
			}
		}
		else if(arg1.getName().equals("hashCode")){
			return hashCode();
		}
		else if(arg1.getName().equals("toString")){
			return toString();
		}
		
		Object[] args = factory_.convertToObjectRefsIfNeeded(arg2);

		InvocationInfo inv = InvocationInfoBuilder.invocationInfo()
				.withMethod(arg1.getName())
				.withParameterTypes(arg1.getParameterTypes())
				.withParameters(args).build();

		ObjInvocationMsg msg = ObjInvocationMsgBuilder.objInvocationMsg()
				.withInvocation(inv).withTargetObjectId(objectRef_)
				.withDestination(objectRef_.getHost())
				.build();

		
		Object rslt = null;
		
		if(invocationSettings_.shouldInvokeAsync(arg0, arg1, arg2)){
			invokeAsync(msg);
		}
		else {
			Class<?> type = arg1.getReturnType();
			SodaInferReturnTypeFromArgument anno = arg1.getAnnotation(SodaInferReturnTypeFromArgument.class);
			if(anno != null){
				type = (Class<?>)arg2[anno.index()];
			}
			rslt = invokeSync(msg, msg.getId(), type);
		}

		return rslt;
	}

	private void invokeAsync(ObjInvocationMsg msg){
		msgBus_.publish(msg);
	}

	private Object invokeSync(ObjInvocationMsg msg, String respid, Class<?> returntype)
			throws Throwable {
		ResponseCatcher catcher = new ResponseCatcher(msgBus_, msg.getId());

		msgBus_.publish(msg);

		ObjInvocationRespMsg resp = catcher.getResponse();

		Log.debug("Recv'd response to invocation: [{}] response: [{}]",msg,resp);
		
		if (resp.getException() != null) {
			throw resp.getException();
		}

		resp.bindResultType(returntype);
		Object returnval = resp.getResult();
		returnval = factory_.createProxiesFromRefsIfNeeded(returnval);

		return returnval;
	}

	@Override
	public String toString() {
		return "ObjProxy [objectRef_=" + objectRef_ + "]";
	}

	@Override
	public int hashCode() {
		return objectRef_.hashCode();
	}

	public ObjRef getObjectRef() {
		return objectRef_;
	}

	public void setObjectRef(ObjRef objectRef) {
		objectRef_ = objectRef;
	}

	
}
