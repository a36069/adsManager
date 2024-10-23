package com.abdi.ads.manager.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalException extends RuntimeException {
    private final int statusCode;
    private final String statusMessage;

    public GlobalException(int statusCode, String statusMessage, String msg) {
        super(msg);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
    public GlobalException(int statusCode, String statusMessage) {
    	super(statusMessage);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public GlobalException(String message, int statusCode, String statusMessage) {
        super(message);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public GlobalException(String message, Throwable cause, int statusCode, String statusMessage) {
        super(message, cause);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public GlobalException(Throwable cause, int statusCode, String statusMessage) {
        super(cause);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public GlobalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int statusCode, String statusMessage) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
