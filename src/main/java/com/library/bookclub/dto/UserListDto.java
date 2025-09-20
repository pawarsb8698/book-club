package com.library.bookclub.dto;


import com.library.bookclub.entity.User;
import com.library.bookclub.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserListDto {

    private long userId;
    private String username;
    private String email;
    private boolean isAdmin;
    private boolean isAvailable;

    public UserListDto(User user) {
        this.userId = user.getId();
        this.username = user.getFirstName() + " " + user.getLastName();
        this.email = user.getLogin();
        this.isAdmin = user.getUserType().equals(UserType.ADMIN);
        this.isAvailable = user.isAvailable();
    }
}
