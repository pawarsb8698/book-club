package com.library.bookclub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class BookListResponse {

    private Page<BookDto> books;
    private boolean hasBorrowedAny;
}