package com.library.bookclub.entity;

import com.library.bookclub.dto.BookDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POJO class.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="book_id")
    private Integer bookId;

    @Column(name="book_name")
    private String bookName;
    private String genre;
    private String author;
    @Column(name="image_name")
    private String imageName;

    public Book(BookDto bookDto) {
        this.bookId = bookDto.getBookId();
        this.bookName = bookDto.getBookName();
        this.genre = bookDto.getGenre();
        this.author = bookDto.getAuthor();
        this.imageName = bookDto.getImageName();
    }
}
