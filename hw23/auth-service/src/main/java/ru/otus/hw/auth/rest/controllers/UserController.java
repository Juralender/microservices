package ru.otus.hw.auth.rest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.auth.services.ProfileService;
import ru.otus.hw.auth.services.dto.UpdateProfileRequest;
import ru.otus.hw.auth.services.dto.UserProfileResponse;

// Reachable only through the gateway's forwardAuth-protected router; see dynamic.yml.
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ProfileService profileService;

    @GetMapping("/{id}")
    public UserProfileResponse getProfile(@RequestHeader("X-User-Id") Long callerId, @PathVariable Long id) {
        return profileService.getProfile(id, callerId);
    }

    @PutMapping("/{id}")
    public UserProfileResponse updateProfile(@RequestHeader("X-User-Id") Long callerId,
                                              @PathVariable Long id,
                                              @Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(id, callerId, request);
    }
}
