package com.library.bookclub.entity;

import com.library.bookclub.enums.BookStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "book_history")
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_history_id")
    private int bookHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowed_book_id")
    private Book book;

    @Column(name = "borrowed_book_date")
    private LocalDate borrowedBookDate;

    @Column(name = "return_due_date")
    private LocalDate returnDueDate;

    @Column(name = "actual_return_due_date")
    private LocalDate actualReturnDate;

    @Column(name = "approved_by_user_id")
    private Integer approvedByUserId;

    @Column(name = "book_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private BookUser bookUser;
}
