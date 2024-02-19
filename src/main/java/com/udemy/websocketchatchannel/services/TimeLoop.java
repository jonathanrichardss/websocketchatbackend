package com.udemy.websocketchatchannel.services;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TimeLoop {

	@Autowired
	private StringBasedGenerator basedGenerator;
	
//	@PostConstruct
//	void init() {
//		new Timer().schedule(new TimerTask() {
//			
//			@Override
//			public void run() {
//			
//				String simple = basedGenerator.getUncachedString();
//				String cached = basedGenerator.getCachedString();
//				
//				System.out.println("Simple: " + simple + " " + "Cached: " + cached);
//			}
//		}, 2000L, 2000L);
//	}
}
