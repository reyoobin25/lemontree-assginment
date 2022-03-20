package com.lemon.tree.domain.dto;

import com.lemon.tree.domain.entity.Board;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class BoardRequestDto {
    @NotBlank
    @Length(max=255, message = "제목은 최대 255자까지 가능합니다.")
    private String title;
    @NotBlank
    private String contents;
    @NotBlank
    private String nickName;

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .contents(contents)
                .nickName(nickName)
                .build();
    }
}
