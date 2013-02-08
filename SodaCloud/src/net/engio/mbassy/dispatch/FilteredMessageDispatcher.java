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
import net.engio.mbassy.listener.IMessageFilter;

/**
 * A dispatcher that implements message filtering based on the filter configuration
 * of the associated message handler. It will delegate message delivery to another
 * message dispatcher after having performed the filtering logic.
 *
 * @author bennidi
 *         Date: 11/23/12
 */
public class FilteredMessageDispatcher implements IMessageDispatcher {

    private final IMessageFilter[] filter;

    private IMessageDispatcher del;

    public FilteredMessageDispatcher(IMessageDispatcher dispatcher) {
        this.del = dispatcher;
        this.filter = dispatcher.getContext().getHandlerMetadata().getFilter();
    }

    private boolean passesFilter(Object message) {

        if (filter == null) {
            return true;
        }
        else {
            for (int i = 0; i < filter.length; i++) {
                if (!filter[i].accepts(message, getContext().getHandlerMetadata())) return false;
            }
            return true;
        }
    }


    @Override
    public void dispatch(Object message, ConcurrentSet listeners) {
         if(passesFilter(message)){
             del.dispatch(message, listeners);
         }
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
