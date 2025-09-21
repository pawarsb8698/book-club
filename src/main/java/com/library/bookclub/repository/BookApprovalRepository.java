package com.library.bookclub.repository;

import com.library.bookclub.entity.BookApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookApprovalRepository extends JpaRepository<BookApproval, Integer>  {

    void deleteByUserId(int userId);
    BookApproval findByUserId(int userId);
}
