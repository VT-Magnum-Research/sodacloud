/*****************************************************************************
 * Copyright 2013 Olivier Croquette <ocroquette@free.fr>                     *
 *                                                                           *
 *  Licensed under the Apache License, Version 2.0 (the "License");          *
 *  you may not use this file except in compliance with the License.         *
 *  You may obtain a copy of the License at                                  *
 *                                                                           *
 *      http://www.apache.org/licenses/LICENSE-2.0                           *
 *                                                                           *
 *  Unless required by applicable law or agreed to in writing, software      *
 *  distributed under the License is distributed on an "AS IS" BASIS,        *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 *  See the License for the specific language governing permissions and      *
 *  limitations under the License.                                           *
 ****************************************************************************/
package org.magnum.soda.server.wamp.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class MessageMapper {

	static JsonParser parser;
	static Gson gson;

	public static Message fromJson(String json) throws JsonParseException {
		Message message = null;
		try {
			message = fromJsonTryBlock(json);
		}
		catch(Exception e) {
			fromJsonCaughtException(e,json);
		}
		return message;
	}

	public static Message fromJsonTryBlock(String json) {
		JsonArray array = getParser().parse(json).getAsJsonArray();
		MessageType messageType = MessageType.fromInteger(getGson().fromJson(array.get(0), Integer.class));
		switch(messageType) {
		case WELCOME:
			return getGson().fromJson(json, WelcomeMessage.class);
		case CALL:
			return getGson().fromJson(json, CallMessage.class);
		case CALLRESULT:
			return getGson().fromJson(json, CallResultMessage.class);
		case CALLERROR:
			return getGson().fromJson(json, CallErrorMessage.class);
		case SUBSCRIBE:
			return getGson().fromJson(json, SubscribeMessage.class);
		case UNSUBSCRIBE:
			return getGson().fromJson(json, UnsubscribeMessage.class);
		case PUBLISH:
			return getGson().fromJson(json, PublishMessage.class);
		case EVENT:
			return getGson().fromJson(json, EventMessage.class);
		}
		System.err.println("MessageMapper.fromJson: Unknown type in: " + json);
		return null;
	}

	public static void fromJsonCaughtException(Exception e, String json) throws JsonParseException {
		String text = "Failed to parse: \"" + json + "\"\nException: " + e;
		System.err.println("fromJsonCaughtException: " + text);
	}
	
	public static String toJson(Message message) {
		return getGson().toJson(message);
	}

	static JsonParser getParser() {
		if (parser == null)
			parser = new JsonParser();
		return parser;
	}
	static Gson getGson() {
		if (gson == null) {
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(WelcomeMessage.class, new WelcomeMessage.Serializer());
			builder.registerTypeAdapter(WelcomeMessage.class, new WelcomeMessage.Deserializer());
			builder.registerTypeAdapter(CallMessage.class, new CallMessage.Serializer());
			builder.registerTypeAdapter(CallMessage.class, new CallMessage.Deserializer());
			builder.registerTypeAdapter(CallResultMessage.class, new CallResultMessage.Serializer());
			builder.registerTypeAdapter(CallResultMessage.class, new CallResultMessage.Deserializer());
			builder.registerTypeAdapter(CallErrorMessage.class, new CallErrorMessage.Serializer());
			builder.registerTypeAdapter(CallErrorMessage.class, new CallErrorMessage.Deserializer());
			builder.registerTypeAdapter(SubscribeMessage.class, new SubscribeMessage.Serializer());
			builder.registerTypeAdapter(SubscribeMessage.class, new SubscribeMessage.Deserializer());
			builder.registerTypeAdapter(PublishMessage.class, new PublishMessage.Serializer());
			builder.registerTypeAdapter(PublishMessage.class, new PublishMessage.Deserializer());
			builder.registerTypeAdapter(EventMessage.class, new EventMessage.Serializer());
			builder.registerTypeAdapter(EventMessage.class, new EventMessage.Deserializer());
			builder.registerTypeAdapter(UnsubscribeMessage.class, new UnsubscribeMessage.Serializer());
			builder.registerTypeAdapter(UnsubscribeMessage.class, new UnsubscribeMessage.Deserializer());
			gson = builder.create();
		}
		return gson;
	}

}
