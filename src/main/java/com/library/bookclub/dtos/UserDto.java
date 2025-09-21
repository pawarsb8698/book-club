package com.library.bookclub.dtos;

import com.library.bookclub.enums.UserType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private String login;
    private String token;
    private UserType userType;

}
