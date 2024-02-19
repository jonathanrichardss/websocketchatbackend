package com.udemy.websocketchatchannel;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.udemy.websocketchatchannel.providers.TokenProvider;

@SpringBootTest
public class TokenProviderTest {

	@Autowired
	private TokenProvider provider;
	
	@Test
	public void test() {
		Map<String, String> decoded = provider.decode("TOKEN-INVALIDO");
		System.out.println(decoded);
	}
}
