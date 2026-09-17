package ru.otus.hw.auth.services;

import ru.otus.hw.auth.services.dto.UpdateProfileRequest;
import ru.otus.hw.auth.services.dto.UserProfileResponse;

public interface ProfileService {
    UserProfileResponse getProfile(Long requestedId, Long callerId);

    UserProfileResponse updateProfile(Long requestedId, Long callerId, UpdateProfileRequest request);
}
