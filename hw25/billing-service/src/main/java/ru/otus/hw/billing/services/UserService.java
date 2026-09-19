package ru.otus.hw.billing.services;

import ru.otus.hw.billing.services.dto.UserCreateRequest;
import ru.otus.hw.billing.services.dto.UserResponse;

public interface UserService {
    UserResponse create(UserCreateRequest request);

    UserResponse findById(Long id);
}
