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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.Mode;

import org.magnum.soda.aop.DefaultInvocationProcessorFactory;
import org.magnum.soda.aop.InvocationProcessorFactory;
import org.magnum.soda.auth.AuthInvocationProcessor;
import org.magnum.soda.msg.LocalAddress;
import org.magnum.soda.proxy.JavaReflectionProxyCreator;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.proxy.ProxyCreator;
import org.magnum.soda.proxy.ProxyFactory;
import org.magnum.soda.proxy.RecordingProxy;
import org.magnum.soda.svc.AuthService;
import org.magnum.soda.svc.AuthenticationListener;
import org.magnum.soda.svc.DefaultNamingService;
import org.magnum.soda.svc.InvocationDispatcher;
import org.magnum.soda.svc.NamingService;
import org.magnum.soda.svc.ObjInvoker;
import org.magnum.soda.svc.ObtainNamingServiceMsg;
import org.magnum.soda.svc.ObtainNamingServiceRespMsg;
import org.magnum.soda.transport.Address;
import org.magnum.soda.transport.Transport;
import org.magnum.soda.transport.TransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public class Soda implements TransportListener {

	private static final Logger Log = LoggerFactory.getLogger(Soda.class);

	private Transport transport_;

	private ObjRef namingServiceRef_;

	private NamingService namingService_ = new DefaultNamingService();

	private LocalAddress localAddress_;
	private MBassyMsgBus msgBus_ = new MBassyMsgBus();
	private ObjRegistry objRegistry_;
	private ProxyFactory proxyFactory_;
	private ObjInvoker objInvoker_;
	private ProxyCreator proxyCreator_;

	private AuthService authService_;

	private Map<Object, SodaBinding> ctxPolyObjBinding_ = null;

	public Soda() {
		this(new LocalAddress());
	}

	public Soda(LocalAddress addr) {
		localAddress_ = addr;
		objRegistry_ = createObjRegistry();
		proxyCreator_ = getProxyCreator();
		proxyFactory_ = new ProxyFactory(objRegistry_, proxyCreator_,
				localAddress_, msgBus_);
		objInvoker_ = new ObjInvoker(msgBus_, objRegistry_, proxyFactory_);
		objInvoker_.setProcessorFactory(getInvocationProcessorFactory());
		
		namingServiceRef_ = objRegistry_.publish(namingService_);
		msgBus_.subscribe(this);
		ctxPolyObjBinding_ = new HashMap<Object, SodaBinding>();
	}

	public Soda(boolean becomeserver) {
		this(becomeserver, null);
	}
	
	public Soda(boolean becomeserver, AuthService svc) {
		this();
		if (becomeserver) {
			svc = (svc == null)? getAuthService() : svc;
			getObjRegistry().insert(NamingService.ROOT_NAMING_SVC,
					getNamingService());
			getObjRegistry()
					.insert(AuthService.ROOT_AUTH_SVC, svc);
		}
	}

	public Soda(Transport t, LocalAddress addr) {
		this(addr);
		transport_ = t;
		transport_.setListener(this);
	}
	
	protected ObjRegistry createObjRegistry(){
		return new DefaultObjRegistry(getLocalAddress());
	}

	protected synchronized ProxyCreator getProxyCreator() {
		return new JavaReflectionProxyCreator();
	}

	protected synchronized AuthService getAuthService() {
		return AuthService.NO_AUTH_SVC;
	}
	
	protected synchronized InvocationProcessorFactory getInvocationProcessorFactory(){
		DefaultInvocationProcessorFactory fact = new DefaultInvocationProcessorFactory();
		fact.addProcessor(SodaAuth.class, AuthInvocationProcessor.class);
		return fact;
	}

	public <T> T invoke(T obj) {
		return invoke(obj, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T invoke(T obj, InvocationDispatcher dispatcher) {
		RecordingProxy recorder = new RecordingProxy(obj, proxyCreator_,
				dispatcher);

		Class<?>[] types = { obj.getClass() };
		if (!proxyCreator_.supportsNonInterfaceProxies()) {
			types = obj.getClass().getInterfaces();
		}

		T proxy = (T) proxyCreator_.createProxy(getClass().getClassLoader(),
				types, recorder);
		return proxy;
	}

	public Runnable asRunnable(Object o) {
		InvocationHandler hdlr = proxyCreator_.getInvocationHandler(o);
		if (hdlr == null || !(hdlr instanceof RecordingProxy)) {
			throw new RuntimeException(
					"asRunnable can only be called on an object that is"
							+ " returned from invoke(...)");
		}
		return (Runnable) hdlr;
	}

	@Subscribe
	@Listener(delivery = Mode.Concurrent)
	public void handleNamingServiceRequest(ObtainNamingServiceMsg msg) {
		ObtainNamingServiceRespMsg resp = (ObtainNamingServiceRespMsg) msg
				.createReply();
		resp.setNamingService(namingServiceRef_);
		msgBus_.publish(resp);
	}

	public void passByValue(Class<?> type) {
		proxyFactory_.passByValue(type);
	}
	
	public void invokeAsync(Class<?> c){
		for(Method m : c.getMethods()){
			if(m.getReturnType() == void.class){
				invokeAsync(m);
			}
		}
	}
	
	public void invokeAsync(Method m){
		proxyFactory_.getInvocationSettings().invokeAsync(m);
	}
	
	public void setInvokeVoidMethodsToAsync(boolean async){
		proxyFactory_.getInvocationSettings().setInvokeVoidMethodsAsync(async);
	}
	
	public void setAllowNonLocalProxyInvocations(boolean allow){
		objInvoker_.setInvokeProxies(allow);
	}

	public LocalAddress getLocalAddress() {
		return localAddress_;
	}

	public MBassyMsgBus getMsgBus() {
		return msgBus_;
	}

	public ObjRegistry getObjRegistry() {
		return objRegistry_;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, ObjRef ref){
		return (T)proxyFactory_.createProxy(new Class[]{type}, ref);
	}

	public <T> T get(Class<T> type, String name) {
		return namingService_.get(type, name);
	}

	public void bind(Object o, String name) {
		namingService_.bind(o, name);
	}

	public void connect(Address addr) {
		transport_.connect(addr);
	}

	public void connect(Transport t, Address addr) {
		setTransport(t);
		connect(addr);
	}

	public void setTransport(Transport t) {
		transport_ = t;
		transport_.setListener(this);
	}

	protected NamingService getNamingService() {
		return namingService_;
	}

	protected ObjInvoker getInvoker() {
		return objInvoker_;
	}

	public SodaBinding bind(Object o) {

		SodaBinding b = new SodaBinding();

		this.ctxPolyObjBinding_.put(o, b);

		return b;

	}

	@SuppressWarnings("unchecked")
	public <T> SodaQuery<T> find(Class<T> type, SodaContext ctx) {

		SodaQuery<Object> sq = new SodaQuery<Object>();
		Iterator<Object> itrObject = this.ctxPolyObjBinding_.keySet()
				.iterator();
		while (itrObject.hasNext()) {
			Object contextObject = itrObject.next();
			Class<? extends Object> cls = contextObject.getClass();
			if (!type.isAssignableFrom(cls)) {
				continue;
			}

			SodaBinding sb = this.ctxPolyObjBinding_.get(contextObject);
			Iterator<SodaContext> itrSodaBinding = sb.getContexts_().iterator();
			while (itrSodaBinding.hasNext()) {
				SodaContext stx = itrSodaBinding.next();
				if (stx.equals(ctx)) {
					sq.getList_().add(contextObject);
				}
			}

		}

		return (SodaQuery<T>) sq;
	}

	// return the nearest N locations of the given geohash
	// public <T> SodaQuery<T> findNearest(Class<T> type, SodaContext ctx) {
	//
	// }

	/*
	 * function getNearest(currentLocation, locations, maxNeighbors) { var
	 * matching = {}, accuracy = 12, matchCount = 0; while (matchCount <
	 * maxNeighbors &amp;&amp; accuracy > 0) { var cmpHash =
	 * currentLocation.geoHash.substring(0,accuracy); for (var i = 0; i <
	 * locations.length; i++) { if (locations[i].geoHash in matching) continue;
	 * //don't re-check ones that have already matched if
	 * (locations[i].geoHash.substring(0,accuracy) === cmpHash) {
	 * matching[locations[i].geoHash] = locations[i]; matchCount++; if
	 * (matchCount === maxNeighbors) break; } } accuracy--; } var tmp = []; for
	 * (var geoHash in matching) { tmp.push(matching[geoHash]); } return tmp; }
	 */
	@Override
	public void connected() {
		try {
			ObjRef ref = NamingService.ROOT_NAMING_SVC;

			NamingService svc = (NamingService) proxyFactory_.createProxy(ref);
			namingService_.setParent(svc, proxyFactory_);
		} catch (Exception e) {
			Log.error("Error looking up root naming service", e);
		}

		try {
			ObjRef ref = AuthService.ROOT_AUTH_SVC;
			authService_ = (AuthService) proxyFactory_.createProxy(ref);
		} catch (Exception e) {
			Log.error("Error looking up auth service and authenticating", e);
		}
	}

	public void authenticate(User u, AuthenticationListener l) {
		authService_.authenticate(u, l);
	}

	@Override
	public void disconnected() {
	}

}
