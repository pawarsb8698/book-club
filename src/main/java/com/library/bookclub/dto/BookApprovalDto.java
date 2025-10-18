package com.library.bookclub.dto;

import com.library.bookclub.entity.BookApproval;
import com.library.bookclub.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookApprovalDto {

    private Integer bookApprovalId;
    private int bookId;
    private int userId;
    private BookStatus bookStatus;

    public BookApprovalDto(BookApproval bookApproval) {
        this.bookApprovalId = bookApproval.getBookApprovalId();
        this.bookId = bookApproval.getBookId();
        this.userId = bookApproval.getUserId();
        this.bookStatus = bookApproval.getBookStatus();
    }
}
