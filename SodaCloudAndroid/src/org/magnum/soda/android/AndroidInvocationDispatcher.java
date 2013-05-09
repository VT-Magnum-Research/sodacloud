/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.android;

import java.lang.reflect.Method;

import org.magnum.soda.svc.InvocationDispatcher;
import org.magnum.soda.svc.InvocationInfo;

import android.os.Handler;

public class AndroidInvocationDispatcher implements InvocationDispatcher {

	private class AsyncResultCatcher {

		private Object result_;
		private Exception exception_;

		public synchronized void set(Object rslt) {
			result_ = rslt;
			notifyAll();
		}

		public synchronized void setEx(Exception ex) {
			exception_ = ex;
			result_ = ex;
			notifyAll();
		}

		public synchronized Object get() throws Exception {
			if (result_ == null) {
				try {
					wait();
				} catch (Exception e) {
				}
			}

			if (exception_ != null) {
				throw exception_;
			}
			return result_;
		}
	}

	private Handler handler_;

	private boolean alwaysInUi_;
	
	public AndroidInvocationDispatcher(Handler handler) {
		super();
		handler_ = handler;
	}
	
	public AndroidInvocationDispatcher(Handler handler, boolean inui) {
		this(handler);
		alwaysInUi_ = inui;
	}

	@Override
	public Object dispatch(final InvocationInfo inv, final Object target)
			throws Exception {
		Method m = inv.resolve(target.getClass());
		if (alwaysInUi_ || m.getAnnotation(SodaInvokeInUi.class) != null) {
			final AsyncResultCatcher c = new AsyncResultCatcher();
			Runnable r = new Runnable() {

				@Override
				public void run() {
					try {
						Object rslt = InvocationDispatcher.DEFAULT_DISPATCHER
								.dispatch(inv, target);
						c.set(rslt);
					} catch (Exception e) {
						c.setEx(e);
					}
				}
			};
			handler_.post(r);

			return c.get();
		} else {
			return InvocationDispatcher.DEFAULT_DISPATCHER
					.dispatch(inv, target);
		}
	}

}
