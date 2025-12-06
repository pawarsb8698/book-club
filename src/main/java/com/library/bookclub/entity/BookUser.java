package com.library.bookclub.entity;

import com.library.bookclub.dto.BookUserDto;
import com.library.bookclub.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "book_user")
public class BookUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_user_id")  // PK column
    private Integer bookUserId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(name= "is_available", columnDefinition = "boolean default true")
    private boolean isAvailable;

    public BookUser(BookUserDto bookUserDto) {
        this.bookUserId = bookUserDto.getBookUserId();
        this.firstName = bookUserDto.getFirstName();
        this.lastName = bookUserDto.getLastName();
        this.userType = bookUserDto.getUserType();
        this.isAvailable = bookUserDto.isAvailable();
    }
}
