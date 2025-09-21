package com.library.bookclub.services;


import com.library.bookclub.dto.BookDto;
import com.library.bookclub.dto.BookHistoryDto;
import com.library.bookclub.entity.Book;
import com.library.bookclub.entity.BookHistory;
import com.library.bookclub.repository.BookHistoryRepository;
import com.library.bookclub.repository.UserRepository;
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

    @Transactional
    public void saveBookHistory(BookDto bookDto, int approverUserId, int borrowedByUserId) {
        BookHistoryDto bookHistoryDto = new BookHistoryDto(bookDto, approverUserId, borrowedByUserId);
        BookHistory bookHistory = new BookHistory(bookHistoryDto);
        bookService.updateBook(bookDto.getBookId(), bookDto);
        bookHistoryRepository.save(bookHistory);
        bookApprovalService.deleteApprovalByUserId(bookHistoryDto.getUserId());
    }

    public List<BookHistoryDto> getBookByUserId(int userId) {
        return bookHistoryRepository.findAllByUserId(userId).stream()
                .map(BookHistoryDto::new)
                .collect(Collectors.toList());
    }
}
