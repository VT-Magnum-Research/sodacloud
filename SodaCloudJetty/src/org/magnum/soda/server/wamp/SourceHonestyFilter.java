/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.server.wamp;

import org.magnum.soda.msg.Msg;
import org.magnum.soda.msg.Protocol;
import org.magnum.soda.server.wamp.messages.PublishMessage;
import org.magnum.soda.server.wamp.messages.SubscribeMessage;
import org.magnum.soda.server.wamp.messages.UnsubscribeMessage;
import org.magnum.soda.transport.MsgContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SourceHonestyFilter implements WampServerListener, PubSubFilter {

	private static final Logger Log = LoggerFactory
			.getLogger(SourceHonestyFilter.class);

	private BiMap<ClientId, String> clientIdMap_ = HashBiMap.create();

	private Protocol protocol_;

	public SourceHonestyFilter(Protocol protocol) {
		super();
		protocol_ = protocol;
	}

	@Override
	public PublishMessage filterPublish(ClientId from, PublishMessage msg) {
		PublishMessage rslt = null;
		MsgContainer cont = msg.getPayload(MsgContainer.class);
		String msgjson = cont.getMsg();
		try {
			Msg msgobj = protocol_.inbound(msgjson);

			String realsrc = clientIdMap_.get(from);

			if (!("" + realsrc).equals(msgobj.getSource())) {
				Log.error(
						"Warning, dropping a msg with a fradulent source, which was actually from: [{}] msg:[{}]",
						realsrc, msgjson);
			} else {
				rslt = msg;
			}
		} catch (Exception e) {
			Log.error("Unexpected error checking the source of msg: [{}]",
					msgjson);
			Log.error("Reason:", e);
		}

		return rslt;
	}

	@Override
	public void clientConnected(ClientId client) {

	}

	@Override
	public void clientDisconnected(ClientId client) {

	}

	@Override
	public void clientSubscribedToTopic(ClientId id, String topic) {
		if(!clientIdMap_.containsKey(id)){
			clientIdMap_.put(id, topic);
		}
	}

	@Override
	public void clientUnSubscribedFromTopic(ClientId id, String topic) {
		clientIdMap_.remove(id);
	}

	@Override
	public SubscribeMessage filterSubscribe(ClientId id, SubscribeMessage sub) {
		SubscribeMessage rslt = null;
		String topic = sub.topicUri;
		ClientId existing = clientIdMap_.inverse().get(topic);
		if (existing != null && existing != id) {
			String correct = clientIdMap_.get(id);
			Log.error(
					"An attempt is being made to snoop on another client's messages by: "
							+ id + "(" + correct + ") [attempting to snoop on:"
							+ existing + " (" + topic + ")");
			
		} else {
			rslt = sub;
			clientIdMap_.put(id, topic);
		}
		
		return rslt;
	}

	@Override
	public UnsubscribeMessage filterUnsubscribe(ClientId id, UnsubscribeMessage topic) {
		return topic;
	}

}
