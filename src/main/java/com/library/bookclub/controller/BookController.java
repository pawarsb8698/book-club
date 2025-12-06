package com.library.bookclub.controller;

import com.library.bookclub.dto.*;
import com.library.bookclub.dtos.UserDto;
import com.library.bookclub.enums.BookStatus;
import com.library.bookclub.service.BookService;
import com.library.bookclub.services.BookApprovalService;
import com.library.bookclub.services.BookHistoryService;
import lombok.AllArgsConstructor;
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
import java.util.Collections;
import java.util.List;

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
    public ResponseEntity<BookListResponse> listBooks(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "8") int numberOfBooksPerPage, Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        boolean hasBorrrowedBook;
        BookApprovalDto bookApprovalDto = bookApprovalService.findByUserId(currentUser.getId());
        if(bookApprovalDto!=null){
            hasBorrrowedBook = true;
        } else {
            List<UserBookHistoryDto> userBookHistory = bookHistoryService.getBooksByUserId(currentUser.getId());
            Collections.reverse(userBookHistory);
            UserBookHistoryDto userBookHistoryDto = userBookHistory.stream()
                    .findFirst()
                    .orElse(null);
            hasBorrrowedBook = userBookHistoryDto != null && (userBookHistoryDto.getBookStatus().equals(BookStatus.BORROWED) ||
                    userBookHistoryDto.getBookStatus().equals(BookStatus.RETURN_PENDING) ||
                    userBookHistoryDto.getBookStatus().equals(BookStatus.BORROW_PENDING));
        }
        Page<BookDto> books = bookService.getAllBooks(pageNumber, numberOfBooksPerPage);
        return ResponseEntity.ok(new BookListResponse(books, hasBorrrowedBook));
    }

    @GetMapping("/borrow/{id}")
    public ResponseEntity<BookDto> borrowBook(@PathVariable("id") Integer bookId, Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookDto bookDto = bookService.getBookById(bookId);
        if (!bookDto.getBookStatus().equals(BookStatus.AVAILABLE.name())) {
            throw new RuntimeException("Book is unavailable.");
        }
        bookDto.setBookStatus(BookStatus.BORROW_PENDING.name());
        bookDto.setBorrowedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookDto.setReturnDueDate(LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        BookDto borrowedBook = bookService.updateBook(bookId, bookDto);
        bookApprovalService.saveBookApproval(new BookApprovalDto(null, bookId, currentUser.getId(), BookStatus.BORROW_PENDING));
        bookHistoryService.saveBookHistory(borrowedBook, null, currentUser.getId());
        return ResponseEntity.ok(borrowedBook);
    }

    @GetMapping("/return/{id}")
    public ResponseEntity<BookDto> returnBook(@PathVariable("id") int bookId, Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookDto bookDto = bookService.getBookById(bookId);
        bookDto.setBookStatus(BookStatus.RETURN_PENDING.name());
        bookDto.setBorrowedDate(null);
        bookDto.setReturnDueDate(null);
        BookDto returnedBook = bookService.updateBook(bookId, bookDto);
        bookApprovalService.saveBookApproval(new BookApprovalDto(null, bookId, currentUser.getId(),
                BookStatus.RETURN_PENDING));
        bookHistoryService.saveBookHistory(returnedBook, null, currentUser.getId());
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
    public ResponseEntity<List<BookApprovalResponseDto>> approveBookRequest(@PathVariable("id") int approvalId, Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookApprovalDto bookApprovalDto = bookApprovalService.findById(approvalId);
        BookDto bookDto = bookService.getBookById(bookApprovalDto.getBookId());
        bookApprovalService.deleteById(approvalId);
        if (!bookDto.getBookStatus().equals(BookStatus.BORROW_PENDING.name())) {
            throw new RuntimeException("Book isn't available");
        }
        bookDto.setBookStatus(BookStatus.BORROWED.name());
        bookDto.setBorrowedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookDto.setReturnDueDate(LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookHistoryService.saveBookHistory(bookDto, currentUser.getId(), bookApprovalDto.getUserId());
        return getApprovals();
    }

    @GetMapping("/reject/{id}")
    public ResponseEntity<List<BookApprovalResponseDto>> rejectBookRequest(@PathVariable("id") Integer approvalId, Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookApprovalDto bookApprovalDto = bookApprovalService.findById(approvalId);
        bookApprovalService.deleteById(approvalId);
        BookDto bookDto = bookService.getBookById(bookApprovalDto.getBookId());
        if (!bookDto.getBookStatus().equals(BookStatus.BORROW_PENDING.name())) {
            throw new RuntimeException("Book isn't available");
        }
        bookDto.setBookStatus(BookStatus.AVAILABLE.name());
        bookDto.setBorrowedDate(null);
        bookDto.setReturnDueDate(null);
        bookHistoryService.saveBookHistory(bookDto, currentUser.getId(), bookApprovalDto.getUserId());
        return getApprovals();
    }

    @GetMapping("/borrowedBook")
    public ResponseEntity<BookDto> getBorrowedBook(Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookApprovalDto bookApprovalDto = bookApprovalService.findByUserId(currentUser.getId());
        BookDto bookDto;
        if (bookApprovalDto == null) {
            List<UserBookHistoryDto> userBookHistory = bookHistoryService.getBooksByUserId(currentUser.getId());
            Collections.reverse(userBookHistory);
            UserBookHistoryDto userBookHistoryDto = userBookHistory.stream()
                            .findFirst()
                            .filter(history -> !history.getBookStatus().equals(BookStatus.AVAILABLE))
                            .orElse(null);
            bookDto = userBookHistoryDto == null ? null : userBookHistoryDto.getBookDto();
        } else {
            bookDto = bookService.getBookById(bookApprovalDto.getBookId());
        }
        return ResponseEntity.ok(bookDto);
    }

    @GetMapping("/withdrawApproval")
    public ResponseEntity<String> withdrawApproval(Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        int bookId = bookApprovalService.findByUserId(currentUser.getId()).getBookId();
        bookApprovalService.deleteApprovalByUserId(currentUser.getId());
        BookDto bookDto = bookService.getBookById(bookId);
        bookDto.setBorrowedDate(null);
        bookDto.setReturnDueDate(null);
        bookDto.setBookStatus(BookStatus.AVAILABLE.name());
        bookService.updateBook(bookDto.getBookId(), bookDto);
        bookHistoryService.saveBookHistory(bookDto, null, currentUser.getId());
        return new ResponseEntity<>("Withdrawn Successfully!", HttpStatus.CREATED);
    }

    @GetMapping("/getApprovals")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<BookApprovalResponseDto>> getApprovals() {
        List<Object[]> rows = bookApprovalService.getBookApprovals();

        List<BookApprovalResponseDto> approvals = rows.stream()
                .map(r -> new BookApprovalResponseDto(
                        ((Number) r[0]).longValue(),  // approval id
                        (String) r[1],                // userName
                        (String) r[2],                // bookName
                        (String) r[3],                // status
                        (String) r[4],
                        (Long) r[5]))     // userId
                .toList();

        return ResponseEntity.ok(approvals);
    }

    @GetMapping("/acceptReturn/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<BookApprovalResponseDto>> acceptReturn(@PathVariable("id")int approvalId,
                                                                      Authentication authentication) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        BookApprovalDto bookApprovalDto = bookApprovalService.findById(approvalId);
        bookApprovalService.deleteById(approvalId);
        BookDto bookDto = bookService.getBookById(bookApprovalDto.getBookId());
        if (!bookDto.getBookStatus().equals(BookStatus.RETURN_PENDING.name())) {
            throw new RuntimeException("Book isn't available");
        }
        bookDto.setBookStatus(BookStatus.AVAILABLE.name());
        bookDto.setBorrowedDate(null);
        bookDto.setReturnDueDate(null);
        bookHistoryService.saveBookHistory(bookDto, currentUser.getId(), bookApprovalDto.getUserId());
        return getApprovals();
    }

    @GetMapping("/userHistory/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserHistoryDto>> getUserHistoryForBook(Authentication authentication, @PathVariable("id")int bookId) {
        List<UserHistoryDto> userBookHistoryDtos = bookHistoryService.getHistoryForBook(bookId);
        return ResponseEntity.ok(userBookHistoryDtos);
    }

    @GetMapping("/bookHistory/{id}")
    public ResponseEntity<List<UserBookHistoryDto>> getBooksHistoryForUser(Authentication authentication, @PathVariable("id")int userId) {
        UserDto currentUser = (UserDto) authentication.getPrincipal();
        if(userId != currentUser.getId()) {
            throw new RuntimeException("You do not have access!!!");
        }
        List<UserBookHistoryDto> userBookHistoryDtos = bookHistoryService.getBooksByUserId(currentUser.getId());
        return ResponseEntity.ok(userBookHistoryDtos);
    }
}