package com.lemon.tree.domain.dto;

import com.lemon.tree.domain.entity.Board;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponseDto {
    private Long id;
    private String title;
    private String contents;
    private String nickName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.nickName = board.getNickName();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
    }
}
