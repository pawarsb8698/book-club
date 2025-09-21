package com.library.bookclub.controller;

import com.library.bookclub.dto.BookApprovalDto;
import com.library.bookclub.dto.BookDto;
import com.library.bookclub.dto.BookHistoryDto;
import com.library.bookclub.dtos.UserDto;
import com.library.bookclub.enums.BookStatus;
import com.library.bookclub.service.BookService;
import com.library.bookclub.services.BookApprovalService;
import com.library.bookclub.services.BookHistoryService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {
    private static final String UPLOAD_DIR = "D:\\bookClubUploads";
    private BookService bookService;
    private BookHistoryService bookHistoryService;
    private BookApprovalService bookApprovalService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<BookDto> createBook(
            @RequestPart("book") BookDto book,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        book.setBookStatus(BookStatus.AVAILABLE.name());
        BookDto savedBook = bookService.createBook(book);
        try {
            saveFileToFolder(file, savedBook.getBookId());
        } catch (IOException e) {
            return ResponseEntity.ok(book);
        }
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<BookDto> getBook(@PathVariable("id") Integer bookId) {
        BookDto bookDto = bookService.getBookById(bookId);
        return ResponseEntity.ok(bookDto);
    }

    @GetMapping
    public Page<BookDto> listBooks(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "8") int numberOfBooksPerPage) {
        return bookService.getAllBooks(pageNumber, numberOfBooksPerPage);
    }

    @GetMapping("/borrow/{id}")
    public ResponseEntity<BookDto> borrowBook(@PathVariable("id") Integer bookId, Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookDto bookDto = bookService.getBookById(bookId);
        if (!bookDto.getBookStatus().equals(BookStatus.AVAILABLE.name())) {
            throw new RuntimeException("Book is unavailable.");
        }
        bookDto.setBookStatus(BookStatus.PENDING_APPROVAL.name());
        bookDto.setBorrowedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookDto.setReturnDueDate(LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        BookDto borrowedBook = bookService.updateBook(bookId, bookDto);
        bookApprovalService.saveBookApproval(new BookApprovalDto(null, bookId, currentUser.getId()));
        return ResponseEntity.ok(borrowedBook);
    }

    @GetMapping("/return/{id}")
    public ResponseEntity<BookDto> returnBook(@PathVariable("id") Integer bookId) {
        BookDto bookDto = bookService.getBookById(bookId);
        bookDto.setBookStatus(BookStatus.AVAILABLE.name());
        bookDto.setBorrowedDate(null);
        bookDto.setReturnDueDate(null);
        BookDto returnedBook = bookService.updateBook(bookId, bookDto);
        return ResponseEntity.ok(returnedBook);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BookDto> updateBook(@PathVariable("id") Integer bookId,
                                              @RequestPart("book") BookDto updatedBookDto,
                                              @RequestPart(value = "file", required = false) MultipartFile file) {
        BookDto bookDto = bookService.updateBook(bookId, updatedBookDto);
        try {
            saveFileToFolder(file, bookDto.getBookId());
        } catch (IOException e) {
            return ResponseEntity.ok(updatedBookDto);
        }
        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteBook(@PathVariable("id") Integer bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok("Book deleted successfully.");
    }

    private void saveFileToFolder(MultipartFile file, Integer bookId) throws IOException {
        if (file == null || file.isEmpty()) return;

        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException();
            }
        }

        String fileName = file.getOriginalFilename();
        Path filepath = Paths.get(UPLOAD_DIR, bookId + fileName.substring(fileName.lastIndexOf(".")));
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File saved to: " + filepath.toAbsolutePath());
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path path = Paths.get(UPLOAD_DIR).resolve(filename);
        byte[] image = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }

    @GetMapping("/approve/{id}")
    public ResponseEntity<String> approveBookRequest(@PathVariable("id") Integer borrowedByUserId, Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookApprovalDto bookApprovalDto = bookApprovalService.findByUserId(borrowedByUserId);
        BookDto bookDto = bookService.getBookById(bookApprovalDto.getBookId());
        if (!bookDto.getBookStatus().equals(BookStatus.AVAILABLE.name())) {
            throw new RuntimeException("Book isn't available");
        }
        bookDto.setBookStatus(BookStatus.BORROWED.name());
        bookDto.setBorrowedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookDto.setReturnDueDate(LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookHistoryService.saveBookHistory(bookDto, currentUser.getId(), borrowedByUserId);
        return new ResponseEntity<>("Resource not found!", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/reject/{id}")
    public ResponseEntity<String> rejectBookRequest(@PathVariable("id") Integer borrowedByUserId, Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookApprovalDto bookApprovalDto = bookApprovalService.findByUserId(borrowedByUserId);
        BookDto bookDto = bookService.getBookById(bookApprovalDto.getBookId());
        if (!bookDto.getBookStatus().equals(BookStatus.AVAILABLE.name())) {
            throw new RuntimeException("Book isn't available");
        }
        bookDto.setBookStatus(BookStatus.AVAILABLE.name());
        bookDto.setBorrowedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookDto.setReturnDueDate(LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookHistoryService.saveBookHistory(bookDto, currentUser.getId(), borrowedByUserId);
        return new ResponseEntity<>("Resource not found!", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/borrowedBook")
    public ResponseEntity<BookDto> getBorrowedBook(Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookApprovalDto bookApprovalDto = bookApprovalService.findByUserId(currentUser.getId());
        BookDto bookDto;
        if(bookApprovalDto == null) {
            BookHistoryDto bookHistoryDto =
        bookHistoryService.getBookByUserId(currentUser.getId()).stream()
                    .filter(b -> StringUtils.isBlank(b.getActualReturnDate())) // replace with your status
                    .findFirst().orElse(null);
            bookDto = bookHistoryDto == null ? null: bookService.getBookById(bookHistoryDto.getBorrowedBookId());
        } else {
            bookDto = bookService.getBookById(bookApprovalDto.getBookId());
        }
        return ResponseEntity.ok(bookDto);
    }
}