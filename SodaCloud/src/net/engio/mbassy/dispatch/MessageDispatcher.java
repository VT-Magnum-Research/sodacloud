/******************************************************************************************
MIT License

Copyright (c) 2012 Benjamin Diedrichsen

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify, merge,
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE.
********************************************************************************************/
package net.engio.mbassy.dispatch;

import java.lang.reflect.Method;

import net.engio.mbassy.common.ConcurrentSet;

/**
 * Standard implementation for direct, unfiltered message delivery.
 *
 * For each message delivery, this dispatcher iterates over the listeners
 * and uses the previously provided handler invocation to deliver the message
 * to each listener
 *
 * @author bennidi
 *         Date: 11/23/12
 */
public class MessageDispatcher implements IMessageDispatcher {

    private MessagingContext context;

    private IHandlerInvocation invocation;

    public MessageDispatcher(MessagingContext context, IHandlerInvocation invocation) {
        this.context = context;
        this.invocation = invocation;
    }

    @Override
    public void dispatch(Object message, ConcurrentSet listeners) {
        Method handler = getContext().getHandlerMetadata().getHandler();
        for(Object listener: listeners){
            getInvocation().invoke(handler, listener, message);
        }
    }

    public MessagingContext getContext() {
        return context;
    }

    @Override
    public IHandlerInvocation getInvocation() {
        return invocation;
    }
}
