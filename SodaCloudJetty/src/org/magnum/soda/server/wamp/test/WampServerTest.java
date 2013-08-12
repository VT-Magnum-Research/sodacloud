/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.server.wamp.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.magnum.soda.server.wamp.ClientId;
import org.magnum.soda.server.wamp.WampServer;
import org.magnum.soda.server.wamp.WampServerListener;
import org.magnum.soda.server.wamp.common.Channel;
import org.magnum.soda.server.wamp.messages.MessageMapper;
import org.magnum.soda.server.wamp.messages.PublishMessage;
import org.magnum.soda.server.wamp.messages.SubscribeMessage;
import org.mockito.ArgumentCaptor;

public class WampServerTest {

	@Test
	public void testClientConnect() throws Exception {
		WampServerListener listener = mock(WampServerListener.class);
		Channel chnld = mock(Channel.class);
		
		WampServer server = new WampServer();
		server.addListener(listener);
		server.addClient(chnld);
		
		ArgumentCaptor<ClientId> captor = ArgumentCaptor.forClass(ClientId.class);
		verify(chnld).handle(any(String.class));
		verify(listener).clientConnected(captor.capture());
	}
	
	@Test
	public void testClientDisonnect() throws Exception {
		WampServerListener listener = mock(WampServerListener.class);
		Channel chnld = mock(Channel.class);
		
		WampServer server = new WampServer();
		server.addListener(listener);
		server.addClient(chnld);
		
		ArgumentCaptor<ClientId> captor = ArgumentCaptor.forClass(ClientId.class);
		verify(chnld).handle(any(String.class));
		verify(listener).clientConnected(captor.capture());
		
		String topic = UUID.randomUUID().toString();
		SubscribeMessage msg = new SubscribeMessage(topic);
		ClientId clientId = captor.getValue();
		server.handleIncomingMessage(clientId, MessageMapper.toJson(msg));
		verify(listener).clientSubscribedToTopic(clientId, topic);
		
		doThrow(new IOException()).when(chnld).handle(any(String.class));
		PublishMessage pub = new PublishMessage(topic);
		String json = MessageMapper.toJson(pub);
		server.handleIncomingMessage(clientId, json);
		
		verify(listener).clientDisconnected(clientId);
	}
	
	@Test
	public void testPubSub() throws Exception {
		WampServerListener listener = mock(WampServerListener.class);
		Channel chnld = mock(Channel.class);
		
		WampServer server = new WampServer();
		server.addListener(listener);
		server.addClient(chnld);
		
		ArgumentCaptor<ClientId> captor = ArgumentCaptor.forClass(ClientId.class);
		verify(listener).clientConnected(captor.capture());
		
		ClientId clientId = captor.getValue();
		
		String topic = UUID.randomUUID().toString();
		SubscribeMessage msg = new SubscribeMessage(topic);
		server.handleIncomingMessage(clientId, MessageMapper.toJson(msg));
		verify(listener).clientSubscribedToTopic(clientId, topic);
		
		PublishMessage pub = new PublishMessage(topic);
		String json = MessageMapper.toJson(pub);
		server.handleIncomingMessage(clientId, json);
		
		//should be twice due to the welcome msg
		verify(chnld,times(2)).handle(any(String.class));
	}

}
