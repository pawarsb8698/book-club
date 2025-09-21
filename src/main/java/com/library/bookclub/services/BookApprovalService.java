package com.library.bookclub.services;


import com.library.bookclub.dto.BookApprovalDto;
import com.library.bookclub.dto.BookHistoryDto;
import com.library.bookclub.entity.BookApproval;
import com.library.bookclub.repository.BookApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookApprovalService {

    private final BookApprovalRepository bookApprovalRepository;

    public void saveBookApproval(BookApprovalDto bookApprovalDto){
        BookApproval bookApproval = new BookApproval(bookApprovalDto);
        bookApprovalRepository.save(bookApproval);

    }

    public void deleteApprovalByUserId(int userId){
        bookApprovalRepository.deleteByUserId(userId);

    }

    public BookApprovalDto findByUserId(int userId){
       return new BookApprovalDto(bookApprovalRepository.findByUserId(userId));

    }

}
