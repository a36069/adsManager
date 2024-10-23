package com.abdi.ads.manager.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotAccessException extends RuntimeException {

    public NotAccessException() {
    }

    public NotAccessException(String message) {
        super(message);
    }
}
