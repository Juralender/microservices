package ru.otus.hw.notification.rest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.notification.services.NotificationService;
import ru.otus.hw.notification.services.dto.EmailMessageResponse;
import ru.otus.hw.notification.services.dto.SendEmailRequest;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    public EmailMessageResponse sendEmail(@Valid @RequestBody SendEmailRequest request) {
        return notificationService.sendEmail(request);
    }

    @GetMapping
    public List<EmailMessageResponse> list(@RequestParam(required = false) String recipient) {
        return StringUtils.hasText(recipient)
                ? notificationService.findByRecipient(recipient)
                : notificationService.findAll();
    }
}
