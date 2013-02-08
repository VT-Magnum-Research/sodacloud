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
package net.engio.mbassy.subscription;

import net.engio.mbassy.dispatch.AsynchronousHandlerInvocation;
import net.engio.mbassy.dispatch.EnvelopedMessageDispatcher;
import net.engio.mbassy.dispatch.FilteredMessageDispatcher;
import net.engio.mbassy.dispatch.IHandlerInvocation;
import net.engio.mbassy.dispatch.IMessageDispatcher;
import net.engio.mbassy.dispatch.MessageDispatcher;
import net.engio.mbassy.dispatch.MessagingContext;
import net.engio.mbassy.dispatch.ReflectiveHandlerInvocation;

/**
 * Created with IntelliJ IDEA.
 * @author bennidi
 * Date: 11/16/12
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFactory {

    public Subscription createSubscription(MessagingContext context){
        IHandlerInvocation invocation = buildInvocationForHandler(context);
        IMessageDispatcher dispatcher = buildDispatcher(context, invocation);
        return new Subscription(context, dispatcher);
    }

    protected IHandlerInvocation buildInvocationForHandler(MessagingContext context){
        IHandlerInvocation invocation = new ReflectiveHandlerInvocation(context);
        if(context.getHandlerMetadata().isAsynchronous()){
            invocation = new AsynchronousHandlerInvocation(invocation);
        }
        return invocation;
    }

    protected IMessageDispatcher buildDispatcher(MessagingContext context, IHandlerInvocation invocation){
       IMessageDispatcher dispatcher = new MessageDispatcher(context, invocation);
       if(context.getHandlerMetadata().isEnveloped()){
          dispatcher = new EnvelopedMessageDispatcher(dispatcher);
       }
       if(context.getHandlerMetadata().isFiltered()){
          dispatcher = new FilteredMessageDispatcher(dispatcher);
       }
       return dispatcher;
    }
}
