package ru.otus.hw.records.services;

import ru.otus.hw.records.services.dto.RecordCreateRequest;
import ru.otus.hw.records.services.dto.RecordResponse;

import java.util.List;

public interface RecordService {
    RecordResponse create(String ownerUsername, RecordCreateRequest request);

    List<RecordResponse> findAllForOwner(String ownerUsername);

    RecordResponse findByIdForOwner(String ownerUsername, Long id);
}
