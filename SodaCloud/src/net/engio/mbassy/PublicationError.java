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

import java.lang.reflect.Method;

/**
 * Publication errors are created when object publication fails for some reason and contain details
 * as to the cause and location where they occured.
 * <p/>
 * @author bennidi
 * Date: 2/22/12
 * Time: 4:59 PM
 */
public class PublicationError {

	private Throwable cause;

	private String message;

	private Method listener;

	private Object listeningObject;

	private Object publishedObject;


	public PublicationError(Throwable cause, String message, Method listener, Object listeningObject, Object publishedObject) {
		this.cause = cause;
		this.message = message;
		this.listener = listener;
		this.listeningObject = listeningObject;
		this.publishedObject = publishedObject;
	}

	public PublicationError(){
		super();
	}

	public Throwable getCause() {
		return cause;
	}

	public PublicationError setCause(Throwable cause) {
		this.cause = cause;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public PublicationError setMessage(String message) {
		this.message = message;
		return this;
	}

	public Method getListener() {
		return listener;
	}

	public PublicationError setListener(Method listener) {
		this.listener = listener;
		return this;
	}

	public Object getListeningObject() {
		return listeningObject;
	}

	public PublicationError setListeningObject(Object listeningObject) {
		this.listeningObject = listeningObject;
		return this;
	}

	public Object getPublishedObject() {
		return publishedObject;
	}

	public PublicationError setPublishedObject(Object publishedObject) {
		this.publishedObject = publishedObject;
		return this;
	}

    @Override
    public String toString() {
        return "PublicationError{" +
                "\n" +
                "\tcause=" + cause +
                "\n" +
                "\tmessage='" + message + '\'' +
                "\n" +
                "\tlistener=" + listener +
                "\n" +
                "\tlisteningObject=" + listeningObject +
                "\n" +
                "\tpublishedObject=" + publishedObject +
                '}';
    }
}
