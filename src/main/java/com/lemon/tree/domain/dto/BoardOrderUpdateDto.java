package com.lemon.tree.domain.dto;

import lombok.Data;

@Data
public class BoardOrderUpdateDto {
    /**
     * To be, 옮기고 싶은 곳에 order id
     */
    private Integer targetOrderId;
}
