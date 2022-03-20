package com.lemon.tree.domain.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK
    private String title; // 제목
    private String contents; // 내용
    private String nickName; // 작성자
    @CreatedDate
    private LocalDateTime createdAt; // 생성일
    @LastModifiedDate
    private LocalDateTime updatedAt; // 수정일
    @ColumnDefault("0")
    private Double orderId; // 정렬 순서 값

    @Builder
    public Board(String title, String contents, String nickName, Double orderId) {
        this.title = title;
        this.contents = contents;
        this.nickName = nickName;
        this.orderId = orderId;
    }
}