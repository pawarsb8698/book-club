package com.library.bookclub.services;


import com.library.bookclub.dto.BookApprovalDto;
import com.library.bookclub.entity.BookApproval;
import com.library.bookclub.repository.BookApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookApprovalService {

    private final BookApprovalRepository bookApprovalRepository;

    public void saveBookApproval(BookApprovalDto bookApprovalDto){
        BookApproval bookApproval = new BookApproval(bookApprovalDto);
        bookApprovalRepository.save(bookApproval);

    }

    @Transactional
    public void deleteApprovalByUserId(int userId) {
        bookApprovalRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteById(int approvalId) {
        bookApprovalRepository.deleteById(approvalId);
    }

    public BookApprovalDto findByUserId(int userId) {
        Optional<BookApproval> optionalApproval = bookApprovalRepository.findByUserId(userId);
        return optionalApproval.map(BookApprovalDto::new).orElse(null);
    }

    public BookApprovalDto findById(int approvalId) {
        return new BookApprovalDto(bookApprovalRepository.findById(approvalId).get());
    }

    public List<Object[]> getBookApprovals() {
        return bookApprovalRepository.findAllApprovals();
    }
}
