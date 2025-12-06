package com.library.bookclub.services;


import com.library.bookclub.dto.BookDto;
import com.library.bookclub.dto.BookUserDto;
import com.library.bookclub.dto.UserBookHistoryDto;
import com.library.bookclub.dto.UserHistoryDto;
import com.library.bookclub.entity.Book;
import com.library.bookclub.entity.BookHistory;
import com.library.bookclub.repository.BookHistoryRepository;
import com.library.bookclub.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookHistoryService {
    private final BookHistoryRepository bookHistoryRepository;
    private final BookApprovalService bookApprovalService;
    private final BookService bookService;
    private final UserService userService;

    @Transactional
    public void saveBookHistory(BookDto bookDto, Integer approverUserId, int borrowedByUserId) {
        BookUserDto bookUserDto = userService.findById(borrowedByUserId);
        UserBookHistoryDto userBookHistoryDto = new UserBookHistoryDto(bookDto, approverUserId, bookUserDto);
        BookHistory bookHistory = new BookHistory(userBookHistoryDto);
        bookHistory.setBook(new Book(bookDto));
        bookService.updateBook(bookDto.getBookId(), bookDto);
        bookHistoryRepository.save(bookHistory);

    }

    public List<UserBookHistoryDto> getBooksByUserId(int userId) {
        return bookHistoryRepository.findAllByBookHistoryId(userId).stream()
                .map(UserBookHistoryDto::new)
                .collect(Collectors.toList());
    }

    public List<UserHistoryDto> getHistoryForBook(int bookId) {
        return bookHistoryRepository.findAllByBook_BookId(bookId)
                .stream()
                .map(UserHistoryDto::new)
                .toList();
    }
}
