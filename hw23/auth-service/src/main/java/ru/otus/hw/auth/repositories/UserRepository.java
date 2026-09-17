package ru.otus.hw.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.auth.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
