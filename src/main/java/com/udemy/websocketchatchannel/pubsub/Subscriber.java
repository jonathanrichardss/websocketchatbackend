package com.udemy.websocketchatchannel.pubsub;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.websocketchatchannel.config.RedisConfig;
import com.udemy.websocketchatchannel.handler.WebSocketHandler;

import jakarta.annotation.PostConstruct;

public class Subscriber {

	private static final Logger LOGGER = Logger.getLogger(Publisher.class.getName());
	
	@Autowired
	private ReactiveStringRedisTemplate redisTemplate;
	
	@Autowired
	private WebSocketHandler webSocketHandler;
	
	@PostConstruct
	private void init() {
		this.redisTemplate
		.listenTo(ChannelTopic.of(RedisConfig.CHAT_MESSAGES_CHANNEL))
		.map(ReactiveSubscription.Message::getMessage)
		.subscribe(this::onChatMessage);
	}
	
	private void onChatMessage(final String chatMessageSerialized) {
		LOGGER.info("chat message was received");
		try {
			ChatMessage chatMessage = new ObjectMapper().readValue(chatMessageSerialized, ChatMessage.class);
			webSocketHandler.notify(chatMessage);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
