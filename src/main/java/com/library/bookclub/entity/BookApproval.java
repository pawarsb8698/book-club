package com.library.bookclub.entity;

import com.library.bookclub.dto.BookApprovalDto;
import com.library.bookclub.dto.BookHistoryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book_approval")
public class BookApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_approval_id")
    private Integer bookApprovalId;

    @Column(name = "book_id")
    private int bookId;

    @Column(name = "user_id")
    private int userId;

    public BookApproval(BookApprovalDto bookApprovalDto){
        this.bookApprovalId =bookApprovalDto.getBookApprovalId();
        this.bookId = bookApprovalDto.getBookId();
        this.userId = bookApprovalDto.getUserId();

    }
}
