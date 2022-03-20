package com.lemon.tree.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ValidationErrorResponse extends ErrorResponse {
    private String fieldName;

    public ValidationErrorResponse(String fieldName, String code, String message) {
        this.fieldName = fieldName;
        this.code = code;
        this.message = message;
    }
}
