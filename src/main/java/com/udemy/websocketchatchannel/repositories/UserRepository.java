package com.udemy.websocketchatchannel.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.udemy.websocketchatchannel.data.UserDto;

public interface UserRepository extends MongoRepository<UserDto, String> {

}
