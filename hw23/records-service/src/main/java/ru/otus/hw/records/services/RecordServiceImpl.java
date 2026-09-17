package ru.otus.hw.records.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.records.exceptions.RecordNotFoundException;
import ru.otus.hw.records.models.UserRecord;
import ru.otus.hw.records.repositories.RecordRepository;
import ru.otus.hw.records.services.dto.RecordCreateRequest;
import ru.otus.hw.records.services.dto.RecordResponse;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;

    @Override
    @Transactional
    public RecordResponse create(String ownerUsername, RecordCreateRequest request) {
        var record = new UserRecord(null, request.getContent(), ownerUsername, Instant.now());
        var saved = recordRepository.save(record);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordResponse> findAllForOwner(String ownerUsername) {
        return recordRepository.findAllByOwnerUsernameOrderByIdDesc(ownerUsername).stream()
                .map(RecordServiceImpl::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecordResponse findByIdForOwner(String ownerUsername, Long id) {
        var record = recordRepository.findByIdAndOwnerUsername(id, ownerUsername)
                .orElseThrow(() -> new RecordNotFoundException("Record with id " + id + " not found"));
        return toResponse(record);
    }

    private static RecordResponse toResponse(UserRecord record) {
        return new RecordResponse(record.getId(), record.getContent(), record.getOwnerUsername(), record.getCreatedAt());
    }
}
