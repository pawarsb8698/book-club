package com.library.bookclub.repository;

import com.library.bookclub.dto.BookApprovalResponseDto;
import com.library.bookclub.entity.BookApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookApprovalRepository extends JpaRepository<BookApproval, Integer>  {

    void deleteByUserId(int userId);
    Optional<BookApproval> findByUserId(int userId);

    @Query(value = "SELECT " +
            "    ba.book_approval_id, " +
            "    CONCAT(u.first_name, ' ', u.last_name) AS userName, " +
            "    b.book_name AS bookName, " +
            "    b.book_status AS status, " +
            "    u.user_type AS userType, " +
            "    u.id AS userId " +
            "FROM " +
            "    book_approval ba " +
            "LEFT JOIN " +
            "    app_user u ON ba.user_id = u.id " +
            "LEFT JOIN " +
            "    books b ON ba.book_id = b.book_id;",
            nativeQuery = true)
    List<Object[]> findAllApprovals();



}
