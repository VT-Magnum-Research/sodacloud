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

import net.engio.mbassy.listener.MetadataReader;
import net.engio.mbassy.subscription.SubscriptionFactory;

import java.util.concurrent.*;

/**
 * The bus configuration holds various parameters that can be used to customize the bus' runtime behaviour. *
 *
 * @author bennidi
 *         Date: 12/8/12
 */
public class BusConfiguration {

    private static final ThreadFactory DaemonThreadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        }
    };

    public static final BusConfiguration Default(){
        return new BusConfiguration();
    }

    private int numberOfMessageDispatchers;

    private ExecutorService executor;

    private int maximumNumberOfPendingMessages;

    private SubscriptionFactory subscriptionFactory;

    private MetadataReader metadataReader;

    public BusConfiguration() {
        this.numberOfMessageDispatchers = 2;
        this.maximumNumberOfPendingMessages = Integer.MAX_VALUE;
        this.subscriptionFactory = new SubscriptionFactory();
        this.executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), DaemonThreadFactory);
        this.metadataReader = new MetadataReader();

    }

    public MetadataReader getMetadataReader() {
        return metadataReader;
    }

    public BusConfiguration setMetadataReader(MetadataReader metadataReader) {
        this.metadataReader = metadataReader;
        return this;
    }

    public int getNumberOfMessageDispatchers() {
        return numberOfMessageDispatchers > 0 ? numberOfMessageDispatchers : 2;
    }

    public BusConfiguration setNumberOfMessageDispatchers(int numberOfMessageDispatchers) {
        this.numberOfMessageDispatchers = numberOfMessageDispatchers;
        return this;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public BusConfiguration setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public int getMaximumNumberOfPendingMessages() {
        return maximumNumberOfPendingMessages;
    }

    public BusConfiguration setMaximumNumberOfPendingMessages(int maximumNumberOfPendingMessages) {
        this.maximumNumberOfPendingMessages = maximumNumberOfPendingMessages > 0
                ? maximumNumberOfPendingMessages
                : Integer.MAX_VALUE;
        return this;
    }

    public SubscriptionFactory getSubscriptionFactory() {
        return subscriptionFactory;
    }

    public BusConfiguration setSubscriptionFactory(SubscriptionFactory subscriptionFactory) {
        this.subscriptionFactory = subscriptionFactory;
        return this;
    }
}
