package com.eventty.eventtynextgen.events.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventParticipantLimitPolicyType {

    LIMITED("참여 인원 제한 있음"),
    UNLIMITED("참여 인원 제한 없음");

    private final String name;
}
