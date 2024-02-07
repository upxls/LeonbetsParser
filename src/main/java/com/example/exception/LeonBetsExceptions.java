package com.example.exception;

public class LeonBetsExceptions extends RuntimeException {
    public LeonBetsExceptions(String message) {
        super(message);
    }

    public LeonBetsExceptions(Throwable cause) {
        super(cause);
    }
}
