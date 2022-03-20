package com.lemon.tree.repository;

import com.lemon.tree.domain.dto.BoardRequestDto;
import com.lemon.tree.domain.entity.Board;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BoardRepositoryTests {
    @Autowired
    BoardRepository boardRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void saveTest() {
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        boardRequestDto.setTitle("title");
        boardRequestDto.setContents("contents");
        boardRequestDto.setNickName("test");
        Board board = boardRepository.save(boardRequestDto.toEntity());

        Double order_id = board.getId() * 1.0;
        board.setOrderId(order_id);
        Board storedBoard = boardRepository.save(board);

        assertThat(board.getId()).isEqualTo(storedBoard.getId());
    }

    @Test
    public void findAllTest() {
        Integer page = 1;
        Pageable pageList = PageRequest.of((page-1), 10, Sort.by("orderId"));

        Page<Board> list = boardRepository.findAll(pageList);

        assertThat(list.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void updateTest() {
        Long id = 1L;
        Optional<Board> optionalBoard = boardRepository.findById(id);

        Board board = optionalBoard.get();
        board.setTitle("test update");
        board.setContents("test");
        board.setNickName("test");
        board.setUpdatedAt(LocalDateTime.now());

        Board saveBoard = boardRepository.save(board);
        assertThat(saveBoard.getId()).isEqualTo(board.getId());
    }

    @Test
    public void deleteTest() {
        Long id = 1L;
        Optional<Board> optionalBoard = boardRepository.findById(id);
        Long deleteId = optionalBoard.get().getId();
        boardRepository.deleteById(deleteId);

        Optional<Board> findBoard = boardRepository.findById(deleteId);

        assertThat(findBoard.isPresent()).isEqualTo(false);
    }
}
