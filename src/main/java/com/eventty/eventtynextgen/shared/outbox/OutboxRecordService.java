package com.eventty.eventtynextgen.shared.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Repository
@RequiredArgsConstructor
public class OutboxRecordService {

    private final OutboxRecordRepository outboxEventRepository;

    @Transactional
    public void saveOutboxEvent(OutboxRecord outboxRecord) {
        outboxEventRepository.save(outboxRecord);
    }
}
