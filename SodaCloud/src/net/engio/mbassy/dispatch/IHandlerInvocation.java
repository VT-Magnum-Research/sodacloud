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

/**
 * A handler invocation encapsulates the logic that is used to invoke a single
 * message handler to process a given message.
 * A handler invocation might come in different flavours and can be composed
 * of various independent invocations be means of delegation (decorator pattern)
 *
 * @author bennidi
 *         Date: 11/23/12
 */
public interface IHandlerInvocation {

    /**
     * Invoke the message delivery logic of this handler invocation
     *
     * @param handler The method that represents the actual message handler logic of the listener
     * @param listener The listener that will receive the message
     * @param message  The message to be delivered to the listener
     */
    public void invoke(final Method handler, final Object listener, final Object message);

    /**
     * Get the messaging context associated with this invocation
     * @return
     */
    public MessagingContext getContext();

}
