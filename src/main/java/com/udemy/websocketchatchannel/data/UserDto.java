package com.udemy.websocketchatchannel.data;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public record UserDto(String id, String name, String picture) {

}
