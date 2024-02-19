package com.udemy.websocketchatchannel.pubsub;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.websocketchatchannel.config.RedisConfig;
import com.udemy.websocketchatchannel.data.UserDto;
import com.udemy.websocketchatchannel.repositories.UserRepository;

@Component
public class Publisher {
	
	private static final Logger LOGGER = Logger.getLogger(Publisher.class.getName());
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ReactiveStringRedisTemplate redisTemplate;
	
	public void publicChatMessage(String userIdFrom, String userIdTo, String text) throws JsonProcessingException {
		UserDto from = userRepository.findById(userIdFrom).orElseThrow();
		UserDto to = userRepository.findById(userIdTo).orElseThrow();
		ChatMessage chatMessage = new ChatMessage(from, to, text);
		String chatMessageSerialized = new ObjectMapper().writeValueAsString(chatMessage);
		redisTemplate.convertAndSend(RedisConfig.CHAT_MESSAGES_CHANNEL, chatMessageSerialized)
		.subscribe();
		
		LOGGER.info("chat message was published!");
	}

}
