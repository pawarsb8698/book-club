package com.library.bookclub.dto;

import com.library.bookclub.entity.BookHistory;
import com.library.bookclub.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryDto {

    private BookUserDto userDto;
    private int bookHistoryId;
    private BookDto bookDto;
    private int userId;
    private String borrowedBookDate;
    private String returnDueDate;
    private String actualReturnDate;
    private Integer approvedByUserId;
    private BookStatus bookStatus;
    private String notes;

    public UserHistoryDto(BookHistory bookHistory) {
        this.userDto = new BookUserDto(bookHistory.getBookUser());
        this.bookHistoryId = bookHistory.getBookHistoryId();
        this.bookDto = new BookDto(bookHistory.getBook());
        this.borrowedBookDate =
                (bookHistory.getBorrowedBookDate() != null) ? bookHistory.getBorrowedBookDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.approvedByUserId = bookHistory.getApprovedByUserId();
        this.actualReturnDate = (bookHistory.getActualReturnDate() != null) ? bookHistory.getActualReturnDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.approvedByUserId = bookHistory.getApprovedByUserId();
        this.bookStatus = bookHistory.getBookStatus();
        this.notes = bookHistory.getNotes();
    }
}
