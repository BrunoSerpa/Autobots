package com.autobots.automanager.controles;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class ErroControle {
    private int status;
    private String message;
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public ErroControle(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
