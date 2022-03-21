package com.lemon.tree.service;

import com.lemon.tree.domain.dto.BoardOrderUpdateDto;
import com.lemon.tree.domain.entity.Board;
import com.lemon.tree.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class BoardServiceTests {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    BoardService boardService;
    BoardRepository boardRepository = mock(BoardRepository.class);

    @BeforeEach
    public void init(){
        logger.info("init!");
        boardService = new BoardService(boardRepository);
        MockitoAnnotations.openMocks(boardRepository);
    }

    /**
     * 순서 변경 시 사이 값 테스트
     */
    @Test
    public void changeOrderTest() throws Exception {
        List<Board> boardList = generateBoardList();

        mockFindAll(boardList);
        mockFindById(boardList);
        mockBetweenOrder(boardList);
        mockSave();

        BoardOrderUpdateDto dto = new BoardOrderUpdateDto();
        dto.setTargetOrderId(3);
        Long result = boardService.changeOrder(5L, dto);

        assertThat(result).isEqualTo(5L);
        ArgumentCaptor<Board> argument = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository, times(1)).findAll();
        verify(boardRepository).save(argument.capture());
        assertThat(argument.getValue().getOrderId()).isEqualTo(2.5);
    }

    /**
     * 음수를 넣었을 때, IllegalArgumentException exception을 던지는지
     */
    @Test
    public void negativeTargetIdTest() {
        List<Board> boardList = generateBoardList();

        mockFindAll(boardList);

        BoardOrderUpdateDto dto = new BoardOrderUpdateDto();
        dto.setTargetOrderId(-3);

        assertThrows(IllegalArgumentException.class, () -> boardService.changeOrder(5L, dto ));
    }

    /**
     * total값 보다 큰 값을 넣었을 때, IllegalArgumentException exception을 던지는지
     */
    @Test
    public void overTotalCountTargetIdTest() {
        List<Board> boardList = generateBoardList();

        mockFindAll(boardList);

        BoardOrderUpdateDto dto = new BoardOrderUpdateDto();
        dto.setTargetOrderId(10);

        assertThrows(IllegalArgumentException.class, () -> boardService.changeOrder(5L, dto ));
    }

    /**
     * 없는 게시글을 옮겼을 때
     */
    @Test
    public void notFoundTargetBoardTest() {
        List<Board> boardList = generateBoardList();

        mockFindAll(boardList);
        mockFindById(boardList);
        mockBetweenOrder(boardList);

        BoardOrderUpdateDto dto = new BoardOrderUpdateDto();
        dto.setTargetOrderId(2);
        assertThrows(IllegalArgumentException.class, () -> boardService.changeOrder(11L, dto ));
    }

    /**
     * 마지막 리스트로 옮겼을 때
     */
    @Test
    public void chageOrderLastListTest() throws Exception {
        List<Board> boardList = generateBoardList();

        mockFindAll(boardList);
        mockFindById(boardList);
        mockBetweenOrder(boardList);
        mockSave();

        BoardOrderUpdateDto dto = new BoardOrderUpdateDto();
        dto.setTargetOrderId(9);
        Long result = boardService.changeOrder(1L, dto);

        assertThat(result).isEqualTo(1L);
        ArgumentCaptor<Board> argument = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository, times(1)).findAll();
        verify(boardRepository).save(argument.capture());
        assertThat(argument.getValue().getOrderId()).isEqualTo(10);
    }

    /**
     * 9개 리스트를 만든다.
     */
    private List<Board> generateBoardList() {
        List<Board> boardList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            Board board = new Board(i + "title", i + "content", "lee", i * 1.0);
            board.setId(i * 1l);
            boardList.add(board);
        }
        return boardList;
    }

    private void mockFindAll(List<Board> boardList) {
        when(boardRepository.findAll()).thenReturn(boardList);
    }

    private void mockFindById(List<Board> boardList) {
        boardList.stream().forEach(
                board -> when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board))
        );
    }

    private void mockBetweenOrder(List<Board> boardList) {
        when(boardRepository.getBetweenOrder(anyInt(), anyInt())).thenAnswer((Answer<List<Double>>) invocation -> {
            Object[] args = invocation.getArguments();
            int listCount = (int) args[0];
            int offset = (int) args[1];
            return boardList.subList(listCount, listCount + offset).stream().map(board -> board.getOrderId()).collect(Collectors.toList());
        });
    }

    private void mockSave() {
        when(boardRepository.save(any())).thenAnswer((Answer<Board>) invocation -> invocation.getArgument(0));
    }
}
