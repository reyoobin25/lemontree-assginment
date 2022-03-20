package com.lemon.tree.service;

import com.lemon.tree.domain.dto.BoardOrderUpdateDto;
import com.lemon.tree.domain.dto.BoardRequestDto;
import com.lemon.tree.domain.dto.BoardResponseDto;
import com.lemon.tree.domain.entity.Board;
import com.lemon.tree.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BoardServiceTests {

    @Autowired
    BoardService boardService;
    @Autowired
    BoardRepository boardRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Test
//    public void bulkSave() {
//        List list = new ArrayList();
//        for (int i = 1; i < 50000; i++) {
//            Board board = new Board(i + " no title", i + " contents", i + "tester", i * 1.0);
//            list.add(board);
//            if (0 == (i % 1000)) {
//                boardRepository.saveAll(list);
//                list.clear();
//            }
//            logger.info("current idx = {}", i);
//        }
//        boardRepository.saveAll(list);
//    }

//    @Test
//    public void bulkOrderUpdate() {
//        ArrayList<Board> list = (ArrayList<Board>) boardRepository.findAll();
//        for (int i = 0; i < list.size(); i++) {
//            Board board = list.get(i);
//            board.setOrderId(board.getId() * 1.0);
//        }
//        boardRepository.saveAll(list);
//    }

    @Test
    public void findBetweenOrderTest() {
        List<Double> board = boardRepository.getBetweenOrder(48, 2);

        assertThat(board.get(0)).isEqualTo(49.0);
        assertThat(board.get(1)).isEqualTo(50.0);
    }

    /**
     * 예외상황이 발생할 수 있는 내용으로, 아래 프로세스가 통과되어야만 한다.
     * @throws Exception
     */
    @Test
    public void checkChangeOrderTest() throws Exception {
        // 5개 입력
        List list = new ArrayList();
        for (int i = 1; i < 6; i++) {
            Board board = new Board(i + " no title", i + " contents", i + "tester", i * 1.0);
            list.add(board);
        }
        List<Board> boardList = boardRepository.saveAll(list);

        // 예외 케이스 통과가 되어야 한다.
        if (boardList.size() > 0) {
            // id 5 -> 2번째
            BoardOrderUpdateDto boar1 = new BoardOrderUpdateDto();
            boar1.setTargetOrderId(2);
            Long chageId = boardService.changeOrder(5L, boar1);
            assertThat(chageId).isEqualTo(5L);

            // id 1 삭제
            boardRepository.deleteById(1L);

            // id 3 -> 2번째
            BoardOrderUpdateDto boar2 = new BoardOrderUpdateDto();
            boar2.setTargetOrderId(2);
            Long id = boardService.changeOrder(3L, boar2);
            assertThat(id).isEqualTo(3L);
        }
    }
}
