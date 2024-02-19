package com.udemy.websocketchatchannel.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udemy.websocketchatchannel.data.UserDto;
import com.udemy.websocketchatchannel.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public List<UserDto> findChatUsers() {
		return userRepository.findAll();
	}
}
