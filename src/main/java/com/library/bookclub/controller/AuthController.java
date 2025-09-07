package com.library.bookclub.controller;
import com.library.bookclub.config.UserAuthenticationProvider;
import com.library.bookclub.dtos.CredentialsDto;
import com.library.bookclub.dtos.SignUpDto;
import com.library.bookclub.dtos.UserDto;
import com.library.bookclub.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody CredentialsDto credentialsDto,
                                         HttpServletResponse response) {
        UserDto userDto = userService.login(credentialsDto);
        String token = userAuthenticationProvider.createToken(userDto);

        // Create HTTP-only cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true) // set false if not using HTTPS locally
                .path("/")
                .maxAge(60 * 60) // 1 hour
                .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        userDto.setToken(null); // Don't send token in body anymore
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody SignUpDto user,
                                            HttpServletResponse response) {
        UserDto createdUser = userService.register(user);
        String token = userAuthenticationProvider.createToken(createdUser);

        // Set cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        createdUser.setToken(null);
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

}
