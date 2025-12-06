package com.library.bookclub.services;

import com.library.bookclub.dto.BookUserDto;
import com.library.bookclub.dto.UserListDto;
import com.library.bookclub.dtos.CredentialsDto;
import com.library.bookclub.dtos.SignUpDto;
import com.library.bookclub.dtos.UserDto;
import com.library.bookclub.entity.User;
import com.library.bookclub.enums.UserType;
import com.library.bookclub.exception.AppException;
import com.library.bookclub.mappers.UserMapper;
import com.library.bookclub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByLogin(credentialsDto.login())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        if (!user.isAvailable()) {
            throw new AppException("User unavailable.", HttpStatus.BAD_REQUEST);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByLogin(userDto.login());
        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.signUpToUser(userDto);
        if (getAllUsers().isEmpty()) {
            user.setUserType(UserType.SUPERUSER);
        }
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.password())));
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    public UserDto findByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public BookUserDto findById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return new BookUserDto(user.getBookUser());
    }

    public List<UserListDto> getAllUsersExceptSuperUser() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getUserType().equals(UserType.SUPERUSER))
                .map(UserListDto::new)
                .toList();
    }


    public void updateUseRole(int userId, UserType userType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        user.setUserType(userType);
        userRepository.save(user);
    }

    public void updateAvailability(int userId, boolean isAvailable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        user.setAvailable(isAvailable);
        userRepository.save(user);
    }

    private List<UserListDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserListDto::new)
                .toList();
    }

}
