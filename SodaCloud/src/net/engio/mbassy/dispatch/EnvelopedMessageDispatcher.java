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
import net.engio.mbassy.subscription.MessageEnvelope;

/**
 * Todo: Add javadoc
 *
 * @author bennidi
 *         Date: 12/12/12
 */
public class EnvelopedMessageDispatcher implements IMessageDispatcher {

    private IMessageDispatcher del;

    public EnvelopedMessageDispatcher(IMessageDispatcher dispatcher) {
        this.del = dispatcher;
    }

    @Override
    public void dispatch(Object message, ConcurrentSet listeners) {
        del.dispatch(new MessageEnvelope(message), listeners);
    }

    @Override
    public MessagingContext getContext() {
        return del.getContext();
    }

    @Override
    public IHandlerInvocation getInvocation() {
        return del.getInvocation();
    }
}
