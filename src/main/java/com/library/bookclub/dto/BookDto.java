package com.library.bookclub.dto;

import com.library.bookclub.entity.Book;
import com.library.bookclub.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Integer bookId;
    private String bookName;
    private String genre;
    private String author;
    private String owner;
    private String description;
    private String imageName;
    private String bookStatus;
    private String borrowedDate;
    private String returnDueDate;

    public BookDto(Book book){
        this.bookId = book.getBookId();
        this.bookName = book.getBookName();
        this.genre = book.getGenre();
        this.author = book.getAuthor();
        this.imageName = book.getImageName();
        this.bookStatus = book.getBookStatus().name();
        this.owner = book.getOwner();
        this.description = book.getDescription();
        this.borrowedDate = (book.getBorrowedDate() != null) ? book.getBorrowedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        this.returnDueDate = (book.getReturnDueDate() != null) ? book.getReturnDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;

    }
}
