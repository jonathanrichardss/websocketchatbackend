package com.udemy.websocketchatchannel.pubsub;

import com.udemy.websocketchatchannel.data.UserDto;

public record ChatMessage(UserDto from, UserDto to, String text) {
}
