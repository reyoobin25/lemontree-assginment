package com.lemon.tree.controller;

import com.lemon.tree.common.DefaultErrorResponse;
import com.lemon.tree.common.ValidationErrorResponse;
import com.lemon.tree.domain.dto.BoardOrderUpdateDto;
import com.lemon.tree.domain.dto.BoardRequestDto;
import com.lemon.tree.domain.dto.BoardResponseDto;
import com.lemon.tree.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@EnableJpaAuditing
@RestController
@RequestMapping("/v1/api")
public class BoardController {

    private final BoardService boardService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BoardController(BoardService boardService) {
        this.boardService= boardService;
    }

    /**
     * exception handler에서 정의되지 않은 예외를 캐치하기 위한 처리
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<DefaultErrorResponse> unHandledException(Exception e) {
        logger.error("핸들링되지 않은 예외 발생 = {}", e);
        return ResponseEntity.ok(new DefaultErrorResponse());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleArgNotValid(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String fieldName = fieldError.getField();
        String message = fieldError.getDefaultMessage();

        return ResponseEntity.ok(new ValidationErrorResponse(fieldName, "400", message));
    }

    /**
     * 글 입력
     * @param request
     */
    @PostMapping("/boards")
    public Long save(@RequestBody @Valid BoardRequestDto request) {
        return boardService.save(request);
    }

    /**
     * 글 리스트 조회
     * @param page
     */
    @GetMapping("/boards")
    public List<BoardResponseDto> findAll(@RequestParam("page") int page) {
        return boardService.findAll(page);
    }

    /**
     * 글 수정
     * @param id
     * @param request
     */
    @PutMapping("/boards/{id}")
    public Long update(@PathVariable("id") Long id, @RequestBody @Valid BoardRequestDto request) {
        return boardService.update(id, request);
    }

    /**
     * 글 삭제
     * @param id
     */
    @DeleteMapping("/boards/{id}")
    public Long delete(@PathVariable("id") Long id) {
        return boardService.delete(id);
    }

    /**
     * 순서 변경
     * @param id
     * @param boardOrderUpdateDto
     * @throws Exception
     */
    @PostMapping("/board/{id}/order")
    public Long move(@PathVariable("id") Long id, @RequestBody BoardOrderUpdateDto boardOrderUpdateDto) throws Exception {
        return boardService.changeOrder(id, boardOrderUpdateDto);
    }
}
