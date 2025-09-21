package com.library.bookclub.repository;

import com.library.bookclub.entity.BookHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookHistoryRepository extends JpaRepository<BookHistory, Integer> {
    List<BookHistory> findAllByUserId(int userId);
}
