package ru.otus.hw.auth.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.auth.exceptions.AccessDeniedException;
import ru.otus.hw.auth.services.dto.RegisterRequest;
import ru.otus.hw.auth.services.dto.UpdateProfileRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProfileServiceImplTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProfileService profileService;

    @Test
    void ownerCanReadAndUpdateTheirOwnProfile() {
        var user = authService.register(new RegisterRequest("dave", "s3cret!"));

        var updated = profileService.updateProfile(user.getId(), user.getId(),
                new UpdateProfileRequest("Dave", "dave@example.com"));
        assertThat(updated.getDisplayName()).isEqualTo("Dave");
        assertThat(updated.getEmail()).isEqualTo("dave@example.com");

        var fetched = profileService.getProfile(user.getId(), user.getId());
        assertThat(fetched.getDisplayName()).isEqualTo("Dave");
    }

    @Test
    void otherUserCannotReadOrEditProfile() {
        var owner = authService.register(new RegisterRequest("erin", "s3cret!"));
        var intruder = authService.register(new RegisterRequest("frank", "s3cret!"));

        assertThatThrownBy(() -> profileService.getProfile(owner.getId(), intruder.getId()))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> profileService.updateProfile(owner.getId(), intruder.getId(),
                new UpdateProfileRequest("Hacked", null)))
                .isInstanceOf(AccessDeniedException.class);
    }
}
