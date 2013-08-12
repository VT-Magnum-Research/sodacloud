/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.server.wamp.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.magnum.soda.msg.Msg;
import org.magnum.soda.protocol.generic.DefaultProtocol;
import org.magnum.soda.proxy.ObjRef;
import org.magnum.soda.server.wamp.ClientId;
import org.magnum.soda.server.wamp.ClientIdFactory;
import org.magnum.soda.server.wamp.SourceHonestyFilter;
import org.magnum.soda.server.wamp.messages.PublishMessage;
import org.magnum.soda.server.wamp.messages.SubscribeMessage;
import org.magnum.soda.svc.InvocationInfo;
import org.magnum.soda.svc.ObjInvocationMsg;
import org.magnum.soda.transport.MsgContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SourceHonestyFilterTest {

	@Test
	public void testDuplicateSubscriberDetection() throws Exception {
		DefaultProtocol p1 = new DefaultProtocol();
		SourceHonestyFilter filter = new SourceHonestyFilter(p1);
		ClientIdFactory fact = new ClientIdFactory();
		ClientId c1 = fact.getNext();
		ClientId c2 = fact.getNext();
		
		assertNotNull(filter.filterSubscribe(c1, new SubscribeMessage(c1.toString())));
		filter.clientSubscribedToTopic(c1, c1.toString());
		assertNotNull(filter.filterSubscribe(c2, new SubscribeMessage(c2.toString())));
		filter.clientSubscribedToTopic(c2, c2.toString());
		
		assertNull(filter.filterSubscribe(c1, new SubscribeMessage(c2.toString())));
		assertNull(filter.filterSubscribe(c2, new SubscribeMessage(c1.toString())));
	}
	
	@Test
	public void testFilteringHonesty() throws Exception {
		DefaultProtocol p1 = new DefaultProtocol();
		SourceHonestyFilter filter = new SourceHonestyFilter(p1);
		ClientIdFactory fact = new ClientIdFactory();
		ClientId c1 = fact.getNext();
		ClientId c2 = fact.getNext();
		
		filter.clientSubscribedToTopic(c1, c1.toString());
		filter.clientSubscribedToTopic(c2, c2.toString());
		
		ObjInvocationMsg msg = new ObjInvocationMsg();
		msg.setSource(c1.toString());
		InvocationInfo i = new InvocationInfo();
		i.setParameters(new Object[]{});
		i.setMethod("a");
		msg.setTargetObjectId(ObjRef.fromObjUri("ws://asdf#1"));
		msg.setInvocation(i);
		
		MsgContainer mc = p1.outbound(msg);
		PublishMessage m1 = new PublishMessage(c1.toString());
		m1.setPayload(mc);
		
		assertEquals(m1, filter.filterPublish(c1, m1));
		assertNull(filter.filterPublish(c2, m1));
		assertEquals(m1, filter.filterPublish(c1, m1));
		
		msg.setSource(c2.toString());
		mc = p1.outbound(msg);
		m1.setPayload(mc);
		assertNull(filter.filterPublish(c1, m1));
		assertEquals(m1, filter.filterPublish(c2, m1));
	}

}
