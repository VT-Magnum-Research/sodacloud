/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.transport.wamp;

import android.os.Message;

public interface MsgQueue {

	public void sendMessage(Message msg);
	
	public Message obtainMessage();
	
}
