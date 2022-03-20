package com.lemon.tree.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class ErrorResponse {
    protected String code;
    protected String message;
}
