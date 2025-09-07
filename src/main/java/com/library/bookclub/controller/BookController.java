package com.library.bookclub.controller;

import com.library.bookclub.dto.BookDto;
import com.library.bookclub.service.BookService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {
    private static final String UPLOAD_DIR = "D:\\bookClubUploads";
    private BookService bookService;

    @PostMapping
    public ResponseEntity<BookDto> createBook(
            @RequestPart("book") BookDto book,
            @RequestPart("file") MultipartFile file){
        BookDto savedBook = bookService.createBook(book);
        try {
            saveFileToFolder(file, savedBook.getBookId());
        } catch (IOException e) {
            return ResponseEntity.ok(book);
        }
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable("id") Integer bookId) {
        BookDto bookDto = bookService.getBookById(bookId);
        return ResponseEntity.ok(bookDto);
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks(HttpSession httpSession){
        List<BookDto> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @PutMapping("{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable("id") Integer bookId,
                                              @RequestPart("book") BookDto updatedBookDto,
                                              @RequestPart("file") MultipartFile file){
        BookDto bookDto = bookService.updateBook(bookId, updatedBookDto);
        try {
            saveFileToFolder(file, bookId);
        } catch (IOException e) {
            return ResponseEntity.ok(updatedBookDto);
        }
        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBook(@PathVariable("id") Integer bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok("Book deleted successfully.");
    }

    private void saveFileToFolder(MultipartFile file, Integer bookId) throws IOException {
        if (file.isEmpty()) return;

        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            if (!directory.mkdirs()){
                throw new IOException();
            }
        }

        String fileName = file.getOriginalFilename();
        Path filepath = Paths.get(UPLOAD_DIR, bookId + fileName.substring(fileName.lastIndexOf(".")));
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File saved to: " + filepath.toAbsolutePath());
    }
}