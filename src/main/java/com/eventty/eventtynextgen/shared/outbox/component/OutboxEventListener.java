package com.eventty.eventtynextgen.shared.outbox.component;

import static com.eventty.eventtynextgen.shared.constant.SharedConst.OBJECT_MAPPER;

import com.eventty.eventtynextgen.base.exception.CustomException;
import com.eventty.eventtynextgen.base.exception.enums.CommonErrorType;
import com.eventty.eventtynextgen.shared.outbox.OutboxRecord;
import com.eventty.eventtynextgen.shared.outbox.OutboxRecordRepository;
import com.eventty.eventtynextgen.shared.outbox.enums.AggregateType;
import com.eventty.eventtynextgen.shared.outbox.enums.EventType;
import com.eventty.eventtynextgen.shared.outbox.events.FileDeletedEvent;
import com.eventty.eventtynextgen.shared.utils.LoggerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final OutboxRecordRepository outboxEventRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOutboxEvent(FileDeletedEvent fileDeletedEvent) {
        try {
            String payload = OBJECT_MAPPER.writeValueAsString(fileDeletedEvent);
            OutboxRecord outboxRecord = OutboxRecord.of(AggregateType.ASSET_FILE, String.valueOf(fileDeletedEvent.getFileMetadataId()), EventType.FILE_DELETE, payload);

            this.outboxEventRepository.save(outboxRecord);
        } catch (JsonProcessingException e) {
            CustomException customException = CustomException.of(HttpStatus.INTERNAL_SERVER_ERROR, CommonErrorType.JSON_PROCESSING_ERROR, "Message: " + e.getMessage());
            customException.addSuppressed(e);
            LoggerUtils.error(customException);
            throw customException;
        }
    }
}
