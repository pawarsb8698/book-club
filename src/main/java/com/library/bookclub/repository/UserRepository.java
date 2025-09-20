package com.library.bookclub.repository;

import com.library.bookclub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);
    List<User> findAll();
    Optional<User> findById(int id);
    User save(User user);
}
