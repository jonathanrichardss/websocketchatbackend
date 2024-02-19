package com.udemy.websocketchatchannel.providers;

import java.security.PublicKey;

import org.springframework.stereotype.Component;

@Component
public interface AuthKeyProvider {

	public PublicKey getPublicKey(String keyId);
}
