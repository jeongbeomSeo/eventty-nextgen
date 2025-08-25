package com.eventty.eventtynextgen.base.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventsErrorType implements ErrorType {

    ILLEGAL_EVENT_END_BEFORE_START("ILLEGAL_EVENT_END_BEFORE_START", "행사 종료 시간은 행사 시작 시간 이후여야 합니다."),
    ILLEGAL_EVENT_IMAGE("ILLEGAL_EVENT_IMAGE", "행사 이미지 원본 그대로 데이터베이스에 저장할 수 없습니다. S3와 같은 저장소에 저장 후 URL을 담아주세요."),
    ILLEGAL_MAX_PARTICIPANTS("ILLEGAL_MAX_PARTICIPANTS", "행사 참여 인원 제한 정책이 '인원 제한 없음'이 아닌 경우, 최대 참여 인원은 1명 이상의 양수여야 합니다.");


    private final String code;
    private final String msg;
}
