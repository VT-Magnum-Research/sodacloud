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

import java.util.Comparator;
import java.util.UUID;

import net.engio.mbassy.common.ConcurrentSet;
import net.engio.mbassy.dispatch.IMessageDispatcher;
import net.engio.mbassy.dispatch.MessagingContext;

/**
 * A subscription is a thread safe container for objects that contain message handlers
 */
public class Subscription {

    private UUID id = UUID.randomUUID();

    protected ConcurrentSet<Object> listeners = new ConcurrentSet<Object>();

    private IMessageDispatcher dispatcher;

    private MessagingContext context;

    public Subscription(MessagingContext context, IMessageDispatcher dispatcher) {
        this.context = context;
        this.dispatcher = dispatcher;
    }


    public boolean handlesMessageType(Class<?> messageType){
        return context.getHandlerMetadata().handlesMessage(messageType);
    }


    public void publish(Object message){
          dispatcher.dispatch(message, listeners);
    }

    public MessagingContext getContext(){
        return context;
    }

    public int getPriority(){
        return context.getHandlerMetadata().getPriority();
    }


    public void subscribe(Object o) {
        listeners.add(o);
    }


    public boolean unsubscribe(Object existingListener) {
        return listeners.remove(existingListener);
    }

    public int size(){
        return listeners.size();
    }


    public static final Comparator<Subscription> SubscriptionByPriorityDesc = new Comparator<Subscription>() {
        @Override
        public int compare(Subscription o1, Subscription o2) {
            int result =  o1.getPriority() - o2.getPriority();
            return result == 0 ? o1.id.compareTo(o2.id): result;
        }
    };

}
