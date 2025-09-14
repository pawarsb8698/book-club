package com.library.bookclub.service.impl;

import com.library.bookclub.dto.BookDto;
import com.library.bookclub.entity.Book;
import com.library.bookclub.exception.ResourceNotFoundException;
import com.library.bookclub.repository.BookRepository;
import com.library.bookclub.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = new Book(bookDto);
        Book savedBook = bookRepository.save(book);
        return new BookDto(savedBook);
    }

    @Override
    public BookDto getBookById(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new ResourceNotFoundException("Book not Found"));
        return new BookDto(book);
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto updateBook(Integer bookId, BookDto updatedBookDto) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Book updatedBook = new Book(updatedBookDto);
        updatedBook.setImageName(book.getImageName());
        return new BookDto(bookRepository.save(updatedBook));
    }

    @Override
    public void deleteBook(Integer bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new ResourceNotFoundException("Book not found."));
        bookRepository.deleteById(bookId);
    }
}
