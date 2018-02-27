package com.matemeup.matemeup.entities.websocket;

public interface WebSocketCallback {
    void onMessage(final String message, final Object... args);
}
