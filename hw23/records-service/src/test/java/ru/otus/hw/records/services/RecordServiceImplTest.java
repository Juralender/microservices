package ru.otus.hw.records.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.records.exceptions.RecordNotFoundException;
import ru.otus.hw.records.services.dto.RecordCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RecordServiceImplTest {

    @Autowired
    private RecordService recordService;

    @Test
    void ownerSeesOnlyOwnRecords() {
        recordService.create("alice", new RecordCreateRequest("alice's note"));
        recordService.create("bob", new RecordCreateRequest("bob's note"));

        var aliceRecords = recordService.findAllForOwner("alice");
        assertThat(aliceRecords).hasSize(1);
        assertThat(aliceRecords.get(0).getContent()).isEqualTo("alice's note");
    }

    @Test
    void ownerCanBeALongEmailAddress() {
        var email = "a-rather-long-local-part.with-dots.and-more-text@some-long-subdomain.example.com";

        recordService.create(email, new RecordCreateRequest("note"));

        assertThat(recordService.findAllForOwner(email)).hasSize(1);
    }

    @Test
    void cannotReadAnotherOwnersRecordById() {
        var created = recordService.create("dave", new RecordCreateRequest("dave's secret"));

        assertThatThrownBy(() -> recordService.findByIdForOwner("erin", created.getId()))
                .isInstanceOf(RecordNotFoundException.class);
    }
}
