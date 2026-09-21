package ru.otus.hw.records.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse {
    private Long id;
    private String content;
    private String ownerUsername;
    private Instant createdAt;
}
