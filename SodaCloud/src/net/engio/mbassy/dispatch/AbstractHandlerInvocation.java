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

import net.engio.mbassy.IPublicationErrorHandler;
import net.engio.mbassy.PublicationError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Todo: Add javadoc
 *
 * @author bennidi
 *         Date: 11/23/12
 */
public class AbstractHandlerInvocation {

    private MessagingContext context;

    protected void handlePublicationError(PublicationError error){
        Collection<IPublicationErrorHandler> handlers = getContext().getOwningBus().getRegisteredErrorHandlers();
        for(IPublicationErrorHandler handler : handlers){
            handler.handleError(error);
        }
    }

    protected void invokeHandler(final Object message, final Object listener, Method handler){
        try {
            handler.invoke(listener, message);
        }catch(IllegalAccessException e){
            handlePublicationError(
                    new PublicationError(e, "Error during messageHandler notification. " +
                            "The class or method is not accessible",
                            handler, listener, message));
        }
        catch(IllegalArgumentException e){
            handlePublicationError(
                    new PublicationError(e, "Error during messageHandler notification. " +
                            "Wrong arguments passed to method. Was: " + message.getClass()
                            + "Expected: " + handler.getParameterTypes()[0],
                            handler, listener, message));
        }
        catch (InvocationTargetException e) {
            handlePublicationError(
                    new PublicationError(e, "Error during messageHandler notification. " +
                            "Message handler threw exception",
                            handler, listener, message));
        }
        catch (Throwable e) {
            handlePublicationError(
                    new PublicationError(e, "Error during messageHandler notification. " +
                            "Unexpected exception",
                            handler, listener, message));
        }
    }


    public AbstractHandlerInvocation(MessagingContext context) {
        this.context = context;
    }

    public MessagingContext getContext() {
        return context;
    }
}
