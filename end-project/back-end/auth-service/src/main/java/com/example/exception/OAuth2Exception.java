package com.example.exception;

public class OAuth2Exception extends RuntimeException {
    public OAuth2Exception(String message) {
        super(message);
    }
}
