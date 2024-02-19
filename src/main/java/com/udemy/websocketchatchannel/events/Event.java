package com.udemy.websocketchatchannel.events;

public record Event<T>(EventType type, T payload) {
   
}
