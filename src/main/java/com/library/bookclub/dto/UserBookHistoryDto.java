package com.library.bookclub.dto;

import com.library.bookclub.entity.BookHistory;
import com.library.bookclub.enums.BookStatus;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class UserBookHistoryDto {

    private int userBookHistoryId;
    private BookDto bookDto;
    private BookUserDto bookUserDto;
    private String borrowedBookDate;
    private String returnDueDate;
    private String actualReturnDate;
    private Integer approvedByUserId;
    private BookStatus bookStatus;
    private String notes;

    public UserBookHistoryDto(BookDto bookDto, Integer approverUserId, BookUserDto bookUserDto) {
        this.bookDto = bookDto;
        this.bookUserDto = bookUserDto;
        this.borrowedBookDate = bookDto.getBorrowedDate();
        this.returnDueDate = bookDto.getReturnDueDate();
        this.approvedByUserId = approverUserId;
        this.bookStatus = BookStatus.valueOf(bookDto.getBookStatus());
    }

    public UserBookHistoryDto(BookHistory bookHistory) {
        this.bookDto = new BookDto(bookHistory.getBook());
        this.bookUserDto = new BookUserDto(bookHistory.getBookUser());
        this.borrowedBookDate =
                (bookHistory.getBorrowedBookDate() != null) ? bookHistory.getBorrowedBookDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.returnDueDate =
                (bookHistory.getReturnDueDate() != null) ? bookHistory.getReturnDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.approvedByUserId = bookHistory.getApprovedByUserId();
        this.actualReturnDate = (bookHistory.getActualReturnDate() != null) ? bookHistory.getActualReturnDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.bookStatus = bookHistory.getBookStatus();

    }
}
