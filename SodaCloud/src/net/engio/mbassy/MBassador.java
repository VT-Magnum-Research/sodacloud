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

import net.engio.mbassy.common.DeadEvent;
import net.engio.mbassy.subscription.Subscription;

import java.util.Collection;
import java.util.concurrent.TimeUnit;


public class MBassador<T> extends AbstractMessageBus<T, SyncAsyncPostCommand<T>> {

    public MBassador(BusConfiguration configuration) {
        super(configuration);
    }


    public MessagePublication publishAsync(T message) {
        return addAsynchronousDeliveryRequest(createMessagePublication(message));
    }

    public MessagePublication publishAsync(T message, long timeout, TimeUnit unit) {
        return addAsynchronousDeliveryRequest(createMessagePublication(message), timeout, unit);
    }

    private MessagePublication createMessagePublication(T message) {
        Collection<Subscription> subscriptions = getSubscriptionsByMessageType(message.getClass());
        if (subscriptions == null || subscriptions.isEmpty()) {
            // Dead Event
            subscriptions = getSubscriptionsByMessageType(DeadEvent.class);
            return MessagePublication.Create(subscriptions, new DeadEvent(message));
        }
        else return MessagePublication.Create(subscriptions, message);
    }



    /**
     * Synchronously publish a message to all registered listeners (this includes listeners defined for super types)
     * The call blocks until every messageHandler has processed the message.
     *
     * @param message
     */
    public void publish(T message) {
        try {
            MessagePublication publication = createMessagePublication(message);
            publication.execute();

            /*
            final Collection<Subscription> subscriptions = getSubscriptionsByMessageType(message.getClass());
            if (subscriptions == null || subscriptions.isEmpty()) {
                // publish a DeadEvent since no subscriptions could be found
                final Collection<Subscription> deadEventSubscriptions = getSubscriptionsByMessageType(DeadEvent.class);
                if (deadEventSubscriptions != null && !deadEventSubscriptions.isEmpty()) {
                    for (Subscription subscription : deadEventSubscriptions) {
                        subscription.publish(new DeadEvent(message));
                    }
                }
            }
            else{
                for (Subscription subscription : subscriptions) {
                    subscription.publish(message);
                }
            }*/
        } catch (Throwable e) {
            handlePublicationError(new PublicationError()
                    .setMessage("Error during publication of message")
                    .setCause(e)
                    .setPublishedObject(message));
        }

    }


    @Override
    public SyncAsyncPostCommand<T> post(T message) {
        return new SyncAsyncPostCommand<T>(this, message);
    }

}
