package dev.stockman.maskutils;

public class InvalidJsonException extends IllegalArgumentException {
    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
