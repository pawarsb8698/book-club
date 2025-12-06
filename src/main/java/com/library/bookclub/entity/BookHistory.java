package com.library.bookclub.entity;

import com.library.bookclub.dto.UserBookHistoryDto;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowed_book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_user_id", nullable = false)
    private BookUser bookUser;

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

    public BookHistory(UserBookHistoryDto userBookHistoryDto) {
        this.actualReturnDate = StringUtils.isNotBlank(userBookHistoryDto.getActualReturnDate())
                ? LocalDate.parse(userBookHistoryDto.getActualReturnDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null;
        this.approvedByUserId = userBookHistoryDto.getApprovedByUserId();
        this.bookHistoryId = userBookHistoryDto.getUserBookHistoryId();
        this.returnDueDate =StringUtils.isNotBlank(userBookHistoryDto.getReturnDueDate())
                ? LocalDate.parse(userBookHistoryDto.getReturnDueDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null;
        this.borrowedBookDate = StringUtils.isNotBlank(userBookHistoryDto.getBorrowedBookDate())
                ? LocalDate.parse(userBookHistoryDto.getBorrowedBookDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null;
        this.notes = userBookHistoryDto.getNotes();
        this.bookUser = new BookUser(userBookHistoryDto.getBookUserDto());
        this.bookStatus = userBookHistoryDto.getBookStatus();
    }
}
