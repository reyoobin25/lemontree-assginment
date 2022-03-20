package com.lemon.tree.repository;

import com.lemon.tree.domain.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAll(Pageable pageable);

    /**
     * 옮기려는 순서의 사이 order_id 값 조회
     * @param start
     * @param offset
     * @return
     */
    @Query(value = "select order_id from board order by order_id asc limit ?1, ?2", nativeQuery = true)
    List<Double> getBetweenOrder(int start, int offset);
}
