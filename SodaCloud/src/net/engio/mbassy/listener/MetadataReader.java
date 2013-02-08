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
import net.engio.mbassy.common.ReflectionUtils;
import net.engio.mbassy.subscription.MessageEnvelope;

import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * The meta data reader is responsible for parsing and validating message handler configurations.
 *
 * @author bennidi
 * Date: 11/16/12
 */
public class MetadataReader {

    //  This predicate is used to find all message listeners (methods annotated with @Subscribe
	//
    private static final IPredicate<Method> AllMessageHandlers = new IPredicate<Method>() {
        @Override
        public boolean apply(Method target) {
            return target.getAnnotation(Listener.class) != null;
        }
    };

    // cache already created filter instances
    private final Map<Class<? extends IMessageFilter>, IMessageFilter> filterCache = new HashMap<Class<? extends IMessageFilter>, IMessageFilter>();

    // retrieve all instances of filters associated with the given subscription
    private IMessageFilter[] getFilter(Listener subscription){
        if (subscription.filters().length == 0) return null;
        IMessageFilter[] filters = new IMessageFilter[subscription.filters().length];
        int i = 0;
        for (Filter filterDef : subscription.filters()) {
            IMessageFilter filter = filterCache.get(filterDef.value());
            if (filter == null) {
                try{
                    filter = filterDef.value().newInstance();
                    filterCache.put(filterDef.value(), filter);
                }
                catch (Exception e){
                    throw new RuntimeException(e);// propagate as runtime exception
                }

            }
            filters[i] = filter;
            i++;
        }
        return filters;
    }


    public MessageHandlerMetadata getHandlerMetadata(Method messageHandler){
        Listener config = messageHandler.getAnnotation(Listener.class);
        return new MessageHandlerMetadata(messageHandler, getFilter(config), config);
    }

    // get all listeners defined by the given class (includes
    // listeners defined in super classes)
    public List<MessageHandlerMetadata> getMessageHandlers(Class<?> target) {
        // get all handlers (this will include all (inherited) methods directly annotated using @Listener
        List<Method> allHandlers = ReflectionUtils.getMethods(AllMessageHandlers, target);
        // retain only those that are at the bottom of their respective class hierarchy (deepest overriding method)
        List<Method> bottomMostHandlers = new LinkedList<Method>();
        for(Method handler : allHandlers){
            if(!ReflectionUtils.containsOverridingMethod(allHandlers, handler)){
                bottomMostHandlers.add(handler);
            }
        }


        List<MessageHandlerMetadata>  filteredHandlers = new LinkedList<MessageHandlerMetadata>();
        // for each handler there will be no overriding method that specifies @Listener
        // but an overriding method does inherit the listener configuration of the overwritten method
        for(Method handler : bottomMostHandlers){
            Listener listener = handler.getAnnotation(Listener.class);
            if(!listener.enabled() || !isValidMessageHandler(handler)) continue; // disabled or invalid listeners are ignored
            Method overriddenHandler = ReflectionUtils.getOverridingMethod(handler, target);
            // if a handler is overwritten it inherits the configuration of its parent method
            MessageHandlerMetadata handlerMetadata = new MessageHandlerMetadata(overriddenHandler == null ? handler : overriddenHandler,
                    getFilter(listener), listener);
            filteredHandlers.add(handlerMetadata);

        }
        return filteredHandlers;
    }


    public <T> MessageListenerMetadata<T> getMessageListener(Class<T> target) {
        return new MessageListenerMetadata(getMessageHandlers(target), target);
    }



    private boolean isValidMessageHandler(Method handler) {
        if(handler == null || handler.getAnnotation(Listener.class) == null){
            return false;
        }
        if (handler.getParameterTypes().length != 1) {
            // a messageHandler only defines one parameter (the message)
            System.out.println("Found no or more than one parameter in messageHandler [" + handler.getName()
                    + "]. A messageHandler must define exactly one parameter");
            return false;
        }
        Enveloped envelope = handler.getAnnotation(Enveloped.class);
        if(envelope != null && !MessageEnvelope.class.isAssignableFrom(handler.getParameterTypes()[0])){
            System.out.println("Message envelope configured but no subclass of MessageEnvelope found as parameter");
            return false;
        }
        if(envelope != null && envelope.messages().length == 0){
            System.out.println("Message envelope configured but message types defined for handler");
            return false;
        }
        return true;
    }

}
