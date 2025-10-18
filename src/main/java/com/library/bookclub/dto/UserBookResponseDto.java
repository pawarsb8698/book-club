package com.library.bookclub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBookResponseDto {
    private BookDto bookDto;
    private int userId;
}