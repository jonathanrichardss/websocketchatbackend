package com.udemy.websocketchatchannel.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.yaml.snakeyaml.events.StreamEndEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.websocketchatchannel.data.UserDto;
import com.udemy.websocketchatchannel.events.Event;
import com.udemy.websocketchatchannel.events.EventType;
import com.udemy.websocketchatchannel.pubsub.ChatMessage;
import com.udemy.websocketchatchannel.services.TicketService;
import com.udemy.websocketchatchannel.services.UserService;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
	
	private static final Logger LOGGER = Logger.getLogger(WebSocketHandler.class.getName());
	
	private final UserService userService;
	
	private final TicketService ticketService;
	
	private final Map<String, WebSocketSession> sessions;
	
	private final Map<String, String> userIds;
	
	public WebSocketHandler(TicketService service, UserService userService) {
		this.userService = userService;
		this.ticketService = service;
		sessions = new ConcurrentHashMap<>();
		userIds = new ConcurrentHashMap<>();
		
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		LOGGER.info("[afterConnectionEstablished] id: " + session.getId());
		Optional<String> ticket = ticketOf(session);
		
		if (ticket == null | ticket.get().isBlank()) {
			LOGGER.warning("session " + session.getId() + " without ticket");
			session.close(CloseStatus.POLICY_VIOLATION);
			return;
		}
		
		Optional<String> userId  = ticketService.getUserIdByTicket(ticket.get());
		if (userId.isEmpty()) {
			LOGGER.warning("session " + session.getId() + " with invalid ticket!");
			session.close(CloseStatus.POLICY_VIOLATION);
			return;
		}
		
		sessions.put(userId.get(), session);
		userIds.put(session.getId(), userId.get());
		LOGGER.info("session " + session.getId() + " was bind to user " + userId.get());
		sendChatUsers(session);
		
	}
	
	private void sendChatUsers(WebSocketSession session) {
		List<UserDto> chatUsers = userService.findChatUsers();
		Event<List<UserDto>> event = new Event<List<UserDto>>(EventType.CHAT_USERS_WERE_UPDATED, chatUsers);
		sendEvent(session, event);
	}

	private void sendEvent(WebSocketSession session, Event event) {
		String eventSerialized;
		try {
			eventSerialized = new ObjectMapper().writeValueAsString(event);
			session.sendMessage(new TextMessage(eventSerialized));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private Optional<String> ticketOf(WebSocketSession session) {
		return Optional.ofNullable(
				session.getUri()
				).map(UriComponentsBuilder::fromUri)
				.map(UriComponentsBuilder::build)
				.map(UriComponents::getQueryParams)
				.map(it -> it.get("ticket"))
				.flatMap(it -> it
						.stream().findFirst())
				.map(String::trim);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		LOGGER.info("[handleMessage] msg:" + message.getPayload());
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		LOGGER.info("[handleTextMessage] txtMsg: " + message.getPayload());
		if (message.getPayload().equals("ping")) {
			session.sendMessage(new TextMessage("pong"));
			return;
		}
		
		MessagePayload payload  = new ObjectMapper().readValue(message.getPayload(), MessagePayload.class);
		String userIdFrom = userIds.get(session.getId());
		publisher.publishChatMessage(userIdFrom, payload.to(), payload.text());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		LOGGER.info("[afterConnectionClosed] + id:" + session.getId());
		String userId = userIds.get(session.getId());
		sessions.remove(userId);
		userIds.remove(session.getId());
	}

	public void notify(ChatMessage chatMessage) {
		Event<ChatMessage> event = new Event<>(EventType.CHAT_MESSAGE_WAS_CREATED, chatMessage);
		List<String> userIds = List.of(chatMessage.from().id(), chatMessage.to().id());
		userIds.stream()
		       .distinct()
		       .map(sessions::get)
		       .filter(Objects::nonNull)
		       .forEach(session -> sendEvent(session, event));
	}
	
}
