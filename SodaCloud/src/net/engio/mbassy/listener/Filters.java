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

/**
 * Todo: Add javadoc
 *
 * @author bennidi
 *         Date: 12/12/12
 */
public class Filters {

    public static final class AllowAll implements IMessageFilter {

        @Override
        public boolean accepts(Object event, MessageHandlerMetadata metadata) {
            return true;
        }
    }

    public static final class RejectAll implements IMessageFilter {

        @Override
        public boolean accepts(Object event, MessageHandlerMetadata metadata) {
            return false;
        }
    }


    public static final class RejectSubtypes implements IMessageFilter {

        @Override
        public boolean accepts(Object event, MessageHandlerMetadata metadata) {
            for(Class handledMessage : metadata.getHandledMessages()){
                if(handledMessage.equals(event.getClass()))return true;
            }
            return false;
        }
    }
}
