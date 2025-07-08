package dev.stockman.maskutils.core;

public class InvalidJsonException extends IllegalArgumentException {
    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
