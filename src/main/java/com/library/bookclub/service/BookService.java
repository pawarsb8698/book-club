package com.library.bookclub.service;

import com.library.bookclub.dto.BookDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto bookDto);
    BookDto getBookById(Integer bookId);
    Page<BookDto> getAllBooks(int page, int size);
    BookDto updateBook(Integer bookId, BookDto updatedBookDto);
    void deleteBook(Integer bookId);
}
