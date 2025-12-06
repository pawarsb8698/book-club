package com.library.bookclub.dto;

import com.library.bookclub.dtos.UserDto;
import com.library.bookclub.entity.BookUser;
import com.library.bookclub.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class BookUserDto {

    private Integer bookUserId;
    private String firstName;
    private String lastName;
    private UserType userType;
    private boolean isAvailable;

    public BookUserDto(BookUser bookUser){
        this.bookUserId = bookUser.getBookUserId();
        this.firstName = bookUser.getFirstName();
        this.lastName = bookUser.getLastName();
        this.userType = bookUser.getUserType();
        this.isAvailable = bookUser.isAvailable();
    }
}
