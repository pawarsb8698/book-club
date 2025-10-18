package com.library.bookclub.dto;

import com.library.bookclub.enums.BookStatus;
import com.library.bookclub.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookApprovalResponseDto {
    private Long id;
    private String userName;
    private String bookName;
    private String status;
    private String userType;
    private long userId;
}
