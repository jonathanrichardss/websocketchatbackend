package com.udemy.websocketchatchannel.services;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.udemy.websocketchatchannel.data.UserDto;
import com.udemy.websocketchatchannel.providers.TokenProvider;
import com.udemy.websocketchatchannel.repositories.UserRepository;

@Service
public class TicketService {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private TokenProvider tokenProvider;
	
	@Autowired
	private UserRepository userRepository;
	
	public String buildAndSaveTicket(String token) {
		 if (token == null || token.isBlank()) throw new RuntimeException("missing token");
		 String ticket = UUID.randomUUID().toString();
		 Map<String, String> user = tokenProvider.decode(token);
		 String userId = user.get("id");
		 redisTemplate.opsForValue().set(ticket, userId, Duration.ofSeconds(10L));
		 saveUser(user);
		 return ticket;
	}
		
	private void saveUser(Map<String, String> user) {
		userRepository.save(new UserDto(user.get("id"), user.get("name"), user.get("picture")));
	}
	
	public Optional<String> getUserIdByTicket(String ticket) {
		return Optional.ofNullable(redisTemplate.opsForValue().getAndDelete(ticket));
	}
}
