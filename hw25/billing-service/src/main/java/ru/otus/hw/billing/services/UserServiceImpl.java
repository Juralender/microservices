package ru.otus.hw.billing.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.billing.exceptions.UserAlreadyExistsException;
import ru.otus.hw.billing.exceptions.UserNotFoundException;
import ru.otus.hw.billing.models.Account;
import ru.otus.hw.billing.models.User;
import ru.otus.hw.billing.repositories.AccountRepository;
import ru.otus.hw.billing.repositories.UserRepository;
import ru.otus.hw.billing.services.dto.UserCreateRequest;
import ru.otus.hw.billing.services.dto.UserResponse;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }

        var now = Instant.now();
        var user = userRepository.save(new User(null, request.getUsername(), request.getEmail(), now));

        accountRepository.save(new Account(null, user.getId(), BigDecimal.ZERO, now, now));
        log.info("Created user {} ({}) with a zero-balance account", user.getId(), user.getUsername());

        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return toResponse(user);
    }

    private static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt());
    }
}
