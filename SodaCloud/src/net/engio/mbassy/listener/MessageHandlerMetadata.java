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

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 *
 *
 * @author bennidi
 * Date: 11/14/12
 */
public class MessageHandlerMetadata {

    private Method handler;

    private IMessageFilter[] filter;

    private Listener listenerConfig;

    private boolean isAsynchronous = false;

    private Enveloped envelope = null;

    private List<Class<?>> handledMessages = new LinkedList<Class<?>>();

    private boolean acceptsSubtypes = true;


    public MessageHandlerMetadata(Method handler, IMessageFilter[] filter, Listener listenerConfig) {
        this.handler = handler;
        this.filter = filter;
        this.listenerConfig = listenerConfig;
        this.isAsynchronous = listenerConfig.delivery().equals(Mode.Concurrent);
        this.envelope = handler.getAnnotation(Enveloped.class);
        this.acceptsSubtypes = !listenerConfig.rejectSubtypes();
        if(this.envelope != null){
            for(Class messageType : envelope.messages())
                handledMessages.add(messageType);
        }
        else{
            handledMessages.add(handler.getParameterTypes()[0]);
        }
        this.handler.setAccessible(true);
    }


    public boolean isAsynchronous(){
        return isAsynchronous;
    }

    public boolean isFiltered(){
        return filter != null && filter.length > 0;
    }

    public int getPriority(){
        return listenerConfig.priority();
    }

    public Method getHandler() {
        return handler;
    }

    public IMessageFilter[] getFilter() {
        return filter;
    }

    public List<Class<?>> getHandledMessages(){
        return handledMessages;
    }

    public boolean isEnveloped() {
        return envelope != null;
    }

    public boolean handlesMessage(Class<?> messageType){
        for(Class<?> handledMessage : handledMessages){
            if(handledMessage.equals(messageType))return true;
            if(handledMessage.isAssignableFrom(messageType) && acceptsSubtypes()) return true;
        }
        return false;
    }

    public boolean acceptsSubtypes(){
        return acceptsSubtypes;
    }


    public boolean isEnabled() {
        return listenerConfig.enabled();
    }
}
