package com.eventty.eventtynextgen.events.component;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class EventDetailsValidator {

    public VerifyResult validateEventDetails(LocalDateTime applyStartAt, LocalDateTime applyEndAt) {
        if (!validateApplyTime(applyStartAt, applyEndAt)) {
            return new VerifyResult(VerifyEventDetailsResult.ILLEGAL_ARGUMENT_APPLY_END_BEFORE_START, "Apply end time should be after apply start time applyStartAt: " + applyStartAt + " applyEndAt: " + applyEndAt + " applyEndAt: " + applyEndAt);
        }

        return new VerifyResult(VerifyEventDetailsResult.VERIFIED, "");
    }

    private boolean validateApplyTime(LocalDateTime applyStartAt, LocalDateTime applyEndAt) {
        return applyStartAt.isBefore(applyEndAt);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VerifyResult {
        private VerifyEventDetailsResult verifyEventDetailsResult;
        private String details;
    }

    public enum VerifyEventDetailsResult {
        VERIFIED,
        ILLEGAL_ARGUMENT_APPLY_END_BEFORE_START
    }
}
