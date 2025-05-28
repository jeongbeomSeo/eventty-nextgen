package com.eventty.eventtynextgen.events.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {

    STARTUP("창업"),
    IT_PROGRAMMING("IT/프로그래밍"),
    LIFE("라이프"),
    ECONOMY_FINANCE("경제/금융"),
    MANAGEMENT("경영"),
    HUMANITIES_SOCIAL("인문/사회"),
    ART("예술"),
    MARKETING("마케팅"),
    CAREER("커리어"),
    SCIENCE_TECH("과학기술"),
    DESIGN_VIDEO("디자인/영상"),
    MEDICAL("의료/의학"),
    EVENT_PLANNING("행사 기획"),
    TOURISM_TRAVEL("관광/여행"),
    ETC("기타");

    private final String name;
}