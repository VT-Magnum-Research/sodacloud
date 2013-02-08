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

import net.engio.mbassy.IMessageBus;
import net.engio.mbassy.listener.MessageHandlerMetadata;

/**
 * The messaging context holds all data/objects that is relevant to successfully publish
 * a message within a subscription. A one-to-one relation between a subscription and
 * MessagingContext holds -> a messaging context is created for each distinct subscription
 * that lives inside a message bus.
 *
 * @author bennidi
 *         Date: 11/23/12
 */
public class MessagingContext {

    private IMessageBus owningBus;

    private MessageHandlerMetadata handlerMetadata;

    public MessagingContext(IMessageBus owningBus, MessageHandlerMetadata handlerMetadata) {
        this.owningBus = owningBus;
        this.handlerMetadata = handlerMetadata;
    }

    /**
     * Get a reference to the message bus this context belongs to
     * @return
     */
    public IMessageBus getOwningBus() {
        return owningBus;
    }


    /**
     * Get the meta data that specifies the characteristics of the message handler
     * that is associated with this context
     * @return
     */
    public MessageHandlerMetadata getHandlerMetadata() {
        return handlerMetadata;
    }

}
