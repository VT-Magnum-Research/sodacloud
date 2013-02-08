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

import net.engio.mbassy.common.ConcurrentSet;

/**
 * A message dispatcher provides the functionality to deliver a single message
 * to a set of listeners. A message dispatcher uses a message context to access
 * all information necessary for the message delivery.
 *
 * The delivery of a single message to a single listener is responsibility of the
 * handler invocation object associated with the dispatcher.
 *
 * Implementations if IMessageDispatcher are partially designed using decorator pattern
 * such that it is possible to compose different message dispatchers to achieve more complex
 * dispatch logic.
 *
 * @author bennidi
 *         Date: 11/23/12
 */
public interface IMessageDispatcher {

    /**
     * Delivers the given message to the given set of listeners.
     * Delivery may be delayed, aborted or restricted in various ways, depending
     * on the configuration of the dispatcher
     *
     * @param message The message that should be delivered to the listeners
     * @param listeners The listeners that should receive the message
     */
    public void dispatch(Object message, ConcurrentSet listeners);

    /**
     * Get the messaging context associated with this dispatcher
     *
     * @return
     */
    public MessagingContext getContext();

    /**
     * Get the handler invocation that will be used to deliver the message to each
     * listener
     * @return
     */
    public IHandlerInvocation getInvocation();
}
