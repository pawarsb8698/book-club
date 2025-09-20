package com.library.bookclub.mappers;

import com.library.bookclub.dtos.SignUpDto;
import com.library.bookclub.dtos.UserDto;
import com.library.bookclub.entity.User;
import com.library.bookclub.enums.UserType;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    default User signUpToUser(SignUpDto signUpDto) {
        User user = new User();
        user.setFirstName(signUpDto.firstName());
        user.setLogin(signUpDto.login());
        user.setLastName(signUpDto.lastName());
        user.setUserType(UserType.EMPLOYEE);
        user.setAvailable(true);
        return user;
    }

}
