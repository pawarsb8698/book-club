package com.library.bookclub.entity;

import com.library.bookclub.dto.BookHistoryDto;
import com.library.bookclub.enums.BookStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book_history")
public class BookHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_history_id")
    private int bookHistoryId;

    @Column(name = "borrowed_book_id", nullable = false)
    private int borrowedBookId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "borrowed_book_date")
    private LocalDate borrowedBookDate;

    @Column(name = "return_due_date")
    private LocalDate returnDueDate;

    @Column(name = "actual_return_due_date")
    private LocalDate actualReturnDate;

    @Column(name = "approved_by_user_id")
    private Integer approvedByUserId;

    @Column(name = "book_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    private String notes;

    public BookHistory(BookHistoryDto bookHistoryDto) {
        this.actualReturnDate = StringUtils.isNotBlank(bookHistoryDto.getActualReturnDate())
                ? LocalDate.parse(bookHistoryDto.getActualReturnDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null;
        this.approvedByUserId = bookHistoryDto.getApprovedByUserId();
        this.bookHistoryId = bookHistoryDto.getBookHistoryId();
        this.returnDueDate =StringUtils.isNotBlank(bookHistoryDto.getReturnDueDate())
                ? LocalDate.parse(bookHistoryDto.getReturnDueDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null;
        this.borrowedBookDate = StringUtils.isNotBlank(bookHistoryDto.getBorrowedBookDate())
                ? LocalDate.parse(bookHistoryDto.getBorrowedBookDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null;
        this.notes = bookHistoryDto.getNotes();
        this.userId = bookHistoryDto.getUserId();
        this.bookStatus = bookHistoryDto.getBookStatus();
    }
}
