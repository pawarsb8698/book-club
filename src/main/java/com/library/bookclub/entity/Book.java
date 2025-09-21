package com.library.bookclub.entity;

import ch.qos.logback.core.util.StringUtil;
import com.library.bookclub.dto.BookDto;
import com.library.bookclub.enums.BookStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * POJO class.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;
    @Column(name = "book_name", nullable = false)
    private String bookName;
    @Column(nullable = false)
    private String genre;
    @Column(nullable = false)
    private String author;
    @Column(name = "image_name", nullable = false)
    private String imageName;
    @Column(name = "book_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;
    @Column(nullable = false)
    private String owner;
    @Column(nullable = false)
    private String description;
    @Column(name = "borrowed_date")
    private LocalDate borrowedDate;
    @Column(name = "return_due_date")
    private LocalDate returnDueDate;

    public Book(BookDto bookDto) {
        this.bookId = bookDto.getBookId();
        this.bookName = bookDto.getBookName();
        this.genre = bookDto.getGenre();
        this.author = bookDto.getAuthor();
        this.imageName = bookDto.getImageName();
        this.bookStatus = BookStatus.valueOf(bookDto.getBookStatus());
        this.owner = bookDto.getOwner();
        this.description = bookDto.getDescription();
        this.borrowedDate = StringUtils.isNotBlank(bookDto.getBorrowedDate())
                ? LocalDate.parse(bookDto.getBorrowedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null;
        this.returnDueDate = StringUtils.isNotBlank(bookDto.getReturnDueDate())
                ? LocalDate.parse(bookDto.getReturnDueDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null;
    }
}
