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
package net.engio.mbassy;

/**
 * Publication error handlers are provided with a publication error every time an error occurs during message publication.
 * A handler might fail with an exception, not be accessible because of the presence of a security manager
 * or other reasons might lead to failures during the message publication process.
 *
 * <p/>
 * @author bennidi
 * Date: 2/22/12
 */
public interface IPublicationErrorHandler {

    /**
     * Handle the given publication error.
     *
     * @param error
     */
	public void handleError(PublicationError error);

    // This is the default error handler it will simply log to standard out and
    // print stack trace if available
    static final class ConsoleLogger implements IPublicationErrorHandler {
        @Override
        public void handleError(PublicationError error) {
            System.out.println(error);
            if (error.getCause() != null) error.getCause().printStackTrace();
        }
    }

    ;
}
