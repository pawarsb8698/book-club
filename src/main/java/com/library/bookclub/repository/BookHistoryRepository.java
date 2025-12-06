package com.library.bookclub.repository;

import com.library.bookclub.entity.BookHistory;
import com.library.bookclub.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookHistoryRepository extends JpaRepository<BookHistory, Integer> {
    List<BookHistory> findAllByBookHistoryId(int userId);
    List<BookHistory> findAllByBook_BookId(int bookId);
}