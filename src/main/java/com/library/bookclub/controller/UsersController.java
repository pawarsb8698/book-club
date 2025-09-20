package com.library.bookclub.controller;


import com.library.bookclub.dto.UserListDto;
import com.library.bookclub.enums.UserType;
import com.library.bookclub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPERUSER')")
    public ResponseEntity<List<UserListDto>> getUsers() {
        List<UserListDto> users = new ArrayList<>(userService.getAllUsersExceptSuperUser());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/makeAdmin/{userId}")
    @PreAuthorize("hasAnyAuthority('SUPERUSER')")
    public ResponseEntity<List<UserListDto>> updateUserAsAdmin(@PathVariable("userId") int userId) {
        userService.updateUseRole(userId, UserType.ADMIN);
        return getUsers();
    }

    @GetMapping("/makeEmployee/{userId}")
    @PreAuthorize("hasAnyAuthority('SUPERUSER')")
    public ResponseEntity<List<UserListDto>> updateUserAsEmployee(@PathVariable("userId") int userId) {
        userService.updateUseRole(userId, UserType.EMPLOYEE);
        return getUsers();
    }
    @GetMapping("/makeUserAvailable/{userId}")
    @PreAuthorize("hasAnyAuthority('SUPERUSER')")
    public ResponseEntity<List<UserListDto>> makeUserAvailable(@PathVariable("userId") int userId) {
        userService.updateAvailability(userId, true);
        return getUsers();
    }
    @GetMapping("/makeUserUnavailable/{userId}")
    @PreAuthorize("hasAnyAuthority('SUPERUSER')")
    public ResponseEntity<List<UserListDto>> makeUserUnAvailable(@PathVariable("userId") int userId) {
        userService.updateAvailability(userId, false);
        return getUsers();
    }
}
