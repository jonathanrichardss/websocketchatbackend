package com.udemy.websocketchatchannel.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udemy.websocketchatchannel.services.TicketService;

@RestController
@RequestMapping("/v1/ticket")
@CrossOrigin
public class TicketController {

	@Autowired
	private TicketService service;
	
	@PostMapping
	public Map<String, String> buildTicket(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
		String token = Optional
				.ofNullable(authorization)
				.map(it -> it.replace("Bearer ", ""))
				.orElse("");
		
		String ticket = service.buildAndSaveTicket(token);
		return Map.of("ticket", ticket);
	}
}
