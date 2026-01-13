package com.example.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Data
@NoArgsConstructor
public class ErrorDto {
    private Integer code;
    private String error;
    private String errorMessage;
    private OffsetDateTime timestamp;
    private String path;

    public ErrorDto(String errorMessage, String path, HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.errorMessage = errorMessage;
        this.path = path;
        this.timestamp = ZonedDateTime.now().toOffsetDateTime();
    }
}