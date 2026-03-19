package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByPublicRef(String publicRef);
    boolean existsByPublicRef(String publicRef);
}
