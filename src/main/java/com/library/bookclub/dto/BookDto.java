package com.library.bookclub.dto;

import com.library.bookclub.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Integer bookId;
    private String bookName;
    private String genre;
    private String author;

    public BookDto(Book book){
        this.bookId = book.getBookId();
        this.bookName = book.getBookName();
        this.genre = book.getGenre();
        this.author = book.getAuthor();
    }
}
