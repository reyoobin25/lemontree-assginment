package com.lemon.tree.service;

import com.lemon.tree.domain.dto.BoardOrderUpdateDto;
import com.lemon.tree.domain.dto.BoardRequestDto;
import com.lemon.tree.domain.dto.BoardResponseDto;
import com.lemon.tree.domain.entity.Board;
import com.lemon.tree.repository.BoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * 글 저장
     * @param boardRequestDto
     */
    @Transactional
    public Long save(BoardRequestDto boardRequestDto) {
        Board board = boardRepository.save(boardRequestDto.toEntity());
        // auto-increment로 저장된 id를 oreder_id에 저장
        board.setOrderId(board.getId() * 1.0);

        Board storedBoard = boardRepository.save(board);
        return storedBoard.getId();
    }

    /**
     * 리스트 조회
     * @param page
     */
    public List<BoardResponseDto> findAll(int page) {
        if (0 < page) {
            Pageable pageList = PageRequest.of((page-1), 10, Sort.by("orderId"));
            Page<Board> list = boardRepository.findAll(pageList);
            return list.stream().map(BoardResponseDto::new).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("page를 확인해주세요");
        }
    }

    /**
     * 글 수정
     * @param id
     * @param boardRequestDto
     */
    @Transactional
    public Long update(Long id, BoardRequestDto boardRequestDto) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (true == optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            board.setTitle(boardRequestDto.getTitle());
            board.setContents(boardRequestDto.getContents());
            board.setNickName(boardRequestDto.getNickName());
            board.setUpdatedAt(LocalDateTime.now());

            Board result = boardRepository.save(board);
            return result.getId();
        } else {
            logger.info("[update] id = {}", id);
            throw new IllegalArgumentException("게시글을 찾지 못했습니다.");
        }
    }

    /**
     * 글 삭제
     * @param id
     */
    @Transactional
    public Long delete(Long id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (true == optionalBoard.isPresent()) {
            Long deleteId = optionalBoard.get().getId();
            boardRepository.deleteById(deleteId);
            return deleteId;
        } else {
            logger.info("[delete] id = {}", id);
            throw new IllegalArgumentException("게시글을 찾지 못했습니다.");
        }
    }

    /**
     * 순서 변경
     * @param id
     * @param boardOrderUpdateDto
     * @throws Exception
     */
    // TODO: 2022/03/20 동시성 체크
    public Long changeOrder(Long id, BoardOrderUpdateDto boardOrderUpdateDto) throws Exception {
        Integer targetId = boardOrderUpdateDto.getTargetOrderId();
        Integer totalCount = boardRepository.findAll().size(); // 전체 리스트 수
        Integer listCount = (targetId-2);
        Integer offset = 2;

        // 옮겨지는 순번은 0보다 크고, 전체 리스트 수보다 작거나 같아야 한다.
        if (0 < targetId && totalCount >= targetId) {
            // 가장 첫번째 또는 마지막으로 옮기는 경우
            if (1 == targetId || totalCount == targetId) {
                listCount = (targetId-1);
                offset = 1;
            }
            // 옮기고 싶은 순번에 사이 순번을 위해 값 2개를 가져온다.
            List<Double> board = boardRepository.getBetweenOrder(listCount, offset);
            Collections.sort(board);

            Double firstOrderId = board.get(0);
            // 첫번째 또는 마지막인 경우 1개만 나오기때문에 두번째 수는 0으로 넣는다.
            Double secondOrderId = (1 == targetId || totalCount == targetId) ? 0 : board.get(1);
            // 마지막 자리를 제외한 나머지는 사이값으로 지정한다.
            Double averageOrderId = (totalCount == targetId) ? (firstOrderId + 1) : (firstOrderId + secondOrderId) / 2;

            Optional<Board> tempBoard = boardRepository.findById(id);
            if (true == tempBoard.isPresent()) {
                tempBoard.get().setOrderId(averageOrderId);
                Board resultBoard = boardRepository.save(tempBoard.get());
                return resultBoard.getId();
            } else {
                logger.info("[changeOrder] id = {}", id);
                throw new IllegalArgumentException("게시글을 찾지 못했습니다.");
            }
        } else {
            logger.info("[changeOrder] targetId = {}", targetId);
            throw new IllegalArgumentException("targetId를 확인해주세요.");
        }
    }
}
