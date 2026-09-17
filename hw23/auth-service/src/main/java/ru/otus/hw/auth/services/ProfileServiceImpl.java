package ru.otus.hw.auth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.auth.exceptions.AccessDeniedException;
import ru.otus.hw.auth.exceptions.UserNotFoundException;
import ru.otus.hw.auth.models.User;
import ru.otus.hw.auth.repositories.UserRepository;
import ru.otus.hw.auth.services.dto.UpdateProfileRequest;
import ru.otus.hw.auth.services.dto.UserProfileResponse;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long requestedId, Long callerId) {
        requireOwner(requestedId, callerId);
        var user = userRepository.findById(requestedId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + requestedId + " not found"));
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long requestedId, Long callerId, UpdateProfileRequest request) {
        requireOwner(requestedId, callerId);
        var user = userRepository.findById(requestedId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + requestedId + " not found"));
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        var saved = userRepository.save(user);
        return toResponse(saved);
    }

    private static void requireOwner(Long requestedId, Long callerId) {
        if (!requestedId.equals(callerId)) {
            throw new AccessDeniedException("You may only access your own profile");
        }
    }

    private static UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getEmail());
    }
}
