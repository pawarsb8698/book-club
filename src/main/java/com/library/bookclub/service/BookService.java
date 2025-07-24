package com.library.bookclub.service;

import com.library.bookclub.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto bookDto);
    BookDto getBookById(Integer bookId);
    List<BookDto> getAllBooks();
    BookDto updateBook(Integer bookId, BookDto updatedBookDto);
    void deleteBook(Integer bookId);
}
