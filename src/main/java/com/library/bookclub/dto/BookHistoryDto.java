package com.library.bookclub.dto;

import com.library.bookclub.entity.BookHistory;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class BookHistoryDto {

    private int bookHistoryId;
    private int borrowedBookId;
    private int userId;
    private String borrowedBookDate;
    private String returnDueDate;
    private String actualReturnDate;
    private int approvedByUserId;
    private String notes;

    public BookHistoryDto(BookDto bookDto, int approverUserId, int borrowedByUserId) {
        this.borrowedBookId = bookDto.getBookId();
        this.userId = borrowedByUserId;
        this.borrowedBookDate = bookDto.getBorrowedDate();
        this.returnDueDate = bookDto.getReturnDueDate();
        this.approvedByUserId = approverUserId;
    }

    public BookHistoryDto(BookHistory bookHistory) {
        this.borrowedBookId = bookHistory.getBorrowedBookId();
        this.userId = bookHistory.getUserId();
        this.borrowedBookDate =
                (bookHistory.getBorrowedBookDate() != null) ? bookHistory.getBorrowedBookDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.returnDueDate =
                (bookHistory.getReturnDueDate() != null) ? bookHistory.getReturnDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.approvedByUserId = bookHistory.getApprovedByUserId();
        this.actualReturnDate = (bookHistory.getActualReturnDate() != null) ? bookHistory.getActualReturnDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;


    }
}
