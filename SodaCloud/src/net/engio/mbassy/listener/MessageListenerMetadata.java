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
package net.engio.mbassy.listener;

import net.engio.mbassy.common.IPredicate;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides information about the message listeners of a specific class. Each message handler
 * defined by the target class is represented as a single entity.
 *
 *
 * @author bennidi
 *         Date: 12/16/12
 */
public class MessageListenerMetadata<T> {


    public static final IPredicate<MessageHandlerMetadata> ForMessage(final Class<?> messageType){
        return new IPredicate<MessageHandlerMetadata>() {
            @Override
            public boolean apply(MessageHandlerMetadata target) {
                return target.handlesMessage(messageType);
            }
        };
    }

    private List<MessageHandlerMetadata> handlers;

    private Class<T> listenerDefinition;

    public MessageListenerMetadata(List<MessageHandlerMetadata> handlers, Class<T> listenerDefinition) {
        this.handlers = handlers;
        this.listenerDefinition = listenerDefinition;
    }


    public List<MessageHandlerMetadata> getHandlers(IPredicate<MessageHandlerMetadata> filter){
        List<MessageHandlerMetadata> matching = new LinkedList<MessageHandlerMetadata>();
        for(MessageHandlerMetadata handler : handlers){
            if(filter.apply(handler))matching.add(handler);
        }
        return matching;
    }

    public boolean handles(Class<?> messageType){
        return !getHandlers(ForMessage(messageType)).isEmpty();
    }

    public Class<T> getListerDefinition(){
        return listenerDefinition;
    }
}
