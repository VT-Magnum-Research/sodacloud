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

/**
 * A message publication is created for each asynchronous message dispatch. It reflects the state
 * of the corresponding message publication process, i.e. provides information whether the
 * publication was successfully scheduled, is currently running etc.
 *
 * @author bennidi
 * Date: 11/16/12
 */
public class MessagePublication {

    public static  MessagePublication Create(Collection<Subscription> subscriptions, Object message){
        return new MessagePublication(subscriptions, message, State.Initial);
    }

    private Collection<Subscription> subscriptions;

    private Object message;

    private State state = State.Scheduled;

    private MessagePublication(Collection<Subscription> subscriptions, Object message, State initialState) {
        this.subscriptions = subscriptions;
        this.message = message;
        this.state = initialState;
    }

    public boolean add(Subscription subscription) {
        return subscriptions.add(subscription);
    }

    protected void execute(){
        state = State.Running;
        for(Subscription sub : subscriptions){
            sub.publish(message);
        }
        state = State.Finished;
    }

    public boolean isFinished() {
        return state.equals(State.Finished);
    }

    public boolean isRunning() {
        return state.equals(State.Running);
    }

    public boolean isScheduled() {
        return state.equals(State.Scheduled);
    }

    public MessagePublication markScheduled(){
        if(!state.equals(State.Initial))
            return this;
        state = State.Scheduled;
        return this;
    }

    public MessagePublication setError(){
        state = State.Error;
        return this;
    }

    public boolean isDeadEvent(){
        return DeadEvent.class.isAssignableFrom(message.getClass());
    }

    private enum State{
        Initial,Scheduled,Running,Finished,Error;
    }

}
