package ru.otus.hw.records.rest.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.records.services.RecordService;
import ru.otus.hw.records.services.dto.RecordCreateRequest;
import ru.otus.hw.records.services.dto.RecordResponse;

import java.util.List;

// No update or delete endpoints by design: create + read-own only.
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecordResponse create(@RequestHeader("X-Username") String username,
                                  @Valid @RequestBody RecordCreateRequest request) {
        return recordService.create(username, request);
    }

    @GetMapping
    public List<RecordResponse> findMine(@RequestHeader("X-Username") String username) {
        return recordService.findAllForOwner(username);
    }

    @GetMapping("/{id}")
    public RecordResponse findMineById(@RequestHeader("X-Username") String username, @PathVariable Long id) {
        return recordService.findByIdForOwner(username, id);
    }
}
