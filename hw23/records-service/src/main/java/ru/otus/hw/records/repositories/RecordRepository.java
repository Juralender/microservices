package ru.otus.hw.records.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.records.models.UserRecord;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<UserRecord, Long> {
    List<UserRecord> findAllByOwnerUsernameOrderByIdDesc(String ownerUsername);

    Optional<UserRecord> findByIdAndOwnerUsername(Long id, String ownerUsername);
}
