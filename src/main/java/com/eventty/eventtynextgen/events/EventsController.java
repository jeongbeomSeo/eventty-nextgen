package com.eventty.eventtynextgen.events;

import com.eventty.eventtynextgen.base.annotation.LoginRequired;
import com.eventty.eventtynextgen.events.annotation.EventApiV1;
import com.eventty.eventtynextgen.events.request.EventsCreateEventRequestCommand;
import com.eventty.eventtynextgen.shared.context.SessionContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@EventApiV1
@RestController
@RequiredArgsConstructor
public class EventsController {

    private final EventsService eventsService;

    // TODO: 처리 방식 변경 (이미지 업로드 API 수행 후 해당 API 수행)
    @LoginRequired(requireHost = true)
    @PostMapping
    @Operation(summary = "행사 주최 API")
    public ResponseEntity<Void> createEvent(@RequestBody @Valid EventsCreateEventRequestCommand eventsCreateEventRequestCommand) {

        Long hostId = SessionContextHolder.getContext().getUserId();

        this.eventsService.createEvent(
            hostId, eventsCreateEventRequestCommand.title(), eventsCreateEventRequestCommand.imageUrls(), eventsCreateEventRequestCommand.category(),
            eventsCreateEventRequestCommand.eventStartAt(), eventsCreateEventRequestCommand.eventEndAt(),
            eventsCreateEventRequestCommand.participantLimitPolicy(), eventsCreateEventRequestCommand.maxParticipants(),
            eventsCreateEventRequestCommand.location(), eventsCreateEventRequestCommand.description(), eventsCreateEventRequestCommand.applyStartAt(),
            eventsCreateEventRequestCommand.applyEndAt());

        return ResponseEntity.status(201).build();
    }
}