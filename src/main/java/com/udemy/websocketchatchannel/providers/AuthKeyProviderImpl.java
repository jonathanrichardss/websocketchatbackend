package com.udemy.websocketchatchannel.providers;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;

@Component
public class AuthKeyProviderImpl implements AuthKeyProvider {
	
	private final UrlJwkProvider jwkProvider;
	
	public AuthKeyProviderImpl(@Value("${app.auth.jwks-url}") final String jwksUrl) {
	  try {
		this.jwkProvider = new UrlJwkProvider(new URL(jwksUrl));
	} catch (MalformedURLException e) {
		throw new RuntimeException(e);
	}
	  
	}
	

	@Cacheable("public-key")
	@Override
	public PublicKey getPublicKey(String keyId) {
		try {
			final Jwk jwk = jwkProvider.get(keyId);
			return jwk.getPublicKey();
		} catch (JwkException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
