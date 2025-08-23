package com.eventty.eventtynextgen.events.component;

import com.eventty.eventtynextgen.component.StorageService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateEventValidator {

    private final StorageService storageService;

    public VerifyResult validateEventBasic(LocalDateTime eventStartAt, LocalDateTime eventEndAt, List<String> imageUrls) {
        if (eventStartAt.isAfter(eventEndAt)) {
            return new VerifyResult(VerifyEventBasicResult.ILLEGAL_EVENT_END_BEFORE_START, "eventEndAt: " + eventEndAt + " is before eventStartAt: " + eventStartAt);
        }

        if (imageUrls != null && imageUrls.stream().anyMatch(this::isNotStorageUrl)) {
            return new VerifyResult(VerifyEventBasicResult.ILLEGAL_EVENT_IMAGE, "imageUrls: " + imageUrls.stream().filter(this::isNotStorageUrl).toList());
        }

        return new VerifyResult(VerifyEventBasicResult.VERIFIED, "");
    }


    private boolean isNotStorageUrl(String fileUrl) {
        return !this.storageService.fileExists(fileUrl);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VerifyResult {
        private VerifyEventBasicResult verifyEventBasicResult;
        private String details;
    }

    public enum VerifyEventBasicResult {
        VERIFIED,
        ILLEGAL_EVENT_END_BEFORE_START,
        ILLEGAL_EVENT_IMAGE
    }
}
