package com.lemon.tree.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultErrorResponse extends ErrorResponse {
    public DefaultErrorResponse() {
        this.code = "500";
        this.message = "Server에서 알 수 없는 에러가 발생했습니다."; // TODO: 2022/03/19 가장 앞단의 고객 메세지 고민..
    }
}
