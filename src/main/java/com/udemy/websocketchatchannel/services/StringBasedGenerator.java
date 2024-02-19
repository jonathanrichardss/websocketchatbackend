package com.udemy.websocketchatchannel.services;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class StringBasedGenerator {

	public String getUncachedString() {
		return UUID.randomUUID().toString();
	}
	
	@Cacheable("cached-string")
	public String getCachedString() {
		return UUID.randomUUID().toString();
	}
}
